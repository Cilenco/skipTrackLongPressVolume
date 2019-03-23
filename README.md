# Long press volume to skip tracks
This repo provides a systemless and standalone application which let's you skip tracks on your Android Oreo (8.0+) device by long pressing the volume keys while the screen is off. It doesn't require root but a permission has to be granted to the app via adb. For more information see the details below.

### Installation
Download the newst version of the apk here and install it as usual on your device. After that connect your device to a computer, enable developer settings and fire up following command:

    adb shell pm grant com.cilenco.skiptrack android.permission.SET_VOLUME_KEY_LONG_PRESS_LISTENER

On startup the app will check if has the permission. If you don't see any message the process worked and you are ready to go. If the app is installed as system application by flashing it through a recovery this step is not needed as the permission is granted automatically.


### For developers
Please feel free to send me pull requests for improvements, stability and new feature ideas. Because the application uses the hidden API you have to use a modified version of the [android.jar](https://github.com/anggrayudi/android-hidden-api) file for compiling. For this download the required android.jar file from the linked repo and replace it with original one in `<SDK location>/platforms/android-<API version>/`.

If you change the `targetSdk` or `minimumSdk` make sure to use the proper file.

### Testing
You can test this app on an emulator by just giving it the permission as described above. After that you can send long press events of the volume keys to the emulator with following lines:

    adb shell input keyevent --longpress KEYCODE_VOLUME_UP
    adb shell input keyevent --longpress KEYCODE_VOLUME_DOWN

### ToDo
Currently the application is using a `NotificationListenerService` to deal with the new [background limitations](https://developer.android.com/about/versions/oreo/background.html) introduced in Android Oreo. This requires the user to manually enable it in the system settings but has the advantage that it doesn't display an ongoing notification. The problem with this solution is that the application is always running in the background and consumes (very less but measurable) system resources.

If anyone has a better solution for this (i.e. detect a new `MediaSession` and start the service in response) please open an issue for this or send me a pull request. Remember that this should act as a system feature so a notification (e.g. in ForegroundServices) should be avoided.
