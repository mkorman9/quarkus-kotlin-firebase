package com.github.mkorman9

import com.github.mkorman9.firebase.auth.FirebaseAuthentication
import io.quarkus.security.Authenticated
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(value = [])
class HelloWorldResource {
    @GET
    fun getHelloWorld() = mapOf(
        Pair("hello", "world")
    )

    @GET
    @Path("/auth")
    @Authenticated
    fun getAuthenticated(@Context authentication: FirebaseAuthentication): Map<String, String> {
        return mapOf(
            Pair("user", authentication.uid)
        )
    }
}
