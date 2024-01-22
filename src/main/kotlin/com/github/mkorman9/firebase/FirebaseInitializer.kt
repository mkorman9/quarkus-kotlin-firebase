package com.github.mkorman9.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FirestoreOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.internal.EmulatorCredentials
import com.google.firebase.internal.FirebaseProcessEnvironment
import io.quarkus.arc.profile.UnlessBuildProfile
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.IOException

@ApplicationScoped
@UnlessBuildProfile("test")
class FirebaseInitializer(
    @ConfigProperty(name = "firebase.emulator.enabled", defaultValue = "false")
    val emulatorEnabled: Boolean,
    @ConfigProperty(name = "firebase.emulator.project-id", defaultValue = "emulator-project")
    val emulatorProjectId: String,
    @ConfigProperty(name = "firebase.auth.emulator-url", defaultValue = "127.0.0.1:9099")
    val authEmulatorUrl: String,
    @ConfigProperty(name = "firebase.firestore.emulator-url", defaultValue = "127.0.0.1:9100")
    val firestoreEmulatorUrl: String,
    @ConfigProperty(name = "firebase.credentials", defaultValue = "{}")
    val credentialsContent: String,
    @ConfigProperty(name = "firebase.credentials.path", defaultValue = "serviceAccountKey.json")
    val credentialsPath: String,
    val log: Logger
) {
    @Produces
    @Singleton
    fun firebaseApp(): FirebaseApp {
        // delete default app instance to prevent problems with hot reloads
        try {
            FirebaseApp.getInstance().delete()
        } catch (e: IllegalStateException) {
            // ignore
        }

        val firebaseOptions = createFirebaseOptions()
        return FirebaseApp.initializeApp(firebaseOptions)
    }

    private fun createFirebaseOptions(): FirebaseOptions {
        if (emulatorEnabled) {
            FirebaseProcessEnvironment.setenv("FIREBASE_AUTH_EMULATOR_HOST", authEmulatorUrl)
            log.info("Firebase integration is running in emulator mode")

            return FirebaseOptions.builder()
                .setProjectId(emulatorProjectId)
                .setCredentials(EmulatorCredentials())
                .setFirestoreOptions(
                    FirestoreOptions.newBuilder()
                        .setEmulatorHost(firestoreEmulatorUrl)
                        .build()
                )
                .build()
        } else {
            log.info("Firebase integration is running in production mode")

            val credentials = resolveCredentials()
            return FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()
        }
    }

    private fun resolveCredentials(): GoogleCredentials {
        // try environment variable
        try {
            ByteArrayInputStream(credentialsContent.toByteArray()).use { credentialsStream ->
                return GoogleCredentials.fromStream(credentialsStream)
            }
        } catch (e: IOException) {
            // ignore
        }

        // try file
        try {
            FileInputStream(credentialsPath).use { credentialsStream ->
                return GoogleCredentials.fromStream(credentialsStream)
            }
        } catch (e: IOException) {
            // ignore
        }

        // try platform-default credentials
        try {
            return GoogleCredentials.getApplicationDefault()
        } catch (e: IOException) {
            // ignore
        }

        throw IllegalStateException("Unable to resolve Firebase credentials")
    }
}
