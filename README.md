# Long press volume to skip tracks
This repo provides a systemless and standalone application which let's you skip tracks on your Android (8.0+) phone by long pressing the volume keys while the screen is off. It doesn't require root but has to be flashed through a custom recovery. The latest release can be found here.

### For developers
Please feel free to send me pull requests for improvements, stability and new feature ideas. Because the application uses the hidden API you have to use a modified version of the [android.jar](https://github.com/anggrayudi/android-hidden-api) file for compiling. For this download the required android.jar file from the linked repo and replace it with original one in `<SDK location>/platforms/android-<API version>/`.

If you change the `targetSdk` or `minimumSdk` make sure to use the proper file.

### Testing
If you want to test the application on an emulator you have to grant the permission to register a volume key long press listener to the application before you enable the `NotificationListenerService`. This can be done via ADB with the following command:

    adb shell pm grant com.cilenco.skiptrack android.permission.SET_VOLUME_KEY_LONG_PRESS_LISTENER

After that you can send long press events of the volume keys to the emulator with following lines:

    adb shell input keyevent --longpress KEYCODE_VOLUME_UP
    adb shell input keyevent --longpress KEYCODE_VOLUME_DOWN

### ToDo
Currently the application is using a `NotificationListenerService` to deal with the new [background limitations](https://developer.android.com/about/versions/oreo/background.html) introduced in Android Oreo. This requires the user to manually enable it in the system settings but has the advantage that it doesn't display an ongoing notification. The problem with this solution is that the application is always running in the background and consumes (very less but measurable) system resources.

If anyone has a better solution for this (i.e. detect a new `MediaSession` and start the service in response) please open an issue for this or send me a pull request. Remember that this should act as a system feature so a notification (e.g. in ForegroundServices) should be avoided.
