# ComunicaPlusRS

Aplicación Android para personas con discapacidad auditiva. Permite escribir un mensaje y que el teléfono lo lea en voz alta, convertir en texto lo que dice otra persona y ubicar los teléfonos del usuario.

Proyecto de la asignatura Desarrollo de Aplicaciones Móviles (DSY2204) - DUOC UC.

## Pantallas

| Pantalla | Qué hace |
|---|---|
| Login | Inicio de sesión con Firebase Authentication (correo y contraseña). |
| Registro | Crea la cuenta y guarda el perfil y las preferencias de accesibilidad en Firestore. |
| Recuperar contraseña | Envía el correo de recuperación de Firebase. |
| Home (menú) | Acceso a Escribir, Hablar, Buscar dispositivo y Mi perfil. |
| Escribir | Lectura en voz alta (TextToSpeech), mensaje en pantalla completa, frases guardadas (CRUD) y envío por SMS a un contacto (ContentProvider de Contactos). |
| Hablar | Reconocimiento de voz a texto, tamaño de letra ajustable, vibración / alerta visual e historial en tabla (CRUD). |
| Buscar dispositivo | Fragment con vistas XML: geolocalización (FusedLocationProvider), dirección aproximada, lista de dispositivos, distancia y apertura en el mapa. |
| Mi perfil | Editar datos y preferencias, y eliminar la cuenta. |

También incluye un widget para la pantalla de inicio con acceso directo a Escribir y Hablar.

## Tecnologías

- Kotlin 2.0, Jetpack Compose (Material 3) y Navigation Compose
- Fragment + ViewBinding + RecyclerView para Buscar dispositivo
- ViewModel, StateFlow y corrutinas
- Firebase Authentication y Cloud Firestore
- Google Play Services Location
- Pruebas: JUnit, Mockito, Robolectric, Espresso y Compose UI Test

## Configurar Firebase

1. Crear un proyecto en la consola de Firebase.
2. Agregar una app Android con el paquete `cl.duoc.comunicaplusrs` y copiar `google-services.json` en la carpeta `app/`.
3. Authentication: activar el proveedor **Correo electrónico/contraseña**.
4. Firestore Database: crear la base de datos y pegar las reglas del archivo [`firestore.rules`](firestore.rules).

Estructura de datos en Firestore:

```
usuarios/{uid}
    frases/{id}
    conversaciones/{id}
    dispositivos/{idTelefono}
```

## Pruebas

```bash
./gradlew testDebugUnitTest            # JUnit, Mockito y Robolectric (sin emulador)
./gradlew connectedDebugAndroidTest    # Espresso (con emulador o teléfono conectado)
```

Las pruebas instrumentadas también se pueden ejecutar en Firebase Test Lab subiendo `app-debug.apk` y `app-debug-androidTest.apk`.

## APK firmado

La firma se lee desde `keystore.properties` en la raíz del proyecto (no se sube al repositorio):

```properties
storeFile=comunicaplusrs-release.jks
storePassword=...
keyAlias=comunicaplusrs
keyPassword=...
```

```bash
./gradlew assembleRelease
```

El APK queda en `app/build/outputs/apk/release/app-release.apk`.
