package com.cashito.data.datasources.firebase

import android.util.Log
import com.cashito.data.dto.GoalDto
import com.cashito.data.dto.TransactionDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GoalDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    private val goalsCollection
        get() = firestore.collection("Usuarios").document(userId).collection("Metas")

    suspend fun addGoal(goalDto: GoalDto) {
        goalDto.userId = userId
        goalsCollection.add(goalDto).await()
    }

    fun observeGoals(): Flow<List<GoalDto>> = callbackFlow {
        val listenerRegistration = goalsCollection
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val goals = snapshot.documents.mapNotNull { document ->
                        document.toObject(GoalDto::class.java)?.apply { id = document.id }
                    }
                    trySend(goals)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    fun observeGoalById(id: String): Flow<GoalDto?> = callbackFlow {
        val docRef = goalsCollection.document(id)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val goal = snapshot.toObject(GoalDto::class.java)?.apply { this.id = snapshot.id }
                trySend(goal)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun deleteGoal(id: String) {
        goalsCollection.document(id).delete().await()
    }

    suspend fun updateGoal(goalDto: GoalDto) {
        goalsCollection.document(goalDto.id).set(goalDto).await()
    }

    /**
     * Ejecuta una transacción atómica en Firestore para retirar fondos de una meta y registrar el movimiento.
     *
     * @param goalId El ID de la meta de la cual se retirarán los fondos.
     * @param amount El monto positivo a retirar.
     * @param transactionDto El objeto DTO de la transacción que se registrará (tipo gasto/ajuste).
     */
    suspend fun withdrawFromGoal(goalId: String, amount: Double, transactionDto: TransactionDto) {
        val goalRef = goalsCollection.document(goalId)
        val transactionRef = firestore.collection("Usuarios").document(userId).collection("Transacciones").document()

        transactionDto.userId = userId

        firestore.runTransaction { transaction ->
            val goalSnapshot = transaction.get(goalRef)
            
            // 1. Validaciones
            if (!goalSnapshot.exists()) {
                throw IllegalStateException("La meta no existe")
            }

            // Usamos FieldPath para acceder a propiedades nested si fuera necesario, pero aquí es directo.
            val currentSavedAmount = goalSnapshot.getDouble("savedAmount") ?: 0.0
            val newSavedAmount = currentSavedAmount - amount

            // 2. Actualizar la meta: Reducir saldo y cancelar boost si existe
            val updates = mutableMapOf<String, Any>(
                "savedAmount" to newSavedAmount,
                "activeBoostId" to FieldValue.delete(),
                "activeBoostApr" to FieldValue.delete(),
                "boostExpiryDate" to FieldValue.delete(),
                "activeBoostProfit" to FieldValue.delete()
            )
            transaction.update(goalRef, updates)

            // 3. Crear la transacción de retiro/ajuste
            // Aseguramos que la transacción tenga el ID generado
            transactionDto.id = transactionRef.id
            transaction.set(transactionRef, transactionDto)

        }.await()
    }
}
