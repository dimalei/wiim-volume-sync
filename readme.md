# WiiM Volume Sync

## Introduction

A simple app developed for Android TV boxes to sync their system volume with a WiiM Amp.

## Installation

Use ADB do install the app.

1. Enable wireless debugging on the android TV
2. Connect & Install

```shell
adb connect <android-tv-ip>
adb install wiim-volume-sync.apk
```

## Quick start

![app-screen.png](img/app-screen.png)

- Open the app from the Apps screen and set your WiiM Devices IP.
- Set the max volume to your liking.

> You may need to adjust the System Setting so you remote controls the AndroidTV Media/System
> Volume.

## Usage

Use your Android TVs remote to control the Volume on your WiiM Amp.

### Basic operation

The apps polls the system volume in a 150ms interval and forwards it to your WiiM device over HTTP
once it changed.

WiiM Devices use a self signed certificate, which Android refuses to accept it by default. When
setting the WiiM IP, the app fetches WiiM devices SSL certificate and stores its hash. It will be
checked against the the WiiM Certificate again when sending commands.

## Known issues and limitations

The app was mostly hacked together in a day out of necessity. Expect a lot of them.

## Contributing

Fork/Clone/Copy at your own will.
