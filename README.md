# neji

An app to manage your items using QR code for Android.

## tech stack
* Kotlin Coroutines and Flows
* Dagger Hilt
* DataStore
* Firebase (Auth, Firestore, Storage)
* CameraX
* ML Kit (barcode)

* MVVM with ViewBinding
* gradle (not .kts)
* single-module
* doesn't i18n

## Setup and login
This app requires a firebase project.
The setup and how to login on the app using it are described below.

#### 1. Create a firebase project
Create a firebase project from [here](https://console.firebase.google.com).
You can give it any name you like. This name will be used for login in the application.

#### 2. Authentication settings
Select `Authentication` from the tree on the left to start using it.
Then, open the `Sign-in method` tab, and enable the `Email/Password` provider. At this time, leave the `Email link` disabled.

Once enabled, open the `Users` tab and add a user.
Both email and password can be anything you want. This name will also be used for login in the app.

Since this app is currently intended to be used by one person, we don't need to create multiple users.

Make sure to copy to your clipboard the `User UID` you created.

#### 3. Firestore settings
Next, we will configure the Firestore settings to save the data.

Select Firestore from the tree on the left and create a database.
Choose `Production` for the `Rules`, and for the `Region`, the one fits where you are.

Once the database has been created, we can rewrite the rules.
Open the `Rules` tab and rewrite the rules as below.
Also, replace `USER_ID` with the `User UID` of the user you just added.

```plain
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == "USER_ID";
    }
  }
}
````

#### 4. Storage settings
The next step is to configure the Storage settings to store the images.

Select `Storage` from the tree on the left to start using it.

Once started, select the `Rules` tab, and rewrite the rules as below.
Also, replace `USER_ID` with the UID of the user you just added.

```plain
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == "USER_ID";
    }
  }
}
```

#### 5. Create App ID
Click the gear icon from the left tab and open `Project settings`.

Open the `General` tab, select `Android` from the `Your apps` column, and register your app.
There is no need to download any json and other files.
The `App ID` will now be shown in the `Your apps` column.

#### 6. Login on the app
Login with the app using the firebase project.

The `Firebase Web API key` is the value of the `Web API Key` in the `Project settings`.