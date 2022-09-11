# Call Monitor Task
[Dmytro Gorodnytskyi](https://www.linkedin.com/in/dgorod/)
September 2022

## Architecture
- App built using Clean Architecture pattern with MVVM (Android ViewModel).
- Kotlin Coroutines used for asynchronous work. And LiveData for observing updates.
- Koin used as a dependency injection solution. It's more simple than Dagger/Hilt for this small case.
- Ktor used as embedded in the app server. WebSockets solution I declined since for such thing as a HTTP server it's better to take proven solution that supports all protocols.
- Foreground service used to handle the server.

## Tests
- There are two instrumentation tests and one UI (Espresso) test.  
- Fakes used instead of mocks (Mockito).
- Whole project test coverage obviously not enough. But wasn't taken into account since it's only a test task.

## Notes
- Server runs using Android Foreground service. I chose it following Google guidelines related to security and user visibility about app's background work. Starting API 26 Google also introduced [Background Execution Limits](https://developer.android.com/about/versions/oreo/background). That may stop the background service.
- HTTP server `/status` returns only the boolean state. If device is on call or not. We can't collect ongoing call information due to security reasons. Since API 26 Google [put restrictions](https://developer.android.com/reference/android/telephony/TelephonyManager#EXTRA_INCOMING_NUMBER) on broadcast receivers. And since API 29 they [enforced the policy](https://developer.android.com/reference/android/telephony/TelephonyManager#EXTRA_INCOMING_NUMBER) so that only default call app can read the data.

## TODOs 
- Jetpack Compose can be used as a UI solution.
- Kotlin Gradle DSL or Bazel can be used to improve build setup.
- Architecture levels can be split in separate Android modules. Or instead of layers split by features.
- Runtime permissions could be handled better. With proper UI explanations.