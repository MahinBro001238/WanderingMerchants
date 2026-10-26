![WanderingMerchants](https://raw.githubusercontent.com/MahinBro001238/WanderingMerchants/main/Banner.png)

## **Brief Overview**
Wandering Merchants brings RPG-style merchants to your server. Instead of every shop being in a fixed location like being confined to a single market area, these merchants spawn and roam the world on their own, and stumbling across one out in the wild make the server feel more alive. When a player finds a merchant, the player must give it an item specific to its personality and in return the merchant will allow the player to interact with it to browse and buy items.

## **Required Plugins**
- EconomyShopGUI: https://www.spigotmc.org/resources/economyshopgui.69927/
- Citizens2: Not all Citizens2 builds support 26.3 to 1.21.9, you must choose a specific build based on your Minecraft server's version:
    - 26.3, 26.2, 26.1.2, 26.1.1: https://ci.citizensnpcs.co/job/citizens2/ (latest)
    - 1.21.11: https://ci.citizensnpcs.co/job/Citizens2/4250
    - 1.21.10, 1.21.9: https://ci.citizensnpcs.co/job/Citizens2/4202

## **Setup Guide**
- Make sure that the permissions 'economyshopgui.shop' and 'economyshopgui.sellgui.all' are set to false. This makes it so that players can make transactions through the merchants only otherwise why would players even bother to go to a merchant to buy or sell items when then can just do so using commands. To configure these permissions, you may use any permission manager plugin.

- Make sure to set 'direct-shop-command-console' to 'true' in the EconomyShopGUI's config. This is because if a player were to open a shop, the player could just use the back button to go the main menu of the plugin and access all the other shops that the merchant was not supposed to give access to.

## **Commands**

- `/wm spawn <merchant id> [player]` — Spawn a merchant at your location or at another player's location
  - `/wm spawn sylphie` or `/wm spawn sylphie MahinBro`

- `/wm give <merchant id> [player]` — Give a merchant's item to yourself or another player
  - `/wm give sylphie` or `/wm give sylphie MahinBro`

- `/wm recipe <merchant id> [player]` — Open a merchant item's crafting recipe GUI for yourself or another player
  - `/wm recipe sylphie` or `/wm recipe sylphie MahinBro`

- `/wm get merchant_data <merchant id>` — View the config data of a merchant
  - `/wm get merchant_data sylphie`

- `/wm get npc_data <npc id>` — View the internal data of a merchant NPC set by this plugin
  - `/wm get npc_data 3`

- `/wm get item_data` — View the internal data of the item in your main hand set by this plugin

- `/wm set npc_data <npc id> <field> <value|player|NULL>` — Set the internal data in NPCs used by this plugin

  > ⚠️ **Use with caution.** Incorrectly modifying internal data of a merchant NPC can lead to unexpected issues. Only use this command if you know what you are doing.

  | Fields | Description | Accepted Values | Example |
  |---|---|---|---|
  | `merchant_id` | The ID of a merchant defined in the config | A merchant ID, or `NULL` | `/wm set npc_data 3 merchant_id sylphie` |
  | `merchant_variant` | The current state of the merchant NPC | `stray`, `stray_moving`, `shopkeeper`, `shopkeeper_moving`, or `NULL` | `/wm set npc_data 3 merchant_variant shopkeeper` |
  | `merchant_viewers` | The number of players currently viewing this NPC's GUI. Only for stray variants | An integer, or `NULL` | `/wm set npc_data 3 merchant_viewers 0` |
  | `creation_time` | The timestamp of when this NPC was spawned | A long integer, or `NULL` | `/wm set npc_data 3 creation_time 1745000000000` |
  | `following` | The player this NPC is currently following | An online player's name, or `NULL` to stop following if the NPC is following anyone at the moment | `/wm set npc_data 3 following MahinBro` |

  Use `NULL` as the value to clear the field.

- `/wm set item_data id <value|NULL>` — Set the merchant item ID for the item in your main hand
  - `/wm set item_data id sylphie_scroll`

  > ⚠️ **Use with caution.** Setting an item ID on even dirt can cause it to be accepted by a merchant or setting a incorrect item ID on a actual merchant item can cause it to not be accepted by the merchant. Only use this command if you know what you are doing.

- `/wm set item_data cmd <value|NULL>` — Set the custom model data of the item in your main hand
  - `/wm set item_data cmd sylphie_grimoire`

  > ⚠️ **Use with caution.** Modifying the custom model data of an item may cause it to display a different texture. Only use this command if you know what you are doing.

## **Permissions**

| Permission | Description | Who has it by default |
|---|---|---|
| `wanderingmerchants.wm` | Allows the use of /wm command | Operators only |

## **Under The Hood**
The NPCs themselves are created and controlled through Citizens2. For the actual trading functionality of buying and selling, instead of reinventing a new shop system, the merchants use EconomyShopGUI (which directly plugs into your server's economy) for their shops, buying from them uses your existing in-game currency meaning all shop related configuration can be done through EconomyShopGUI's config. Merchants that have not been given their desired items will disappear after a certain amount of time.

## **Minecraft Version And Server Software**
The latest version plugin was built for Minecraft versions between 26.3 and 1.21.9 running on Paper. It may or may not work on other server software. It should work on forks of Paper such as Purpur and Pufferfish, though it has not been tested on them.

## **Note**
One thing to mention is that EconomyShopGUI requires an economy plugin to function properly such as vault (vault also depends on EssentialsX). You may use any economy plugin.

The world seed used in the gameplay images is 3353863628485365188 in version 26.3 if anyone cares 😄.

## **Optional Plugins**
- LuckPerms: https://modrinth.com/plugin/luckperms (you can use whatever permission manager plugin you like)
- Vault 2.0: https://modrinth.com/plugin/vault-2.0-economy-plugins (you can use whatever economy plugin you like)

## **Support**
For support or questions, join my discord server: https://discord.gg/8yrbGUCFFD

## **License**
Licensed under the Apache License 2.0

## **Other Projects**
Check out other cool stuff I made at https://modrinth.com/user/MahinBro001238
