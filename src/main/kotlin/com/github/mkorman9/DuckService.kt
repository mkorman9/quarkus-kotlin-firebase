package com.github.mkorman9

import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.FirestoreClient
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes

data class Duck(
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
            .set(Duck(
                name = "Beatrice",
                favoriteFood = "Water with ice"
            ))
            .get()

        val ducks = firestore.collection("ducks")
            .get()
            .get()
            .toObjects(Duck::class.java)

        println(ducks)
    }
}
