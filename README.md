# DCU-update example app
This project shows how to implement a DCU service app.

The Device Control Update (DCU) is a new kind of update that allows Update Factory to execute tasks on Android devices.

DCU workflow:
* the uf-android-client downloads the DCU update
* the uf-android-client installs the app in DCU update (dcu-service-app)
* the uf-android-client starts the dcu-service-app
* the dcu-service-app executes the programmed tasks and notifies the result to the uf-android-client
* the uf-android-client uninstalls the dcu-service-app
* the uf-android-client notifies the update result to the update-server

Example use case: update the network access point settings of a group of Android devices.

:warning: Pay attention that the dcu-service-app has to be idempotent because it could be started multiple times from the uf-android-client
(typically each time that the uf-android-client is started and the dcu-update is not ended).

## update-server configuration
To use a DCU update you have to use the new `DCU` software module.

## DCU service app
### Import library
Import the dcu-api library (dcu-api.aar) in your project:
* copy the dcu-api.aar file in the libs folder of your app module.
* include the following dependencies to your build.gradle:
    * implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
    * implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2"

### Service configuration
The dcu-service-app has to export an Android service runnable via an intent with the following format:

`<dcu-service-app applicationID>.action.DCU_SERVICE`

This is a service declaration example for an app with:
* applicationID = com.kynetics.dcuexampleapp
* service class named  = com.kynetics.dcuexampleapp.service.DCUService

```xml
<service
    android:name=".service.DCUService"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
      <action android:name="com.kynetics.dcuexampleap.action.DCU_SERVICE" />
    </intent-filter>
</service>
```

The dcu-service-app has to be a system app:
* the sharedUserId must be `android.uid.system`;
* the app must be signed with the device platform key.

## Communication from dcu-service-app to uf-android-service
After that the uf-android-service starts the dcu-service-app, it waits for a DCCResult message.
If the uf-android-service doesn't receive the DCUResult message within an hour, it uninstalls the dcu-service-app and
notifies the update-server a timeout failure (this behaviour can be changed by sending a DCUConfiguration message)

The dcu-service-app can send three types of message to the uf-android-service:
* [DCUConfiguration](https://kynetics.github.io/dcu-example-app/dcu-api/com.kynetics.uf.android.update.dcu.api/-d-c-u-configuration/index.html) for changing the default behaviour of the uf-android-service. Note that each dcu-service app has its own configuration.
* [DCUNotification](https://kynetics.github.io/dcu-example-app/dcu-api/com.kynetics.uf.android.update.dcu.api/-d-c-u-notification/index.html) for sending info to the update-server
* [DCUResult](https://kynetics.github.io/dcu-example-app/dcu-api/com.kynetics.uf.android.update.dcu.api/-d-c-u-result/index.html) for terminating the update.

To send the messages to the uf-android-client use the [DCUServiceNotifier](https://kynetics.github.io/dcu-example-app/dcu-api/com.kynetics.uf.android.update.dcu.api/-d-c-u-service-notifier/index.html) object

```kotlin
val result =  DCUResult(
    success = true,
    details = listOf(),
    applicationId = BuildConfig.APPLICATION_ID
)
DCUServiceNotifier.notify(applicationContext, result)
```

KDoc of dcu-api module is available [here](https://kynetics.github.io/dcu-example-app/index.html)
## dcu-minimal-app module
The dcu-minimal-app module contains a minimal template of a dcu service app.

When it is started, it simulates the execution of a generic task and then it notifies the result to the
uf-android-client.

## dcu-accesspoint-configurator
The dcu-accesspoint-configurator is an example of dcu service app witch changes the wireless used by the device.

It is a system app so it need to be signed with the device platform key.

Line 21 of [WirelessWorker](https://github.com/Kynetics/dcu-example-app/blob/master/dcu-accesspoint-configurator/src/main/java/com/kynetics/updatefactory/dcu/workers/WirelessWorker.kt#L21) have to be updated with your access point credentials to work correctly.