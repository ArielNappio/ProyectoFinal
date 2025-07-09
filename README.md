# Manual de Implementación

## 📘 Wirin – App de Accesibilidad Educativa
Wirin es una aplicación móvil desarrollada en Kotlin utilizando Jetpack Compose, orientada a mejorar la accesibilidad en el ámbito educativo.
Permite a estudiantes con discapacidad visual acceder a contenido adaptado de forma clara y accesible.


## 🧩 Requisitos

- Android Studio Giraffe o superior

- Java 17

- Kotlin 2.0+

- Conexión a Internet

- Git instalado

- Emulador o dispositivo Android (API 24+)

- Deploy de backend (API) y Base de datos (SQL):
  - [Wirin API](https://github.com/thomasloader1/wirin-api)

## 🔽 1. Clonar el repositorio
```
git clone https://github.com/ArielNappio/ProyectoFinal.git
cd ProyectoFinal
```
## 🧱 2. Estructura general del proyecto
La app está modularizada, y contiene principalmente:

- core: lógica común (network, modelos, utils, etc.)

- features: módulos por funcionalidad

## ⚙️ 3. Configuración de la IP para la API
Para apuntar a tu servidor/API local o remoto, debés modificar la IP en:

📁 core/network/ApiUrls.kt

```kotlin
object ApiUrls {
    const val BASE_URL = "http://192.168.1.X:8080/" // Cambiar esta IP
}
```

⚠️ Asegurate de estar en la misma red que el backend (si es local), y que el backend permita conexiones desde el dispositivo/emulador.

## ▶️ 4. Ejecutar la app

1. Abrir el proyecto en Android Studio.

2. Dejar que sincronice las dependencias (Gradle sync).

3. Seleccionar un emulador o conectar un dispositivo físico.

4. Hacer click en Run ▶️ sobre el módulo app.

## 🐞 5. Problemas comunes

- Timeouts al llamar a la API: Verificar que la IP esté bien configurada en ApiUrls.kt y que el backend esté levantado.

- CORS o errores de red en emulador: Si usás localhost, reemplazar por la IP de tu máquina (ipconfig en Windows o ifconfig en Unix/macOS).

## 📦 6. Generar APK o descargar 

### 🔧 Opción 1: Generar APK localmente

- Ejecutar el siguiente comando en la raíz del proyecto:
```
    ./gradlew assembleDebug
```
- El APK se encuentra en:
```
    app/build/outputs/apk/debug/app-debug.apk
```
### ☁️ Opción 2: Descargar APK desde GitHub
También podés descargar la última versión del APK directamente desde la pestaña Actions del repositorio:

- Ir a [Github Actions](https://github.com/ArielNappio/ProyectoFinal/actions)

- Seleccionar el workflow más reciente

- Ir al final de la página y descargar el artefacto llamado app-debug.apk (o similar)
  
## 🛠️ Stack de tecnologías y librerías utilizadas

| Categoría                | Herramienta / Librería                      |
|--------------------------|---------------------------------------------|
| Lenguaje                 | Kotlin                                      |
| UI                       | Jetpack Compose                             |
| Arquitectura             | Clean Architecture modular por features     |
| Inyección de dependencias| Koin                                        |
| Manejo de estados        | Flow / StateFlow                            |
| Persistencia local       | Room (SQLite)                               |
| Cliente HTTP             | Ktor con kotlinx.serialization              |
| Reconocimiento de voz    | SpeechRecognizer (Android)                  |
| Texto a voz (TTS)        | TextToSpeech (Android)                      |
| Modularización           | Módulos `core` y `features`                 |
| Gestión de dependencias  | Gradle con Kotlin DSL                       |

