package com.github.mkorman9.firebase.auth

import com.google.firebase.auth.FirebaseToken
import java.security.Principal

data class FirebaseAuthentication(
    val uid: String,
    val issuer: String,
    val tenantId: String?,
    val displayName: String?,
    val picture: String?,
    val email: String?,
    val isEmailVerified: Boolean = false,
    val claims: Map<String, Any> = mapOf()
) : Principal {
    override fun getName(): String = uid

    companion object {
        fun from(firebaseToken: FirebaseToken): FirebaseAuthentication {
            return FirebaseAuthentication(
                uid = firebaseToken.uid,
                issuer = firebaseToken.issuer,
                tenantId = firebaseToken.tenantId,
                displayName = firebaseToken.name,
                picture = firebaseToken.picture,
                email = firebaseToken.email,
                isEmailVerified = firebaseToken.isEmailVerified,
                claims = firebaseToken.claims
            )
        }
    }
}
