package com.github.mkorman9.firebase.auth

import com.fasterxml.jackson.core.JsonProcessingException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class FirebaseAuthenticationService(
    val firebaseApp: FirebaseApp
) {
    fun verifyToken(token: String): FirebaseAuthentication {
        try {
            val firebaseToken = FirebaseAuth.getInstance(firebaseApp).verifyIdToken(token)
            return FirebaseAuthentication.from(firebaseToken)
        } catch (e: FirebaseAuthException) {
            if (isClientException(e)) {
                throw RuntimeException(e)
            } else {
                throw AuthenticationServerException(e)
            }
        }
    }

    private fun isClientException(e: FirebaseAuthException): Boolean {
        return (
            e.cause == null // thrown on expired JWT token
                || e.cause is IllegalArgumentException // thrown on malformed JWT token
                || e.cause is JsonProcessingException // thrown on malformed JWT token signature
        )
    }
}

class AuthenticationServerException(cause: Throwable) : RuntimeException(cause)
