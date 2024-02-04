package com.github.mkorman9.firebase.auth

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class FirebaseAuthenticationService(
    val firebaseApp: FirebaseApp
) {
    fun verifyToken(token: String): FirebaseAuthentication {
        val firebaseToken = FirebaseAuth.getInstance(firebaseApp).verifyIdToken(token)
        return FirebaseAuthentication.from(firebaseToken)
    }
}
