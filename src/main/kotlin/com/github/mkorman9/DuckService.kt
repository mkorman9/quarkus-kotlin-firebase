package com.github.mkorman9

import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.FirestoreClient
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import java.util.concurrent.TimeUnit

data class DuckDocument(
    val name: String = "",
    val favoriteFood: String = ""
)

@ApplicationScoped
class DuckService(
    val firebaseApp: FirebaseApp
) {
    fun onStart(@Observes startupEvent: StartupEvent) {
        val firestore = FirestoreClient.getFirestore(firebaseApp)

        firestore.collection("ducks")
            .document("beatrice")
            .set(DuckDocument(
                name = "Beatrice",
                favoriteFood = "Water with ice"
            ))
            .get(5, TimeUnit.SECONDS)

        val ducks = firestore.collection("ducks")
            .get()
            .get(5, TimeUnit.SECONDS)
            .toObjects(DuckDocument::class.java)

        println(ducks)
    }
}
