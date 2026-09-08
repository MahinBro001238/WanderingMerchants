Wandering Merchants brings RPG-style travelling traders to your server. Instead of every shop being in a fixed location, these merchants roam the world on their own, and stumbling across one out in the wild feels like a small event. It makes exploration more rewarding and gives your world the feel of a living, populated place where trade happens out in the world rather than being confined to a single market area

Merchants are NPCs that spawn randomly around the world rather than being placed by an admin. When a player finds one, they can interact with it to browse and buy items. Because the merchants are wired into your server's economy, buying from them uses your existing in-game currency, so the prices and money all flow through the same system your players already use for everything else. This turns exploring into a way to find deals and rare goods rather than just fighting mobs and gathering resources

Under the hood, the NPCs themselves are created and controlled through Citizens, which handles their appearance, movement, and persistence. The actual buying and selling is powered by EconomyShopGUI, meaning the plugin hooks into an established shop system rather than reinventing one, and all transactions respect your configured shop prices. Because the merchants spawn on a timer and manage their own lifecycle, they come and go naturally without needing an admin to place or remove them

This plugin has no config file. Any custom behaviour requires modifying the source code directly and rebuilding the jar from scratch

This plugin was built for Minecraft 26.2 running on PaperMC. It may or may not work on older or newer versions of Minecraft or other server software

This plugin depends on Citizens, EconomyShopGUI, and LuckPerms

- Citizens: https://modrinth.com/plugin/citizens
- EconomyShopGUI: https://modrinth.com/plugin/economyshopgui
- LuckPerms: https://modrinth.com/plugin/luckperms

- In order to work correctly, this plugin assumes that LuckPerms' default group has '*' set to false
- Make sure to set 'direct-shop-command-console' to 'true' in the EconomyShopGUI plugin's config
- LuckPerms' default group must be configured so that 'economyshopgui.shop' and 'economyshopgui.sellgui.all' permissions are set to false
- Another thing to mention is that EconomyShopGUI requires a economy plugin to function properly such as vault (vault also depends on EssentialsX) however it has not been listed as a dependency for Wandering Merchants does not care which economy plugin is being used

Licensed under the Apache License 2.0
