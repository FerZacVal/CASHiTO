package com.cashito.data.datasources.firebase

import android.util.Log
import com.cashito.data.dto.TransactionDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Se encarga de la comunicación directa con Firestore para escribir y leer datos de transacciones.
 */
class FirebaseTransactionDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    init {
        Log.d("FlowDebug", "SUCCESS: FirebaseTransactionDataSource instance created.")
    }

    suspend fun addTransaction(transactionDto: TransactionDto) {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("FlowDebug", "DataSource: FATAL - User is not authenticated. Cannot add transaction.")
            throw IllegalStateException("Usuario no autenticado")
        }

        transactionDto.userId = userId

        Log.d("FlowDebug", "DataSource: Adding document to Firestore for user $userId.")
        firestore.collection("Usuarios").document(userId).collection("Transacciones").add(transactionDto).await()
        Log.d("FlowDebug", "DataSource: Document successfully added to Firestore.")
    }

    suspend fun getTransactions(): List<TransactionDto> {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("FlowDebug", "DataSource: User is not authenticated. Cannot get transactions.")
            return emptyList()
        }

        return try {
            val snapshot = firestore.collection("Usuarios")
                .document(userId)
                .collection("Transacciones")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                val transaction = document.toObject(TransactionDto::class.java)
                transaction?.id = document.id
                transaction
            }
        } catch (e: Exception) {
            Log.e("FlowDebug", "DataSource: Error getting transactions from Firestore.", e)
            emptyList()
        }
    }

    suspend fun getTransactionById(transactionId: String): TransactionDto? {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("FlowDebug", "DataSource: User is not authenticated. Cannot get transaction by ID.")
            return null
        }

        return try {
            val document = firestore.collection("Usuarios")
                .document(userId)
                .collection("Transacciones")
                .document(transactionId)
                .get()
                .await()
            
            if (document.exists()) {
                val transaction = document.toObject(TransactionDto::class.java)
                transaction?.id = document.id
                transaction
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FlowDebug", "DataSource: Error getting transaction by ID.", e)
            null
        }
    }

    suspend fun updateTransaction(transactionId: String, updatedData: Map<String, Any>) {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("FlowDebug", "DataSource: FATAL - User is not authenticated. Cannot update transaction.")
            throw IllegalStateException("Usuario no autenticado")
        }

        Log.d("FlowDebug", "DataSource: Updating document $transactionId for user $userId.")
        firestore.collection("Usuarios").document(userId).collection("Transacciones").document(transactionId)
            .update(updatedData).await()
        Log.d("FlowDebug", "DataSource: Document successfully updated.")
    }

    suspend fun deleteTransaction(transactionId: String) {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("FlowDebug", "DataSource: FATAL - User is not authenticated. Cannot delete transaction.")
            throw IllegalStateException("Usuario no autenticado")
        }

        Log.d("FlowDebug", "DataSource: Deleting document $transactionId for user $userId.")
        firestore.collection("Usuarios").document(userId).collection("Transacciones").document(transactionId).delete().await()
        Log.d("FlowDebug", "DataSource: Document successfully deleted.")
    }
}
