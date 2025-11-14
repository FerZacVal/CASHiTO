package com.cashito.data.datasources.firebase

import com.cashito.data.dto.CategoryDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CategoryDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Usuario no autenticado")

    private val categoriesCollection
        get() = firestore.collection("Usuarios").document(userId).collection("Categorias")

    suspend fun addCategory(categoryDto: CategoryDto) {
        categoriesCollection.add(categoryDto.copy(userId = this.userId)).await()
    }

    fun observeCategories(): Flow<List<CategoryDto>> = callbackFlow {
        val listener = categoriesCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val dtoList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CategoryDto::class.java)?.apply { id = doc.id }
                    }
                    trySend(dtoList)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun getCategoryById(categoryId: String): CategoryDto? {
        val document = categoriesCollection.document(categoryId).get().await()
        return document.toObject(CategoryDto::class.java)?.apply { id = document.id }
    }

    suspend fun updateCategory(categoryId: String, updatedCategory: CategoryDto) { // ACTUALIZADO
        categoriesCollection.document(categoryId).set(updatedCategory).await()
    }
}
