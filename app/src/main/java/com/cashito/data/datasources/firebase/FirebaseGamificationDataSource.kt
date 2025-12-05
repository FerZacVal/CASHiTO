package com.cashito.data.datasources.firebase

import android.util.Log
import com.cashito.data.dto.RewardDto
import com.cashito.data.dto.WeeklyChallengeDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseGamificationDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val TAG = "GamificationDS"

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    }

    suspend fun getWeeklyChallenge(weekId: String): WeeklyChallengeDto? {
        val userId = getUserId()
        return try {
            val document = firestore.collection("Usuarios")
                .document(userId)
                .collection("challenge_progress")
                .document(weekId)
                .get()
                .await()
            
            if (document.exists()) {
                document.toObject(WeeklyChallengeDto::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting weekly challenge", e)
            null
        }
    }
    
    fun observeWeeklyChallenge(weekId: String): Flow<WeeklyChallengeDto?> = callbackFlow {
         val userId = auth.currentUser?.uid
         if (userId == null) {
             close(IllegalStateException("User not authenticated"))
             return@callbackFlow
         }

        val docRef = firestore.collection("Usuarios")
            .document(userId)
            .collection("challenge_progress")
            .document(weekId)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error observing weekly challenge", error)
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(WeeklyChallengeDto::class.java))
            } else {
                trySend(null)
            }
        }

        awaitClose { listener.remove() }
    }

    suspend fun createOrUpdateChallenge(weekId: String, currentAmountDelta: Double, defaultTarget: Double) {
        val userId = getUserId()
        val docRef = firestore.collection("Usuarios")
            .document(userId)
            .collection("challenge_progress")
            .document(weekId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            
            if (!snapshot.exists()) {
                // Create new
                val newChallenge = WeeklyChallengeDto(
                    weekId = weekId,
                    currentAmount = currentAmountDelta,
                    targetAmount = defaultTarget,
                    isCompleted = currentAmountDelta >= defaultTarget,
                    isRewardClaimed = false
                )
                transaction.set(docRef, newChallenge)
            } else {
                // Update existing
                val currentAmount = snapshot.getDouble("currentAmount") ?: 0.0
                val targetAmount = snapshot.getDouble("targetAmount") ?: defaultTarget
                
                val newAmount = currentAmount + currentAmountDelta
                val isCompleted = newAmount >= targetAmount
                
                transaction.update(docRef, mapOf(
                    "currentAmount" to newAmount,
                    "isCompleted" to isCompleted,
                    "lastUpdated" to FieldValue.serverTimestamp()
                ))
            }
        }.await()
    }
    
    // Alternative simple create if it doesn't exist (for when opening dashboard)
    suspend fun initChallengeIfNotExists(weekId: String, defaultTarget: Double) {
        val userId = getUserId()
        val docRef = firestore.collection("Usuarios")
            .document(userId)
            .collection("challenge_progress")
            .document(weekId)

        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
             val newChallenge = WeeklyChallengeDto(
                weekId = weekId,
                currentAmount = 0.0,
                targetAmount = defaultTarget,
                isCompleted = false,
                isRewardClaimed = false
            )
            docRef.set(newChallenge).await()
        }
    }

    suspend fun claimReward(weekId: String, reward: RewardDto) {
        val userId = getUserId()
        val challengeRef = firestore.collection("Usuarios")
            .document(userId)
            .collection("challenge_progress")
            .document(weekId)
            
        val rewardRef = firestore.collection("Usuarios")
            .document(userId)
            .collection("rewards")
            .document(reward.id)

        firestore.runTransaction { transaction ->
            val challengeSnapshot = transaction.get(challengeRef)
            val isRewardClaimed = challengeSnapshot.getBoolean("isRewardClaimed") ?: false
            
            if (isRewardClaimed) {
                throw IllegalStateException("Reward already claimed for this week")
            }
            
            // Mark challenge as claimed
            transaction.update(challengeRef, "isRewardClaimed", true)
            
            // Add reward to inventory
            transaction.set(rewardRef, reward)
        }.await()
    }

    fun observeUserRewards(): Flow<List<RewardDto>> = callbackFlow {
        val userId = auth.currentUser?.uid
         if (userId == null) {
             close(IllegalStateException("User not authenticated"))
             return@callbackFlow
         }

        val collectionRef = firestore.collection("Usuarios")
            .document(userId)
            .collection("rewards")
            //.whereEqualTo("isUsed", false) // Optional: filter used rewards
            
        val listener = collectionRef.addSnapshotListener { snapshot, error ->
             if (error != null) {
                Log.e(TAG, "Error observing rewards", error)
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val rewards = snapshot.documents.mapNotNull { it.toObject(RewardDto::class.java) }
                trySend(rewards)
            } else {
                trySend(emptyList())
            }
        }
        
        awaitClose { listener.remove() }
    }

    suspend fun markRewardAsUsed(rewardId: String, goalId: String?) {
        val userId = getUserId()
        val rewardRef = firestore.collection("Usuarios")
            .document(userId)
            .collection("rewards")
            .document(rewardId)
            
        rewardRef.update(mapOf(
            "isUsed" to true,
            "appliedToGoalId" to goalId
        )).await()
    }
    
    suspend fun getGlobalConfigWeeklyTarget(): Double {
        return try {
            val snapshot = firestore.collection("config")
                .document("gamification")
                .get()
                .await()
                
            if (snapshot.exists()) {
                snapshot.getDouble("weeklyTargetAmount") ?: 200.0
            } else {
                200.0
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting global config", e)
            200.0
        }
    }
}
