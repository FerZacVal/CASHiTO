package com.cashito.data.mappers

import com.cashito.data.dto.ExpenseDto
import com.cashito.domain.entities.expense.Expense

interface ExpenseMapper {
    fun toEntity(dto: ExpenseDto): Expense
    fun toDto(entity: Expense): ExpenseDto
}


