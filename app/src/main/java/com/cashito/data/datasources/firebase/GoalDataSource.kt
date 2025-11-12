package com.cashito.data.datasources.firebase

import android.util.Log
import com.cashito.data.dto.GoalDto
import com.google.firebase.auth.FirebaseAuth
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

    suspend fun addGoal(goalDto: GoalDto) {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("GoalDataSource", "Error: User not authenticated.")
            throw IllegalStateException("Usuario no autenticado para crear meta")
        }

        goalDto.userId = userId
        firestore.collection("Usuarios").document(userId).collection("Metas")
            .add(goalDto)
            .await()
    }

    fun observeGoals(): Flow<List<GoalDto>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("GoalDataSource", "Error: User not authenticated.")
            close(IllegalStateException("Usuario no autenticado para observar metas"))
            return@callbackFlow
        }

        val listenerRegistration = firestore.collection("Usuarios")
            .document(userId)
            .collection("Metas")
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("GoalDataSource", "Error listening to goals snapshot.", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val goals = snapshot.documents.mapNotNull { document ->
                        val goal = document.toObject(GoalDto::class.java)
                        goal?.id = document.id
                        goal
                    }
                    trySend(goals) // Emite la nueva lista de metas
                }
            }
        
        // Se llama cuando el Flow es cancelado
        awaitClose { listenerRegistration.remove() }
    }
}
