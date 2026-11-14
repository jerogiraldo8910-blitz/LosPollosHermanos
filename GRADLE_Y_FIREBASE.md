# Configuración de Gradle y Firebase

## 1-2. Gradle: YA ESTÁ HECHO en este proyecto (solo es referencia si lo copias a otro)

### build.gradle.kts (Project)

```kotlin
id("com.google.gms.google-services") version "4.4.3" apply false
```

## 2. app/build.gradle.kts (:app)

En `plugins { }` agrega (¡sin repetir `com.android.application`, ese es el error de la diapositiva 18!):

```kotlin
id("com.google.gms.google-services")
```

Dentro de `android { }` agrega:

```kotlin
buildFeatures {
    viewBinding = true
}
```

Y en `dependencies { }`:

```kotlin
implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-database")
implementation("androidx.recyclerview:recyclerview:1.3.2")
```

(La librería Material ya viene en el proyecto "Empty Views Activity".)

Luego: **Sync Now**.

## 3. Firebase (consola)

1. Crea el proyecto y registra la app Android con el MISMO package: `com.example.lospolloshermanos`
   (o el que uses; si es otro, cambia el `package` en todos los .kt y los `import ...databinding` / `R`).
2. Descarga `google-services.json` y ponlo en la carpeta `app/`.
3. Authentication → Sign-in method → activa **Correo/Contraseña**.
4. Realtime Database → Crear base de datos.
5. Pestaña **Reglas** → pega el contenido de `firebase_rules.json` → Publicar.
6. Authentication → Users → **Add user** y crea la cuenta del admin
   (`admin@lospollos.com` o el correo que pongas en `Constantes.ADMIN_CORREOS`).
   Hazlo ANTES de entregar la app: así nadie más puede registrar ese correo.
   Si cambias el correo, cámbialo también en `firebase_rules.json`.
