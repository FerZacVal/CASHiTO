package com.cashito.domain.usecases.income

import com.cashito.domain.entities.income.Income
import com.cashito.domain.repositories.goal.GoalRepository
import com.cashito.domain.repositories.income.IncomeRepository
import kotlinx.coroutines.flow.first

/**
 * Caso de uso para añadir un ingreso.
 * Esta clase ahora tiene una lógica de negocio más rica:
 * 1.  Guarda la transacción de ingreso.
 * 2.  Si el ingreso está asociado a una meta (`goalId` no es nulo), actualiza
 *     el monto ahorrado (`savedAmount`) de la meta correspondiente para mantener
 *     la consistencia de los datos.
 */
class AddIncomeUseCase(
    private val incomeRepository: IncomeRepository,
    private val goalRepository: GoalRepository // ARREGLADO: Se añade la dependencia del repositorio de metas.
) {
    suspend operator fun invoke(income: Income) {
        // Paso 1: Siempre guardar la transacción del ingreso.
        incomeRepository.addIncome(income)

        // Paso 2: Si el ingreso está destinado a una meta, actualizar el progreso de esa meta.
        if (income.goalId != null) {
            // Obtenemos la meta actual desde el repositorio. Usamos .first() para obtener el valor más reciente del Flow.
            val goal = goalRepository.getGoalById(income.goalId).first()

            if (goal != null) {
                // Calculamos el nuevo monto ahorrado.
                val newSavedAmount = goal.savedAmount + income.amount
                // Creamos una copia del objeto de la meta con el monto actualizado.
                val updatedGoal = goal.copy(savedAmount = newSavedAmount)
                // Guardamos la meta actualizada en la base de datos.
                goalRepository.updateGoal(updatedGoal)
            }
        }
    }
}
