package com.cashito.data.datasources.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(private val firebaseAuth: FirebaseAuth) {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun signIn(email: String, password: String): FirebaseUser {
        return firebaseAuth.signInWithEmailAndPassword(email, password).await().user
            ?: throw IllegalStateException("FirebaseUser is null after sign in")
    }

    suspend fun createUser(email: String, password: String): FirebaseUser {
        return firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
            ?: throw IllegalStateException("FirebaseUser is null after create user")
    }

    suspend fun saveUserProfile(user: FirebaseUser, nombre: String) {
        val userProfile = hashMapOf(
            "userId" to user.uid,
            "nombre" to nombre,
            "email" to user.email,
            "metodoAuth" to "email"
        )
        firestore.collection("Usuarios").document(user.uid)
            .set(userProfile)
            .await()
    }

    suspend fun getUserProfile(): Map<String, Any>? {
        val userId = firebaseAuth.currentUser?.uid ?: return null
        val document = firestore.collection("Usuarios").document(userId).get().await()
        return if (document.exists()) document.data else null
    }

    fun getAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
