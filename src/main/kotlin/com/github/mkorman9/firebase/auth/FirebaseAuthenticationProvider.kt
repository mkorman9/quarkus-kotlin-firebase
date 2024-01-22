package com.github.mkorman9.firebase.auth

import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.inject.Produces
import jakarta.ws.rs.core.Context

@RequestScoped
class FirebaseAuthenticationProvider {
    @Produces
    fun provideFirebaseAuthentication(@Context securityIdentity: SecurityIdentity?): FirebaseAuthentication? {
        if (securityIdentity == null) {
            return null;
        }

        return securityIdentity.principal as? FirebaseAuthentication
    }
}
