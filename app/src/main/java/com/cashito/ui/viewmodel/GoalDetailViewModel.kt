package com.cashito.ui.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cashito.domain.entities.goal.Goal
import com.cashito.domain.entities.transaction.Transaction
import com.cashito.domain.usecases.goal.DeleteGoalUseCase
import com.cashito.domain.usecases.goal.GetGoalByIdUseCase
import com.cashito.domain.usecases.transaction.GetTransactionsForGoalUseCase
import com.cashito.ui.theme.primaryLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

// --- ARQUITECTURA DE ESTADO DE LA UI ---
// Estas clases data definen la "forma" de los datos que la UI puede mostrar.
// Son modelos inmutables que representan todo lo que la pantalla necesita saber para dibujarse.

/**
 * Representa una meta con sus datos formateados y listos para ser mostrados en la UI.
 * Es una versión "amigable para la vista" de la entidad de dominio `Goal`.
 */
data class GoalDetail(
    val id: String,
    val title: String,         // Título de la meta
    val savedAmount: String,   // Monto ahorrado, ya formateado como texto (ej: "1,250.50")
    val targetAmount: String,  // Monto objetivo, también formateado
    val progress: Float,       // Progreso como un valor entre 0.0 y 1.0 para la barra circular
    val icon: String,
    val color: Color,          // Color específico para la UI de Compose
    val targetDate: String     // Fecha objetivo, formateada como texto
)

/**
 * Representa una transacción (aporte) a una meta, con sus datos listos para la UI.
 */
data class GoalTransaction(
    val id: String,
    val title: String,         // Descripción de la transacción
    val subtitle: String,      // Información secundaria (ej: fecha y hora)
    val amount: String,        // Monto de la transacción, formateado
    val amountColor: Color,    // Color del monto (ej: verde para ingresos)
    val icon: String,
    val color: Color
)

/**
 * El estado completo de la pantalla `GoalDetailScreen`. Contiene toda la información
 * que la UI necesita en un momento dado, incluyendo el estado de carga, errores, y los datos a mostrar.
 * La UI observa los cambios en este objeto para recomponerse.
 */
data class GoalDetailUiState(
    val goal: GoalDetail? = null,                   // Los detalles de la meta a mostrar (nulo si aún no carga)
    val transactions: List<GoalTransaction> = emptyList(), // La lista de aportes a la meta
    val isRecurringEnabled: Boolean = true,         // Estado del switch de ahorro recurrente (funcionalidad futura)
    val showMenu: Boolean = false,                  // Controla la visibilidad del menú desplegable (editar/eliminar)
    val isLoading: Boolean = true,                  // TRUE si estamos cargando datos, para mostrar una animación
    val goalDeleted: Boolean = false,               // Se pone en TRUE después de que la meta se elimina, para que la UI navegue hacia atrás
    val error: String? = null                       // Contiene un mensaje de error si algo falla, para depuración o para mostrar al usuario
)

// --- VIEWMODEL ---

/**
 * Orquesta la lógica de la pantalla de detalle de una meta. Su responsabilidad es:
 * 1. Obtener los datos desde la capa de dominio (a través de los Casos de Uso).
 * 2. Manejar el estado de la UI (carga, error, datos).
 * 3. Exponer el estado a la UI para que esta lo observe.
 * 4. Recibir eventos de la UI (clics en botones) y ejecutar la lógica de negocio correspondiente.
 */
class GoalDetailViewModel(
    // Inyección de dependencias a través del constructor (principio de Inversión de Dependencias).
    // Koin se encargará de proveer estas instancias.
    savedStateHandle: SavedStateHandle, // Permite acceder a los argumentos de navegación, como el ID de la meta.
    private val getGoalByIdUseCase: GetGoalByIdUseCase, // Caso de Uso para obtener los detalles de una meta específica.
    private val getTransactionsForGoalUseCase: GetTransactionsForGoalUseCase, // Caso de Uso para obtener los aportes a esa meta.
    private val deleteGoalUseCase: DeleteGoalUseCase    // Caso de Uso para eliminar la meta.
) : ViewModel() {

    // Obtenemos el ID de la meta de los argumentos de navegación. `checkNotNull` asegura que siempre esté presente.
    private val goalId: String = checkNotNull(savedStateHandle["goalId"])

    // `_uiState` es el estado interno y mutable. Solo el ViewModel puede modificarlo.
    private val _uiState = MutableStateFlow(GoalDetailUiState()) // Se inicializa con el estado por defecto (isLoading = true)
    
    // `uiState` es la versión pública e inmutable (StateFlow) del estado. La UI lo observa pero no puede modificarlo.
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()

    /**
     * El bloque `init` se ejecuta cuando se crea una instancia del ViewModel.
     * Es el lugar perfecto para iniciar la carga de datos.
     */
    init {
        loadGoalDetails()
    }

    /**
     * Orquesta la carga de los datos de la meta y sus transacciones desde el repositorio.
     */
    private fun loadGoalDetails() {
        // Lanzamos una corrutina en el `viewModelScope`. Esto asegura que la operación se cancele
        // automáticamente si el ViewModel es destruido (ej: el usuario sale de la pantalla).
        viewModelScope.launch {
            // 1. Inmediatamente actualizamos el estado para indicar que la carga ha comenzado.
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // 2. Obtenemos los Flows de datos de los casos de uso. Un Flow es como un "chorro" de datos
                //    que puede emitir valores a lo largo del tiempo (ideal para datos en tiempo real).
                val goalFlow = getGoalByIdUseCase(goalId)
                val transactionsFlow = getTransactionsForGoalUseCase(goalId)

                // 3. Usamos `combine` para fusionar los dos flows. El bloque de código dentro de `combine` se ejecutará
                //    cada vez que CUALQUIERA de los dos flows emita un nuevo valor. Recibe el último valor de cada uno.
                combine(goalFlow, transactionsFlow) { goal, transactions ->
                    // Si por alguna razón la meta no se encuentra (ej: fue eliminada por otro dispositivo),
                    // devolvemos un estado de error y paramos la carga.
                    if (goal == null) {
                        return@combine GoalDetailUiState(isLoading = false, error = "Meta no encontrada")
                    }
                    
                    // Si todo va bien, mapeamos las entidades de dominio (`Goal`, `Transaction`) a los modelos de UI
                    // (`GoalDetail`, `GoalTransaction`) y creamos un nuevo estado con los datos listos y la carga finalizada.
                    GoalDetailUiState(
                        goal = goal.toGoalDetail(),
                        transactions = transactions.map { it.toGoalTransaction() },
                        isLoading = false
                    )
                }.collect { newState ->
                    // 4. `collect` se suscribe al Flow combinado y recibe cada nuevo estado que este produce.
                    //    Actualizamos el `_uiState` con el nuevo estado completo.
                    _uiState.value = newState
                }
            } catch (e: CancellationException) {
                // ARREGLADO: Ignoramos la CancellationException. Esto es normal y esperado cuando el usuario
                // sale de la pantalla y el viewModelScope cancela la corrutina.
                // Se relanza para asegurar que la corrutina se detenga por completo.
                throw e
            } catch (e: Exception) {
                // 5. ¡MANEJO DE ERRORES CRÍTICO! Si algo falla en CUALQUIER punto de la carga (ej: sin internet,
                //    o el famoso error de índice de Firestore), la excepción se captura aquí.
                Log.e("GoalDetailViewModel", "Error al cargar los detalles de la meta", e)
                // Actualizamos el estado para detener la animación de carga y registrar el error.
                _uiState.update { it.copy(isLoading = false, error = "Fallo al cargar los detalles: ${e.message}") }
            }
        }
    }

    /**
     * Inicia el proceso para eliminar la meta actual.
     * Esta función es llamada desde la UI cuando el usuario hace clic en "Eliminar".
     */
    fun deleteGoal() {
        viewModelScope.launch {
            try {
                deleteGoalUseCase(goalId) // Llama al caso de uso para ejecutar la lógica de borrado.
                _uiState.update { it.copy(goalDeleted = true) } // Actualiza el estado para notificar a la UI.
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Fallo al eliminar la meta: ${e.message}") }
            }
        }
    }

    // --- Manejadores de Eventos de la UI ---

    /**
     * Actualiza el estado cuando el switch de ahorro recurrente cambia.
     */
    fun onRecurringChanged(isEnabled: Boolean) {
        _uiState.update { it.copy(isRecurringEnabled = isEnabled) }
    }

    /**
     * Muestra u oculta el menú de opciones.
     */
    fun onShowMenu(show: Boolean) {
        _uiState.update { it.copy(showMenu = show) }
    }
}

// --- MAPPERS ---
// Funciones de extensión privadas para mantener la lógica de transformación de datos encapsulada y ordenada.
// Convierten los modelos de la capa de Dominio a los modelos de la capa de UI.

/**
 * Convierte una entidad de dominio `Goal` a un objeto `GoalDetail` para la UI.
 */
private fun Goal.toGoalDetail(): GoalDetail {
    val progress = if (this.targetAmount > 0) (this.savedAmount / this.targetAmount).toFloat() else 0f
    return GoalDetail(
        id = this.id,
        title = this.name,
        savedAmount = String.format("%,.2f", this.savedAmount),
        targetAmount = String.format("%,.2f", this.targetAmount),
        progress = progress,
        icon = this.icon,
        color = Color(android.graphics.Color.parseColor(this.colorHex)),
        targetDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(this.targetDate)
    )
}

/**
 * Convierte una entidad de dominio `Transaction` a un objeto `GoalTransaction` para la UI.
 */
private fun Transaction.toGoalTransaction(): GoalTransaction {
    return GoalTransaction(
        id = this.id,
        title = this.description,
        subtitle = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(this.date),
        amount = "+S/ ${String.format("%,.2f", this.amount)}",
        amountColor = primaryLight, // TODO: Mapear el color según el tipo de transacción (ingreso/gasto)
        icon = "💰", // TODO: Mapear el ícono según la categoría de la transacción
        color = primaryLight
    )
}
