<h1 align="center">One-Tap Sign in with Google</h1></br>

<p align="center">
  <a href="https://jitpack.io/#stevdza-san/OneTapCompose/1.0.10"><img alt="License" src="https://badgen.net/badge/Jitpack/1.0.10/orange?icon=github"/></a>
  <a href="https://github.com/stevdza-san"><img alt="Profile" src="https://badgen.net/badge/Github/stevdza_san/green?icon=github"/></a>
</p><br>

<p align="center">
This library allow you to easily integrate One-Tap Sign in with Google to your project with Jetpack Compose. </br>
It hides all the boilerplate code away from you.
</p><br>

<p align="center">
<img src="https://github.com/stevdza-san/OneTapCompose/blob/master/previews/OneTap.gif" width="268"/>
  <img src="https://github.com/stevdza-san/OneTapCompose/blob/master/previews/OneTap2.gif" width="268"/>
</p>

# Download
<a href="https://jitpack.io/#stevdza-san/OneTapCompose/1.0.10"><img alt="License" src="https://badgen.net/badge/Jitpack/1.0.10/orange?icon=github"/></a>

### Gradle

Add the dependency below to your module's `build.gradle` file:
```gradle
dependencies {
    implementation("com.github.stevdza-san:OneTapCompose:1.0.10")
}
```
Add a repository in your `settings.gradle` file:
```
dependencyResolutionManagement {
    repositories {
        ...
        maven(url = "https://jitpack.io")
    }
}
```
# Usage

Before you can use this library, you need to create a new project on a <a href="https://cloud.google.com/">Google Cloud Platform</a>.
You need to create OAuth Client ID <i>(ANDROID & WEB)</i>, because you will need that same Client ID (WEB), in order to implement One-Tap Sign in with Google.
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

# Google User

And if you wish to extract a user information from a token id, that's now possible too! `getUserFromTokenId()` allows you to do exactly that. It returns a `GoogleUser` object, that contains lot's of different information related to that same user.

```kotlin
onTokenIdReceived = { tokenId ->
    Log.d("LOG", getUserFromTokenId(tokenId).toString())
}
```

Available `GoogleUser` information:
- Sub
- Email
- EmailVerified
- FullName
- GivenName
- FamilyName
- Picture
- IssuedAt
- ExpirationTime
- Locale

# Sign in with Google Button

You can also use a drop-in opinionated button composable that works out of the box and encapsulates all the
sign in logic and follows Google's [Sign in with Google Branding Guidelines](https://developers.google.com/identity/branding-guidelines):

```kotlin
OneTapSignInWithGoogleButton(
    clientId = "YOUR_CLIENT_ID"
)
```

According to the design guidelines, the button is available in 3 themes:
1. Dark
2. Light
3. Neutral

<img src="https://github.com/stevdza-san/OneTapCompose/blob/master/previews/ButtonDark.png" width="300"/>
<img src="https://github.com/stevdza-san/OneTapCompose/blob/master/previews/ButtonLight.png" width="300"/>
<img src="https://github.com/stevdza-san/OneTapCompose/blob/master/previews/ButtonNeutral.png" width="300"/>

You can customise the theme using the `theme` parameter in the composable:
```kotlin
OneTapSignInWithGoogleButton(
    clientId = "YOUR_CLIENT_ID",
    theme = GoogleButtonTheme.Neutral
)
```

The buttons are also available in icon-only mode for all the themes:

<img src="https://github.com/stevdza-san/OneTapCompose/blob/master/previews/IconButtonDark.png" width="100"/>
<img src="https://github.com/stevdza-san/OneTapCompose/blob/master/previews/IconButtonLight.png" width="100"/>
<img src="https://github.com/stevdza-san/OneTapCompose/blob/master/previews/IconButtonNeutral.png" width="100"/>

It can be activated using the `iconOnly` parameter in the composable:

```kotlin
OneTapSignInWithGoogleButton(
    clientId = "YOUR_CLIENT_ID",
    iconOnly = true
)
```

## Button API
| Name  | Type | Description |
| ------------- | ------------- | ------------- |
| clientId  | String  | CLIENT ID (Web) of your project, that you can obtain from a Google Cloud Platform.  |
| state  | OneTapSignInState  | One-Tap Sign in State. Can be used to detect whether the sign in operation has already been triggered.  |
| rememberAccount  | Boolean  | Remember a selected account to sign in with, for an easier and quicker sign in process.  |
| nonce  | String?  | Optional nonce that can be used when generating a Google Token ID  |
| onTokenIdReceived  | ((String) -> Unit)?  | Lambda that will be triggered after a successful authentication. Returns a Token ID.  |
| onUserReceived  | ((String) -> Unit)?  | This function returns a GoogleUser object using the received tokenId.  |
| onDialogDismissed  | ((String) -> Unit)?  | Lambda that will be triggered when One-Tap dialog disappears. Returns a message in a form of a string.  |
| iconOnly  | Boolean  | Whether the button should only show the Google logo.  |
| theme  | GoogleButtonTheme  | Sets the button style to either be Light, Dark, or Neutral which is in accordance with the official Google design guidelines.  |
| colors  | ButtonColors  | ButtonColors that will be used to resolve the colors for this button in different states.  |
| border  | BorderStroke?  | the border to draw around the container of this button  |
| shape  | Shape  | defines the shape of this button's container, border (when border is not null)  |
| onClick  | (() -> Unit)?  | called when this button is clicked  |

# Release Build
If you are planning on publishing your app, be sure to generate a release SHA-1 fingerprint, and create a new oAuth credentials on your Google Cloud Platform project.
Also when you upload your app on Play Console, you'll find there a section (`Release > Setup > App signing`) that will generate the release SHA-1 fingerprint. You take it and create another oAuth credential.

# Troubleshoot
In some cases you may encounter <i>"Google Account not Found."</i> message inside `onDialogDismiss` lambda, even if you have already connected a Google account
on your Android Emulator. Android emulators are prone to that issues <i>(Not sure why and when that's gonna get fixed)</i>.
My suggestion in that case is to try and add a Google account on some other Android Emulator.
If that doesn't work either, then use a real device instead.

Also to debug your app better, check the logs and search for a `OneTapCompose` tag, it might contain additional information to help you out with your issue.

# Like what you see? :yellow_heart:
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
