
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
}
