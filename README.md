<h1 align="center">One-Tap Sign in with Google</h1></br>

<p align="center">
  <a href="https://jitpack.io/#stevdza-san/OneTapCompose/1.0.0"><img alt="License" src="https://badgen.net/badge/Jitpack/1.0.0/orange?icon=github"/></a>
  <a href="https://github.com/stevdza-san"><img alt="Profile" src="https://badgen.net/badge/Github/stevdza_san/green?icon=github"/></a>
</p><br>

<p align="center">
This library allow you to easily integrate One-Tap Sign in wih Google to your project.
It hides all the boilerplate code away from you.
</p><br>

<p align="center">
<img src="https://github.com/stevdza-san/OneTapCompose/blob/master/previews/OneTap.gif" width="268"/>
</p>

## Download
<a href="https://jitpack.io/#stevdza-san/OneTapCompose/1.0.0"><img alt="License" src="https://badgen.net/badge/Jitpack/1.0.4/orange?icon=github"/></a>

### Gradle

Add the dependency below to your module's `build.gradle` file:
```gradle
dependencies {
    implementation 'com.github.stevdza-san:OneTapCompose:1.0.0'
}
```
Add a repository in your `settings.gradle` file:
```
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
## Usage

Before you can use this library, you need to create a new project on a <a href="https://cloud.google.com/">Google Cloud Platform</a>.
You need to create OAuth Client ID (ANDROID & WEB), because you will need that same Client ID (WEB), in order to implement One-Tap Sign in with Google.
</br></br>
After that, just call `OneTapSignInWithGoogle()` function, and pass that same information you've obtained through Google Cloud Platform.
You will also pass `OneTapSignInState`, because that state is used later to trigger One-Tap dialog.

```kotlin
    val state = rememberOneTapSignInState()
    OneTapSignInWithGoogle(
        state = state,
        clientId = "YOUR_CLIENT_ID",
        onTokenIdReceived = { tokenId ->
            Log.d("LOG", tokenId)
        },
        onDialogDismissed = { message ->
            Log.d("LOG", message)
        }
    )
```

To trigger One-Tap dialog, just call `open()` function.

```kotlin
Button(onClick = { state.open() }) {
    Text(text = "Sign in")
}
```

## Troubleshoot
In some cases you may encounter <i>"Google Account not Found."</i> message inside `onDialogDismiss` lambda, even if you have already connected a Google account
on your Android Emulator. Android emulators are prone to that issues <i>(Not sure why and when that's gonna get fixed)</i>.
My suggestion in that case is to try and add a Google account on some other Android Emulator.
If that doesn't work, then use a real device.

## Like what you see? :yellow_heart:
⭐ Give a star to this repository. <br />
☕ Buy me a coffee: https://ko-fi.com/stevdza_san

# License
```xml
Designed and developed by 2022 stevdza-san (Stefan Jovanović)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
