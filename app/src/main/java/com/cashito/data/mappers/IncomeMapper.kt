package com.cashito.data.mappers

import com.cashito.data.dto.TransactionDto
import com.cashito.domain.entities.category.Category
import com.cashito.domain.entities.income.Income

/**
 * Objeto singleton que centraliza la lógica para mapear (convertir) objetos
 * entre la entidad de dominio `Income` y el DTO `TransactionDto`.
 */
object IncomeMapper {

    /**
     * Convierte un DTO de la capa de datos a una entidad de dominio.
     * Esto se usará cuando leamos datos de Firestore.
     */
    fun toEntity(dto: TransactionDto): Income {
        return Income(
            id = dto.userId, // El ID de la transacción debería venir del documento de Firestore
            description = dto.description,
            amount = dto.amount,
            date = dto.date ?: java.util.Date(),
            category = Category(
                id = dto.categoryId,
                name = dto.categoryName,
                icon = dto.categoryIcon,
                color = ""
            )
        )
    }

    /**
     * Convierte una entidad de dominio `Income` a un DTO `TransactionDto`.
     * Esto se usa antes de escribir datos en Firestore.
     * Establece el tipo como "ingreso".
     */
    fun toDto(entity: Income, userId: String): TransactionDto {
        return TransactionDto(
            userId = userId,
            description = entity.description,
            amount = entity.amount,
            type = "ingreso",
            categoryId = entity.category?.id ?: "",
            categoryName = entity.category?.name ?: "Sin categoría",
            categoryIcon = entity.category?.icon ?: ""
        )
    }
}
