# J2ME-Loader 

[![Crowdin](https://d322cqt584bo4o.cloudfront.net/j2me-loader/localized.svg)](https://crowdin.com/project/j2me-loader)
[![GitHub release](https://img.shields.io/github/release/nikita36078/J2ME-Loader.svg)](https://github.com/nikita36078/J2ME-Loader/releases)

J2ME-Loader is a J2ME emulator for Android. It supports most 2D and 3D games (including Mascot Capsule 3D ones). Emulator has a virtual keyboard, individual settings for each application, scaling support.
This project is a fork of [J2meLoader](https://github.com/NaikSoftware/J2meLoader).  
Special thanks to [woesss](https://github.com/woesss), the author of [JL-Mod](https://github.com/woesss/JL-Mod), for creating open-source Mascot Capsule implementation, soundbank support and many other improvements.

System requirements: Android 5.0+  
[4PDA discussion](https://4pda.to/forum/index.php?showtopic=824201)  
[XDA-Developers](https://forum.xda-developers.com/android/apps-games/app-j2me-loader-t3777889)  
[EmuGen wiki](https://emulation.gametechwiki.com/index.php/J2ME_Loader)  
[Discord](https://discord.gg/Ag4rcpz)  
[Automated builds](https://github.com/nikita36078/J2ME-Loader/releases/tag/continuous)

<a href="https://play.google.com/store/apps/details?id=ru.playsoftware.j2meloader">
<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" height="75"></a>
<a href="https://f-droid.org/app/ru.playsoftware.j2meloader">
<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="75"></a>

## Compatibility
[List of the tested Java Games (Touchscreen)](https://github.com/nikita36078/J2ME-Loader/wiki/List-of-Tested-Java-Games-(Touchscreen))  
[List of the tested Java Games (Non Touchscreen)](https://github.com/nikita36078/J2ME-Loader/wiki/List-of-Tested-Java-Games-(Non-Touchscreen))  
[List of the Java Games with Bugs](https://github.com/nikita36078/J2ME-Loader/wiki/List-of-Java-Games-with-Bugs)

## Tips
 - Enabling filtering in some cases can greatly reduce performance. Disable this option if game is too slow.
 - Image flickering issues can be fixed by enabling the "Immediate processing mode" option.
 - Long-press a game and pick "Move to folder" to group your library; the folders button in the toolbar filters the list, and the "Recently played" sort order puts the last launched game first.
 - Use "Select" from the overflow menu to move several games to a folder or delete them at once.
 - The "Folder" sort order groups the library by folder, and the current folder shows up as the toolbar subtitle while a filter is active.
 - Inside a game, the menu has "Hide virtual keyboard" to clear the on-screen keys for the current session without touching the app settings.
 - Long-press a game and pick "Open in new window" to run it again in a second (or third) window next to the first. Each extra window runs in its own process with a copy of the game data, so saves and record stores do not collide. Floating windows need a device or desktop mode that supports free-form windows; otherwise the windows live in separate tasks.
 - The emulator asks the display for its fastest refresh mode, so on 90/120 Hz screens games that draw that fast are no longer held to 60 Hz.
 - "Floating game windows" in Settings opens a game as a small window over the library (Picture-in-Picture). Use the system expand control to go full screen, or the in-game menu's "Float this game" to shrink it back; the setting is on by default and can be switched off.

## Screenshots

<img src="/screenshots/screen.jpg" width="288" height="512"> <img src="/screenshots/screen2.jpg" width="288" height="512">
<img src="/screenshots/screen3.jpg" width="288" height="512"> <img src="/screenshots/screen4.jpg" width="288" height="512">
* For more screenshots check out the [wiki](https://emulation.gametechwiki.com/index.php/J2ME_Loader#Screenshots)

## License
> Copyright 2017-2026 Nikita Shakarun.
> Licensed under the [Apache License, Version 2.0.](http://www.apache.org/licenses/LICENSE-2.0)  
> (See the [LICENSE](https://github.com/nikita36078/J2ME-Loader/blob/master/LICENSE) file for the whole license text.)
