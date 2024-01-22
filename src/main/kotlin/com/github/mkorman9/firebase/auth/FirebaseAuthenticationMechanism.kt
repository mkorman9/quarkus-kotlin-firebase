package com.github.mkorman9.firebase.auth

import io.quarkus.arc.profile.UnlessBuildProfile
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.IdentityProviderManager
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.AuthenticationRequest
import io.quarkus.security.identity.request.BaseAuthenticationRequest
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.quarkus.vertx.http.runtime.security.ChallengeData
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
@UnlessBuildProfile("test")
internal class FirebaseAuthenticationMechanism : HttpAuthenticationMechanism {
    override fun authenticate(
        context: RoutingContext,
        identityProviderManager: IdentityProviderManager
    ): Uni<SecurityIdentity> {
        val header = context.request().getHeader(AUTHORIZATION_HEADER)
        if (header == null || !header.startsWith(BEARER_TOKEN_TYPE)) {
            return Uni.createFrom().nullItem()
        }

        val token = header.substring(BEARER_TOKEN_TYPE_LENGTH).trim()
        return identityProviderManager.authenticate(FirebaseAuthenticationRequest(token))
            .onFailure().recoverWithItem { throwable: Throwable ->
                if (throwable is AuthenticationServerException) {
                    context.fail(throwable)
                }
                null
            }
    }

    override fun getCredentialTypes(): Set<Class<out AuthenticationRequest>> {
        return mutableSetOf(FirebaseAuthenticationRequest::class.java)
    }

    override fun getChallenge(context: RoutingContext): Uni<ChallengeData> {
        return Uni.createFrom().nullItem()
    }

    override fun sendChallenge(context: RoutingContext): Uni<Boolean> {
        return Uni.createFrom().item(false)
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_TOKEN_TYPE = "Bearer"
        private const val BEARER_TOKEN_TYPE_LENGTH = BEARER_TOKEN_TYPE.length
    }
}

@ApplicationScoped
@UnlessBuildProfile("test")
internal class FirebaseIdentityProvider(
    val firebaseAuthenticationService: FirebaseAuthenticationService
) : IdentityProvider<FirebaseAuthenticationRequest> {
    override fun authenticate(
        request: FirebaseAuthenticationRequest,
        context: AuthenticationRequestContext
    ): Uni<SecurityIdentity> {
        return context.runBlocking {
            val authentication = firebaseAuthenticationService.verifyToken(request.token)
            QuarkusSecurityIdentity.builder()
                .setPrincipal(authentication)
                .build()
        }
    }

    override fun getRequestType(): Class<FirebaseAuthenticationRequest> {
        return FirebaseAuthenticationRequest::class.java
    }
}

class FirebaseAuthenticationRequest(val token: String) : BaseAuthenticationRequest()
