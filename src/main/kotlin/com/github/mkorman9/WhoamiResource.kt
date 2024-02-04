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
class WhoamiResource {
    @GET
    fun anonymous() = WhoamiResponse("anonymous")

    @GET
    @Path("/secured")
    @Authenticated
    fun getAuthenticated(@Context authentication: FirebaseAuthentication) =
        WhoamiResponse(authentication.uid)
}

data class WhoamiResponse(val user: String)
