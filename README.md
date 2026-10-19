![WanderingMerchants](https://raw.githubusercontent.com/MahinBro001238/WanderingMerchants/main/Logo%20Banner.png)

## **Brief Overview**
Wandering Merchants brings RPG-style merchants to your server. Instead of every shop being in a fixed location like being confined to a single market area, these merchants spawn and roam the world on their own, and stumbling across one out in the wild make the server feel more alive. When a player finds a merchant, the player must give it an item specific to its personality and in return the merchant will allow the player to interact with it to browse and buy items.

## **Under the hood**
The NPCs themselves are created and controlled through Citizens2. For the actual trading functionality of buying and selling, instead of reinventing a new shop system, the merchants use EconomyShopGUI (which directly plugs into your server's economy) for their shops, buying from them uses your existing in-game currency meaning all shop related configuration can be done through EconomyShopGUI's config. Merchants that have not been given their desired items will disappear after a certain amount of time.

## **Minecraft Version and Server Software**
The latest version plugin was built for Minecraft versions between 26.1.2 and 26.3 running on PaperMC. It may or may not work on other server software. It should work on forks of PaperMC such as Purpur and Pufferfish, though it has not been tested on them.

## **To get the full experience of this plugin:**

i) Make sure that the permissions 'economyshopgui.shop' and 'economyshopgui.sellgui.all' are set to false. This makes it so that players can make transactions through the merchants only otherwise why would players even bother to go to a merchant to buy or sell items when then can just do so using commands. To configure these permissions, you may use any permission manager plugin.

ii) Make sure to set 'direct-shop-command-console' to 'true' in the EconomyShopGUI's config. This is because if a player were to open a shop, the player could just use the back button to go the main menu of the plugin and access all the other shops that the merchant was not supposed to give access to.

## **Note**
One thing to mention is that EconomyShopGUI requires an economy plugin to function properly such as vault (vault also depends on EssentialsX). You may use any economy plugin.

## **Required plugins:**
- Citizens2: https://ci.citizensnpcs.co/job/citizens2/
- EconomyShopGUI: https://www.spigotmc.org/resources/economyshopgui.69927/

## **Optional plugins:**
- LuckPerms: https://modrinth.com/plugin/luckperms (you can use whatever permission manager plugin you like)
- Vault 2.0: https://modrinth.com/plugin/vault-2.0-economy-plugins (you can use whatever economy plugin you like)

## **Support**
For support or questions, join my discord server: https://discord.gg/8yrbGUCFFD

## **License**
Licensed under the Apache License 2.0
