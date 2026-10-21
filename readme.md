# WiiM Volume Sync

<img width="930" height="523" alt="Screenshot From 2026-10-21 21-27-03" src="https://github.com/user-attachments/assets/bd513ddf-1e08-43ab-b367-d7b1792a08e5" />

## Features

- Controls the volume of your WiiM device from your AndroidTV / Nvidia Shield Remote
- Sends a GET request to a custom URL when pressing the Netflix Button (eg. toggle the lights)

## Installation

Use ADB do install the app.

1. Enable wireless debugging on the android TV
2. Connect & Install

```shell
adb connect <android-tv-ip>
adb install wiim-volume-sync.apk
```

## Quick start

- Enable WiiM Volume Sync in Accessibility services
- Configure > enter IP Adress of your WiiM device > Apply & Config

<img width="930" height="523" alt="Screenshot From 2026-10-21 21-32-42" src="https://github.com/user-attachments/assets/28c23dd7-0f8e-4d32-804b-b7583a30f981" />


### Basic operation

The App creates an accessibility service which intercepts Volume +/- and Netflix Button (`KeyEvent.KEYCODE_BUTTON_12`) commands and transforms them
into a WiiM IP API or HTTP GET call.


## Compatibility

Tested on a Nvidia Shield TV.

> Intercepting Keystrokes might not work on newer devices & target SDKs above 36.

## Contributing

Fork/Clone/Copy at your own will. A thank you is enough.
