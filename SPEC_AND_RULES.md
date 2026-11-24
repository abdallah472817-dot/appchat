# مواصفات بناء ChatApp من الصفر (بعد حذف القديم نهائياً)

- المجلد الرئيسي للمشروع: `chatapp/`
- اسم الحزمة (Package Name): `com.example.chatapp`
- ملف إعداد Firebase: `chatapp/app/google-services.json` (بصمة SHA-1 المسجلة: `1e:bd:7b:76:45:bc:21:97:b0:37:a0:fc:5b:3a:52:5c:8c:e5:21:b4`)
- البنية المطلوبة تحت `chatapp/app/src/main/java/com/example/chatapp/`:
  - `model/`: `UserProfile.kt`, `ChatMessage.kt`, `AppSettings.kt`
  - `data/`: `AuthRepository.kt`, `ChatRepository.kt`, `SettingsStore.kt`
  - `navigation/`: `AppRoute.kt`, `AppNavGraph.kt`
  - `ui/theme/`: `Theme.kt`, `Type.kt`
  - `ui/components/`: `BottomNavBar.kt`, `MessageBubble.kt`
  - `ui/screens/`: `LoginScreen.kt`, `ChatScreen.kt`, `ProfileScreen.kt`, `SettingsScreen.kt`
  - `MainActivity.kt`
- ملف CI/CD: `.github/workflows/android.yml` على `ubuntu-latest` مع `JDK 17` وتوليد `debug.keystore` وطباعة `SHA-1` وبناء `app-debug.apk`.
- قواعد الكود: Jetpack Compose + Material 3، فصل الواجهة عن `Repository` و `DataStore`، معالجة الأخطاء بـ `Result<T>` و `try-catch`.
