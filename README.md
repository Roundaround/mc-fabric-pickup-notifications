![Pickup Notifications](https://github.com/Roundaround/mc-fabric-pickup-notifications/raw/main/assets/title-round.png)

![](https://img.shields.io/badge/Loader-Fabric-313e51?style=for-the-badge)
![](https://img.shields.io/badge/MC-1.21--1.21.6%20|%201.20%20|%201.19-313e51?style=for-the-badge)
![](https://img.shields.io/badge/Side-Client%2BServer-313e51?style=for-the-badge)

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/pickup-notifications?style=flat&logo=modrinth&color=00AF5C)](https://modrinth.com/mod/pickup-notifications)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1292780?style=flat&logo=curseforge&color=F16436)](https://www.curseforge.com/minecraft/mc-mods/pickup-notifications)
[![GitHub Repo stars](https://img.shields.io/github/stars/Roundaround/mc-fabric-pickup-notifications?style=flat&logo=github)](https://github.com/Roundaround/mc-fabric-pickup-notifications)

[![Support me on Ko-fi](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/kofi-singular-alt_vector.svg)](https://ko-fi.com/roundaround)

---

Show popup notifications when you pick up items. Shows the items stripped of all unique data (enchantments, name, condition) and shows the raw item icon and name. As of 2.0.0, notifications will also be shown for experience orbs!

Starting with 1.1.0, the mod is required on both the server and client for multiplayer servers.

![](https://cdn.modrinth.com/data/wWiJfmyy/images/39d92e7ed23270ebe50fa0953a75691cd133a226.png)

### Configuration

You can configure the behavior of the mod from the `pickupnotifications.toml` file within your config folder. If you have ModMenu installed, you can also access the configuration through the UI in ModMenu's mod list!

`modEnabled`: `true|false` - Simple toggle for the mod! Set to `false` to disable.

`trackExperience`: `true|false` - Whether to show notifications for experience orb pickups. Set to `false` to disable. (2.0.0+ only)

`guiAlignment`: `"top_left"|"top_right"|"bottom_left"|"bottom_right"` - Where to position the notifications.

`guiOffset`: `"(<Integer>,<Integer>)"` - The amount to offset the notifications from the edge of the screen.

`guiScale`: `Decimal` - Scale to render notifications at.

`maxNotifications`: `Integer` - How many notifications can be on the screen at a time. Additional notifications will be queued up and shown once there is room.

`iconAlignment`: `"outside"|"inside"|"left"|"right"` - Whether the item icons should appear on the 'left' or  'right' of notifications, always on the 'outside' (left for left-aligned, right for right-aligned), or always on the 'inside' of notifications.

`showUniqueInfo`: `true|false` - Whether to show custom names, rarity, and enchantments in the notifications.

`renderBackground`: `true|false` - Whether to render the background behind notifications.

`backgroundOpacity`: `Decimal` - Opacity of the notification background color.

`renderShadow`: `true|false` - Whether to render text shadow in notifications.

#### Version 1.2.4

`guiOffsetX`: `Integer` - The amount to offset the notifications from the side of the screen.

`guiOffsetY`: `Integer` - The amount to offset the notifications from the top or bottom of the screen.
