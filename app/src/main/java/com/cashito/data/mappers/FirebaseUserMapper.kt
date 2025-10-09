package com.cashito.data.mappers

import com.cashito.domain.entities.auth.User
import com.google.firebase.auth.FirebaseUser

object FirebaseUserMapper {
    fun map(firebaseUser: FirebaseUser?): User? {
        return firebaseUser?.let {
            User(
                uid = it.uid,
                email = it.email,
                displayName = it.displayName
            )
        }
    }
}
