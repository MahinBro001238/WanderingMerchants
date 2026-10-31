package com.mahin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.FollowTrait;
import net.kyori.adventure.text.event.ClickEvent;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.Entity;
import java.util.Scanner;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import net.citizensnpcs.trait.TargetableTrait;
import net.citizensnpcs.trait.waypoint.Waypoints;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.LivingEntity;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.scheduler.BukkitTask;
import java.util.Random;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
public class Plugin extends JavaPlugin implements Listener {
    private NamespacedKey MerchantItemKey;
    private Map<String, BukkitTask> tasks = new HashMap<>();
    private FileConfiguration config;
    private long SellGUIPermissionRevokeDelay;
    private boolean AllowItemCraftingWithoutMerchant;
    private Component NoPermissionMessage;
    private Component ItemCraftingNotAllowedMessage;
    private boolean SuppressMerchantDeathMessage;
    private List<WorldConfiguration> worlds = new ArrayList<>();
    private List<Merchant> merchants = new ArrayList<>();
    private String UpdateStatus;
    private Random RandomInstance = new Random();
    private static class MerchantConfiguration {
        public String id;
        public int rarity;
        public boolean enabled;
        public MerchantConfiguration(String id, int rarity, boolean enabled) {
            this.id = id;
            this.rarity = rarity;
            this.enabled = enabled;
        }
    }
    private static class WorldConfiguration {
        public String name;
        public int MerchantSpawnFrequency;
        public List<MerchantConfiguration> EnabledMerchants;
        public int TotalRarity;
        public WorldConfiguration(String name, int MerchantSpawnFrequency, List<MerchantConfiguration> EnabledMerchants, int TotalRarity) {
            this.name = name;
            this.MerchantSpawnFrequency = MerchantSpawnFrequency;
            this.EnabledMerchants = EnabledMerchants;
            this.TotalRarity = TotalRarity;
        }
        public String MerchantQuery(String MerchantId) {
            for (MerchantConfiguration MerchantConfiguration : EnabledMerchants) {
                if (MerchantConfiguration.id.equals(MerchantId)) {
                    if (MerchantConfiguration.enabled) {
                        return "Enabled";
                    }
                    else {
                        return "Disabled";
                    }
                }
            }
            return "Does not exist";
        }
        public String RandomMerchant(Random RandomInstance) {
            int RandomValue = RandomInstance.nextInt(TotalRarity) + 1;
            for (MerchantConfiguration merchant: EnabledMerchants) {
                if (merchant.enabled) {
                    RandomValue -= merchant.rarity;
                    if (RandomValue <= 0) {
                        return merchant.id;
                    }
                }
            }
            return null;
        }
    }
    private static class Merchant {
        public String id;
        public String name;
        public String shop;
        public String SkinValue;
        public String SkinSignature;
        public Component DefaultDialogue;
        public Component CorrectItemDialogue;
        public Component IncorrectItemDialogue;
        public Component FollowDialogue;
        public Component NonShopkeeperUnfollowDialogue;
        public Component ShopkeeperUnfollowDialogue;
        public Component SpawnText;
        public boolean glow;
        public int SpawnRadius;
        public long DespawnTime;
        public String ItemId;
        public Component ItemName;
        public List<Component> ItemLore;
        public String ItemCMDS;
        public ItemStack item;
        public Inventory ItemCraftingRecipeGUI;
        public Material ShopIconMaterial;
        public Component ShopIconName;
        public List<Component> ShopIconLore;
        public ItemStack ShopIcon;
        public Material SellIconMaterial;
        public Component SellIconName;
        public List<Component> SellIconLore;
        public ItemStack SellGUIIcon;
        public Merchant(String id, String name, String shop, String SkinValue, String SkinSignature, Component DefaultDialogue, Component CorrectItemDialogue, Component IncorrectItemDialogue, Component FollowDialogue, Component NonShopkeeperUnfollowDialogue, Component ShopkeeperUnfollowDialogue, Component SpawnText, boolean glow, int SpawnRadius, long DespawnTime, String ItemId, Component ItemName, List<Component> ItemLore, String ItemCMDS, ItemStack item, Inventory ItemCraftingRecipeGUI, Material ShopIconMaterial, Component ShopIconName, List<Component> ShopIconLore, ItemStack ShopIcon, Material SellIconMaterial, Component SellIconName, List<Component> SellIconLore, ItemStack SellGUIIcon) {
            this.id = id;
            this.name = name;
            this.shop = shop;
            this.SkinValue = SkinValue;
            this.SkinSignature = SkinSignature;
            this.DefaultDialogue = DefaultDialogue;
            this.CorrectItemDialogue = CorrectItemDialogue;
            this.IncorrectItemDialogue = IncorrectItemDialogue;
            this.FollowDialogue = FollowDialogue;
            this.NonShopkeeperUnfollowDialogue = NonShopkeeperUnfollowDialogue;
            this.ShopkeeperUnfollowDialogue = ShopkeeperUnfollowDialogue;
            this.SpawnText = SpawnText;
            this.glow = glow;
            this.SpawnRadius = SpawnRadius;
            this.DespawnTime = DespawnTime;
            this.ItemId = ItemId;
            this.ItemName = ItemName;
            this.ItemLore = ItemLore;
            this.ItemCMDS = ItemCMDS;
            this.item = item;
            this.ItemCraftingRecipeGUI = ItemCraftingRecipeGUI;
            this.ShopIconMaterial = ShopIconMaterial;
            this.ShopIconName = ShopIconName;
            this.ShopIconLore = ShopIconLore;
            this.ShopIcon = ShopIcon;
            this.SellIconMaterial = SellIconMaterial;
            this.SellIconName = SellIconName;
            this.SellIconLore = SellIconLore;
            this.SellGUIIcon = SellGUIIcon;
        }
    }
    private static class InventoryOwner implements InventoryHolder {
        public String owner;
        public InventoryOwner(String owner) {
            this.owner = owner;
        }
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
    private ItemStack CreateItem(Material material, Component name, List<Component> lore, String CMDS, String PDCS) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.displayName(name);
        }
        if (lore != null) {
            meta.lore(lore);
        }
        if (CMDS != null) {
            CustomModelDataComponent CMD = meta.getCustomModelDataComponent();
            CMD.setStrings(List.of(CMDS));
            meta.setCustomModelDataComponent(CMD);
        }
        if (PDCS != null) {
            meta.getPersistentDataContainer().set(MerchantItemKey, PersistentDataType.STRING, PDCS);
        }
        item.setItemMeta(meta);
        return item;
    }
    private Merchant GetMerchantData(String id) {
        for (int i=0; i<merchants.size(); i++) {
            Merchant MerchantData = merchants.get(i);
            if (MerchantData.id.equals(id)) {
                return MerchantData;
            }
        }
        return null;
    }
    @Override
    public void onEnable() {
        MerchantItemKey = new NamespacedKey(this, "merchant_item");
        saveDefaultConfig();
        config = getConfig();
        // ---------------------------------------------
        // LEGACY CODE
        // ---------------------------------------------
        if (config.getDouble("config_version") < 3.1)
        {
            File OldConfigFile = new File(getDataFolder(), "config.yml");
            File BackupFile = new File(getDataFolder(), "old_config.yml");
            try
            {
                String OldContent = new String(java.nio.file.Files.readAllBytes(OldConfigFile.toPath()));
                String Header = "# This is your old config file. It was backed up here because it is incompatible with the current version of Wandering Merchants.\n# You can use this as a reference to reconfigure the new config.yml.\n\n";
                java.nio.file.Files.writeString(BackupFile.toPath(), Header + OldContent);
                OldConfigFile.delete();
            }
            catch (Exception e)
            {
                getLogger().severe("Failed to back up old config: " + e.getMessage());
            }
            saveDefaultConfig();
            reloadConfig();
            getLogger().warning("Old config backed up to old_config.yml. A new default config has been generated.");
            config = getConfig();
        }
        // ---------------------------------------------
        long MerchantDespawnCheckFrequency = config.getInt("merchant_despawn_check_frequency") * 20L;
        SellGUIPermissionRevokeDelay = config.getLong("sellgui_permission_revoke_delay");
        MiniMessage MiniMessageInstance = MiniMessage.miniMessage();
        AllowItemCraftingWithoutMerchant = config.getBoolean("allow_item_crafting_without_merchant");
        NoPermissionMessage = MiniMessageInstance.deserialize(config.getString("messages.no_permission"));
        String RawItemCraftingNotAllowedMessage = config.getString("messages.item_crafting_not_allowed");
        if (RawItemCraftingNotAllowedMessage != null) {
            ItemCraftingNotAllowedMessage = MiniMessageInstance.deserialize(RawItemCraftingNotAllowedMessage);
        }
        SuppressMerchantDeathMessage = config.getBoolean("suppress_merchant_death_message");
        ConfigurationSection WorldsSection = config.getConfigurationSection("worlds");
        if (WorldsSection != null) {
            for (String WorldName : WorldsSection.getKeys(false)) {
                ConfigurationSection WorldSection = WorldsSection.getConfigurationSection(WorldName);
                ConfigurationSection WorldMerchantsSection = WorldSection.getConfigurationSection("merchants");
                List<MerchantConfiguration> EnabledMerchants = new ArrayList<>();
                int TotalRarity = 0;
                if (WorldMerchantsSection != null) {
                    for (String MerchantId : WorldMerchantsSection.getKeys(false)) {
                        ConfigurationSection MerchantSection = WorldMerchantsSection.getConfigurationSection(MerchantId);
                        boolean enabled = MerchantSection.getBoolean("enabled");
                        int rarity = MerchantSection.getInt("rarity");
                        if (enabled) {
                            TotalRarity += rarity;
                        }
                        EnabledMerchants.add(new MerchantConfiguration(MerchantId, rarity, enabled));
                    }
                }
                worlds.add(new WorldConfiguration(WorldName, WorldSection.getInt("merchant_spawn_frequency"), EnabledMerchants, TotalRarity));
            }
        }
        ConfigurationSection MerchantsSection = config.getConfigurationSection("merchants");
        if (MerchantsSection != null) {
            for (String key: MerchantsSection.getKeys(false)) {
                ConfigurationSection MerchantSection = MerchantsSection.getConfigurationSection(key);
                String id = key;
                String name = MerchantSection.getString("name");
                String shop = MerchantSection.getString("shop");
                String SkinValue = MerchantSection.getString("skin_value");
                String SkinSignature = MerchantSection.getString("skin_signature");
                Component DefaultDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("default_dialogue"));
                Component CorrectItemDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("correct_item_dialogue"));
                Component IncorrectItemDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("incorrect_item_dialogue"));
                Component FollowDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("follow_dialogue"));
                Component NonShopkeeperUnfollowDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("non_shopkeeper_unfollow_dialogue"));
                Component ShopkeeperUnfollowDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("shopkeeper_unfollow_dialogue"));
                String RawSpawnText = MerchantSection.getString("spawn_text");
                Component SpawnText = null;
                if (RawSpawnText != null) {
                    SpawnText = MiniMessageInstance.deserialize(RawSpawnText);
                }
                boolean glow = MerchantSection.getBoolean("glow");
                int SpawnRadius = MerchantSection.getInt("spawn_radius");
                long DespawnTime = MerchantSection.getInt("despawn_time") * 1000L;
                ConfigurationSection ItemSection = MerchantSection.getConfigurationSection("item");
                String ItemId = ItemSection.getString("id");
                Component ItemName = MiniMessageInstance.deserialize(ItemSection.getString("name"));
                List<Component> ItemLore = new ArrayList<>();
                for (String line: ItemSection.getStringList("lore")) {
                    ItemLore.add(MiniMessageInstance.deserialize(line));
                }
                String ItemCMDS = ItemSection.getString("custom_model_data");
                ItemStack item = CreateItem(Material.ENCHANTED_BOOK, ItemName, ItemLore, ItemCMDS, ItemId);
                ConfigurationSection RecipeSection = ItemSection.getConfigurationSection("recipe");
                List<String> shape = RecipeSection.getStringList("shape");
                ConfigurationSection IngredientsSection = RecipeSection.getConfigurationSection("ingredients");
                Inventory ItemCraftingRecipeGUI = Bukkit.createInventory(new InventoryOwner("Wandering Merchants Item Crafting Recipe GUI"), 45, ItemName);
                for (int i=0; i<3; i++) {
                    String LetterRow = shape.get(i);
                    for (int j=0; j<3; j++) {
                        char letter = LetterRow.charAt(j);
                        if (letter != ' ') {
                            ItemCraftingRecipeGUI.setItem(12 + i*9 + j, CreateItem(Material.valueOf(IngredientsSection.getString(String.valueOf(letter))), null, null, null, null));
                        }
                    }
                }
                ShapedRecipe ItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, ItemId), item);
                ItemCraftingRecipe.shape(shape.get(0), shape.get(1), shape.get(2));
                for (String ingredient: IngredientsSection.getKeys(false)) {
                    ItemCraftingRecipe.setIngredient(ingredient.charAt(0), Material.valueOf(IngredientsSection.getString(ingredient)));
                }
                Bukkit.addRecipe(ItemCraftingRecipe);
                ConfigurationSection ShopIconSection = MerchantSection.getConfigurationSection("shop_icon");
                Material ShopIconMaterial = Material.valueOf(ShopIconSection.getString("material"));
                Component ShopIconName = MiniMessageInstance.deserialize(ShopIconSection.getString("name"));
                List<Component> ShopIconLore = new ArrayList<>();
                for (String line: ShopIconSection.getStringList("lore")) {
                    ShopIconLore.add(MiniMessageInstance.deserialize(line));
                }
                ItemStack ShopIcon = CreateItem(ShopIconMaterial, ShopIconName, ShopIconLore, null, null);
                ConfigurationSection SellGUIIconSection = MerchantSection.getConfigurationSection("sellgui_icon");
                Material SellGUIIconMaterial = Material.valueOf(SellGUIIconSection.getString("material"));
                Component SellGUIIconName = MiniMessageInstance.deserialize(SellGUIIconSection.getString("name"));
                List<Component> SellGUIIconLore = new ArrayList<>();
                for (String line: SellGUIIconSection.getStringList("lore")) {
                    SellGUIIconLore.add(MiniMessageInstance.deserialize(line));
                }
                ItemStack SellGUIIcon = CreateItem(SellGUIIconMaterial, SellGUIIconName, SellGUIIconLore, null, null);
                Merchant merchant = new Merchant(id, name, shop, SkinValue, SkinSignature, DefaultDialogue, CorrectItemDialogue, IncorrectItemDialogue, FollowDialogue, NonShopkeeperUnfollowDialogue, ShopkeeperUnfollowDialogue, SpawnText, glow, SpawnRadius, DespawnTime, ItemId, ItemName, ItemLore, ItemCMDS, item, ItemCraftingRecipeGUI, ShopIconMaterial, ShopIconName, ShopIconLore, ShopIcon, SellGUIIconMaterial, SellGUIIconName, SellGUIIconLore, SellGUIIcon);
                merchants.add(merchant);
            }
        }
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (NPC npc : CitizensAPI.getNPCRegistry()) {
                MetadataStore NPCData = npc.data();
                String NPCMerchantVariant = NPCData.get("merchant_variant");
                if (NPCMerchantVariant != null && (NPCMerchantVariant.equals("stray") || NPCMerchantVariant.equals("stray_moving"))) {
                    if (System.currentTimeMillis() - (long) NPCData.get("creation_time") >= GetMerchantData(NPCData.get("merchant_id")).DespawnTime) {
                        npc.destroy();
                    }
                }
            }
        }, MerchantDespawnCheckFrequency, MerchantDespawnCheckFrequency);
        tasks.put("merchant_despawner", task);
        for (WorldConfiguration WorldConfiguration : worlds) {
            long MerchantSpawnFrequency = WorldConfiguration.MerchantSpawnFrequency * 20L;
            task = Bukkit.getScheduler().runTaskTimer(this, () -> {
                World world = Bukkit.getWorld(WorldConfiguration.name);
                List<Player> WorldPlayers = world.getPlayers();
                if (!WorldPlayers.isEmpty()) {
                    Player RandomPlayer = WorldPlayers.get(RandomInstance.nextInt(WorldPlayers.size()));
                    String RandomMerchantId = WorldConfiguration.RandomMerchant(RandomInstance);
                    Merchant RandomMerchant = GetMerchantData(RandomMerchantId);
                    if (RandomMerchant != null) {
                        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, RandomMerchant.name);
                        MetadataStore NPCData = npc.data();
                        NPCData.setPersistent("merchant_id", RandomMerchantId);
                        NPCData.setPersistent("merchant_variant", "stray");
                        NPCData.setPersistent("merchant_viewers", 0);
                        NPCData.setPersistent("creation_time", System.currentTimeMillis());
                        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                        npc.getOrAddTrait(LookClose.class).toggle();
                        if (RandomMerchant.glow) {
                            NPCData.setPersistent(NPC.Metadata.GLOWING, true);
                        }
                        npc.setProtected(false);
                        npc.getOrAddTrait(TargetableTrait.class).setTargetable(true);
                        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(RandomMerchant.name, RandomMerchant.SkinSignature, RandomMerchant.SkinValue);
                        int NewX = RandomPlayer.getLocation().blockX() + RandomInstance.nextInt(RandomMerchant.SpawnRadius * 2 + 1) - RandomMerchant.SpawnRadius;
                        int NewZ = RandomPlayer.getLocation().blockZ() + RandomInstance.nextInt(RandomMerchant.SpawnRadius * 2 + 1) - RandomMerchant.SpawnRadius;
                        npc.spawn(new Location(world, NewX + 0.5, world.getHighestBlockYAt(NewX, NewZ) + 1, NewZ + 0.5));
                        if (RandomMerchant.SpawnText != null) {
                            RandomPlayer.sendMessage(RandomMerchant.SpawnText);
                        }
                    }
                    else {
                        getLogger().severe("Tried to spawn unknown merchant " + RandomMerchantId + " in world " + WorldConfiguration.name);
                    }
                }
            }, MerchantSpawnFrequency, MerchantSpawnFrequency);
            tasks.put(WorldConfiguration.name + "_merchant_spawner", task);
        }
        String VersionString = "";
        int VersionInteger = 0;
        HttpURLConnection GithubRequest = null;
        Scanner scanner = null;
        try {
            GithubRequest = (HttpURLConnection) new URI("https://api.github.com/repos/MahinBro001238/WanderingMerchants/releases/latest").toURL().openConnection();
            GithubRequest.setConnectTimeout(5000);
            GithubRequest.setReadTimeout(5000);
            scanner = new Scanner(GithubRequest.getInputStream());
            VersionString = scanner.useDelimiter("\\A").next().split("\"tag_name\":\"v")[1].split("\"")[0];
            scanner.close();
            GithubRequest.disconnect();
            VersionInteger = Integer.parseInt(VersionString.replace(".", ""));
        }
        catch (Exception e) {
            if (GithubRequest != null) {
                GithubRequest.disconnect();
            }
            if (scanner != null) {
                scanner.close();
            }
        }
        if (VersionInteger > 117) {
            UpdateStatus = "There is a new version available: v" + VersionString;
        }
        else if (VersionInteger == 117) {
            UpdateStatus = "You are using the latest version";
        }
        else {
            UpdateStatus = "Unable to check for updates";
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        new Metrics(this, 34647);
    }
    @EventHandler
    public void OnCitizensEnable(CitizensEnableEvent event) {
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            MetadataStore NPCData = npc.data();
            String MerchantId = NPCData.get("merchant_id");
            if (MerchantId != null) {
                Merchant MerchantData = GetMerchantData(MerchantId);
                if (MerchantData != null) {
                    boolean WorldFound = false;
                    for (WorldConfiguration WorldConfiguration : worlds) {
                        if (WorldConfiguration.name.equals(npc.getStoredLocation().getWorld().getName())) {
                            WorldFound = true;
                            String MerchantStatus = WorldConfiguration.MerchantQuery(MerchantId);
                            if (MerchantStatus.equals("Enabled")) {
                                if (NPCData.get("merchant_variant").equals("stray")) {
                                    NPCData.setPersistent("merchant_viewers", 0);
                                    npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                                }
                                npc.setName(MerchantData.name);
                                npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(MerchantData.name, MerchantData.SkinSignature, MerchantData.SkinValue);
                                npc.spawn(npc.getStoredLocation());
                            }
                            else if (MerchantStatus.equals("Disabled")) {
                                npc.despawn();
                            }
                            else {
                                npc.destroy();
                            }
                            break;
                        }
                    }
                    if (!WorldFound) {
                        npc.destroy();
                    }
                }
                else {
                    npc.destroy();
                }
            }
        }
        getLogger().info("=============================================");
        getLogger().info("Wandering Merchants v1.1.7");
        getLogger().info("Made by MahinBro001238");
        getLogger().info("Licensed under the Apache License 2.0");
        getLogger().info("For support: https://discord.gg/8yrbGUCFFD");
        getLogger().info(UpdateStatus);
        getLogger().info("Wandering Merchants has been enabled");
        getLogger().info("=============================================");
        if (worlds.isEmpty()) {
            getLogger().severe("No worlds are defined in the config");
            getLogger().severe("Wandering Merchants will now disable itself");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        else if (merchants.isEmpty()) {
            getLogger().severe("No merchants are defined in the config");
            getLogger().severe("Wandering Merchants will now disable itself");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    @EventHandler
    public void OnItemCraft(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        ItemMeta ItemMeta = result.getItemMeta();
        Player player = (Player) event.getWhoClicked();
        if (ItemMeta != null && ItemMeta.getPersistentDataContainer().has(MerchantItemKey)) {
            if (!AllowItemCraftingWithoutMerchant) {
                String WorldName = player.getWorld().getName();
                boolean WorldFound = false;
                for (WorldConfiguration WorldConfiguration: worlds) {
                    if (WorldConfiguration.name.equals(WorldName)) {
                        WorldFound = true;
                        String ItemId = ItemMeta.getPersistentDataContainer().get(MerchantItemKey, PersistentDataType.STRING);
                        boolean ItemsMerchantFound = false;
                        for (MerchantConfiguration MerchantConfiguration: WorldConfiguration.EnabledMerchants) {
                            String MerchantId = MerchantConfiguration.id;
                            Merchant MerchantData = GetMerchantData(MerchantId);
                            if (MerchantData == null) {
                                getLogger().severe("Found unknown merchant " + MerchantId + " in world " + WorldName + "'s configuration");
                            }
                            else {
                                if (MerchantData.ItemId.equals(ItemId)) {
                                    ItemsMerchantFound = true;
                                    break;
                                }
                            }
                        }
                        if (!ItemsMerchantFound) {
                            event.setCancelled(true);
                            if (ItemCraftingNotAllowedMessage != null) {
                                player.sendMessage(ItemCraftingNotAllowedMessage);
                            }
                        }
                        break;
                    }
                }
                if (!WorldFound) {
                    event.setCancelled(true);
                    if (ItemCraftingNotAllowedMessage != null) {
                        player.sendMessage(ItemCraftingNotAllowedMessage);
                    }
                }
            }
        }
    }
    @EventHandler
    public void OnNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        MetadataStore NPCData = npc.data();
        String NPCMerchantId = NPCData.get("merchant_id");
        if (NPCMerchantId != null) {
            String NPCMerchantVariant = NPCData.get("merchant_variant");
            String NPCName = npc.getName();
            String FollowingName;
            Player player = event.getClicker();
            String PlayerName = player.getName();
            Merchant MerchantData = GetMerchantData(NPCMerchantId);
            Inventory GUI = Bukkit.createInventory(new InventoryOwner(NPCMerchantId + " " + npc.getId() + " " + NPCMerchantVariant), 45, Component.text(NPCName));
            ItemStack TalkToMerchant = CreateItem(Material.PAPER, Component.text("Talk to " + NPCName), null, null, null);
            ItemStack MerchantItemInteraction = CreateItem(Material.ENCHANTED_BOOK, MerchantData.ItemName, List.of(Component.text("Left click to give ").append(MerchantData.ItemName).append(Component.text(" to " + NPCName)), Component.text("Right click to view ").append(MerchantData.ItemName).append(Component.text(" recipe"))), MerchantData.ItemCMDS, null);
            ItemStack ShopIcon = MerchantData.ShopIcon;
            ItemStack SellGUIIcon = MerchantData.SellGUIIcon;
            ItemStack follow = CreateItem(Material.LIME_CONCRETE, Component.text("Follow"), List.of(Component.text("Make " + NPCName + " follow you")), null, null);
            ItemStack unfollow = CreateItem(Material.RED_CONCRETE, Component.text("Unfollow"), List.of(Component.text("Make " + NPCName + " stop following you")), null, null);
            Entity following = npc.getOrAddTrait(FollowTrait.class).getFollowing();
            if (following != null) {
                FollowingName = following.getName();
            }
            else {
                FollowingName = "null";
            }
            ItemStack AlreadyFollowing = CreateItem(Material.WHITE_CONCRETE, Component.text("Following " + FollowingName), List.of(Component.text(NPCName + " is currently following " + FollowingName)), null, null);
            if (NPCMerchantVariant.equals("stray")) {
                npc.getOrAddTrait(Waypoints.class).setWaypointProvider("linear");
                NPCData.setPersistent(NPC.Metadata.GLOWING, false);
                NPCData.setPersistent("merchant_viewers", (int) NPCData.get("merchant_viewers") + 1);
                GUI.setItem(20, TalkToMerchant);
                GUI.setItem(22, MerchantItemInteraction);
                GUI.setItem(24, follow);
            }
            else if (NPCMerchantVariant.equals("stray_moving")) {
                GUI.setItem(20, TalkToMerchant);
                GUI.setItem(22, MerchantItemInteraction);
                if (PlayerName.equals(FollowingName)) {
                    GUI.setItem(24, unfollow);
                }
                else {
                    GUI.setItem(24, AlreadyFollowing);
                }
            }
            else if (NPCMerchantVariant.equals("shopkeeper")) {
                GUI.setItem(20, ShopIcon);
                GUI.setItem(22, SellGUIIcon);
                GUI.setItem(24, follow);
            }
            else {
                GUI.setItem(20, ShopIcon);
                GUI.setItem(22, SellGUIIcon);
                if (PlayerName.equals(FollowingName)) {
                    GUI.setItem(24, unfollow);
                }
                else {
                    GUI.setItem(24, AlreadyFollowing);
                }
            }
            GUI.setItem(36, CreateItem(Material.REDSTONE_BLOCK, Component.text("Health: " + String.format("%.1f", ((LivingEntity) npc.getEntity()).getHealth()) + "/20.0"), null, null, null));
            player.openInventory(GUI);
        }
    }
    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder owner = inventory.getHolder();
        if (owner instanceof InventoryOwner) {
            event.setCancelled(true);
            if (inventory.getItem(20) != null) {
                String[] InventoryOwner = ((InventoryOwner) owner).owner.split(" ");
                NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(InventoryOwner[1]));
                if (npc != null) {
                    MetadataStore NPCData = npc.data();
                    String NPCMerchantVariant = NPCData.get("merchant_variant");
                    if (InventoryOwner[2].equals(NPCMerchantVariant)) {
                        Merchant MerchantData = GetMerchantData(NPCData.get("merchant_id"));
                        Player player = (Player) event.getWhoClicked();
                        int slot = event.getRawSlot();
                        if (slot == 20) {
                            if (NPCMerchantVariant.equals("stray") || NPCMerchantVariant.equals("stray_moving")) {
                                player.sendMessage(MerchantData.DefaultDialogue);
                                player.closeInventory();
                            }
                            else {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "shop " + MerchantData.shop + " " + player.getName());
                            }
                        }
                        else if (slot == 22) {
                            if (NPCMerchantVariant.equals("stray") || NPCMerchantVariant.equals("stray_moving")) {
                                if (event.getClick() == ClickType.RIGHT) {
                                    inventory.setContents(MerchantData.ItemCraftingRecipeGUI.getContents().clone());
                                }
                                else {
                                    ItemStack ItemInHand = player.getInventory().getItemInMainHand();
                                    ItemMeta ItemInHandItemMeta = ItemInHand.getItemMeta();
                                    if (ItemInHandItemMeta != null && MerchantData.ItemId.equals(ItemInHandItemMeta.getPersistentDataContainer().get(MerchantItemKey, PersistentDataType.STRING))) {
                                        ItemInHand.setAmount(ItemInHand.getAmount()-1);
                                        player.sendMessage(MerchantData.CorrectItemDialogue);
                                        NPCData.remove("merchant_viewers");
                                        NPCData.setPersistent("merchant_variant", NPCMerchantVariant.replace("stray", "shopkeeper"));
                                    }
                                    else {
                                        player.sendMessage(MerchantData.IncorrectItemDialogue);
                                    }
                                    player.closeInventory();
                                }
                            }
                            else {
                                PermissionAttachment permission = player.addAttachment(this);
                                permission.setPermission("economyshopgui.sellgui." + MerchantData.shop, true);
                                player.setMetadata("sellgui_permission_container", new FixedMetadataValue(this, permission));
                                player.recalculatePermissions();
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sellgui " + player.getName());
                            }
                        }
                        else if (slot == 24) {
                            if (NPCMerchantVariant.equals("stray") || NPCMerchantVariant.equals("shopkeeper")) {
                                if (npc.getOrAddTrait(FollowTrait.class).getFollowing() == null) {
                                    player.sendMessage(MerchantData.FollowDialogue);
                                    NPCData.setPersistent("merchant_variant", NPCMerchantVariant + "_moving");
                                    NPCData.remove("merchant_viewers");
                                    npc.getOrAddTrait(FollowTrait.class).follow(player);
                                }
                            }
                            else {
                                if (inventory.getItem(24).getType() == Material.RED_CONCRETE) {
                                    NPCData.setPersistent("merchant_variant", NPCMerchantVariant.replace("_moving", ""));
                                    npc.getOrAddTrait(FollowTrait.class).follow(null);
                                    if (NPCMerchantVariant.equals("stray_moving")) {
                                        NPCData.setPersistent("merchant_viewers", 0);
                                        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                                        player.sendMessage(MerchantData.NonShopkeeperUnfollowDialogue);
                                    }
                                    else {
                                        player.sendMessage(MerchantData.ShopkeeperUnfollowDialogue);
                                    }
                                }
                            }
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent event) {
        InventoryHolder owner = event.getInventory().getHolder();
        if (owner instanceof InventoryOwner) {
            String OwnerString = ((InventoryOwner) owner).owner;
            if (!OwnerString.equals("Wandering Merchants Item Crafting Recipe GUI")) {
                NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(OwnerString.split(" ")[1]));
                if (npc != null) {
                    MetadataStore NPCData = npc.data();
                    String NPCMerchantVariant = NPCData.get("merchant_variant");
                    if (NPCMerchantVariant.equals("stray") && ((InventoryOwner) owner).owner.split(" ")[2].equals(NPCMerchantVariant)) {
                        int NPCMerchantViewers = (int) NPCData.get("merchant_viewers") - 1;
                        NPCData.setPersistent("merchant_viewers", NPCMerchantViewers);
                        if (NPCMerchantViewers == 0) {
                            npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                        }
                    }
                }
            }
        }
        else if (owner != null && owner.getClass().getSimpleName().equals("SellGUI")) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                Player player = (Player) event.getPlayer();
                if (player.isOnline()) {
                    if (player.hasMetadata("sellgui_permission_container")) {
                        PermissionAttachment permission = (PermissionAttachment) player.getMetadata("sellgui_permission_container").get(0).value();
                        player.removeAttachment(permission);
                        player.removeMetadata("sellgui_permission_container", Plugin.this);
                        player.recalculatePermissions();
                    }
                }
            }, SellGUIPermissionRevokeDelay);
        }
    }
    @EventHandler
    public void OnNPCDeath(PlayerDeathEvent event) {
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getEntity());
        if (npc != null) {
            if (npc.data().get("merchant_id") != null) {
                npc.destroy();
                if (SuppressMerchantDeathMessage) {
                    event.deathMessage(null);
                }
            }
        }
    }
    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (UpdateStatus.startsWith("There is a new version available: v") && player.hasPermission("wanderingmerchants.wm")) {
            player.sendMessage(Component.text("[Wandering Merchants] " + UpdateStatus, NamedTextColor.YELLOW));
        }
    }
    @Override
    public void onDisable() {
        getLogger().info("Wandering Merchants v1.1.7 has been disabled");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player CommandExecutor) {
            if (CommandExecutor.hasPermission("wanderingmerchants.wm")) {
                if (args.length != 0) {
                    if (args[0].equals("spawn")) {
                        if (args.length == 2 || args.length == 3) {
                            Merchant MerchantData = GetMerchantData(args[1]);
                            if (MerchantData != null) {
                                Player player;
                                if (args.length == 3) {
                                    player = Bukkit.getPlayer(args[2]);
                                }
                                else {
                                    player = CommandExecutor;
                                }
                                if (player != null) {
                                    NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, MerchantData.name);
                                    MetadataStore NPCData = npc.data();
                                    NPCData.setPersistent("merchant_id", MerchantData.id);
                                    NPCData.setPersistent("merchant_variant", "stray");
                                    NPCData.setPersistent("merchant_viewers", 0);
                                    NPCData.setPersistent("creation_time", System.currentTimeMillis());
                                    npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                                    npc.getOrAddTrait(LookClose.class).toggle();
                                    if (MerchantData.glow) {
                                        NPCData.setPersistent(NPC.Metadata.GLOWING, true);
                                    }
                                    npc.setProtected(false);
                                    npc.getOrAddTrait(TargetableTrait.class).setTargetable(true);
                                    npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(MerchantData.name, MerchantData.SkinSignature, MerchantData.SkinValue);
                                    npc.spawn(player.getLocation());
                                    if (MerchantData.SpawnText != null) {
                                        player.sendMessage(MerchantData.SpawnText);
                                    }
                                    if (args.length == 3) {
                                        CommandExecutor.sendMessage(Component.text("Spawned " + MerchantData.id + " at " + player.getName() + "'s location", NamedTextColor.GOLD));
                                    }
                                    else {
                                        CommandExecutor.sendMessage(Component.text("Spawned " + MerchantData.id + "", NamedTextColor.GOLD));
                                    }
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Unknown player: " + args[2], NamedTextColor.RED));
                                }

                            }
                            else {
                                CommandExecutor.sendMessage(Component.text("Unknown merchant: " + args[1], NamedTextColor.RED));
                            }
                        }
                        else {
                            CommandExecutor.sendMessage(Component.text("Usage: /wm spawn <merchant id> [player]", NamedTextColor.RED));
                        }
                    }
                    else if (args[0].equals("give")) {
                        if (args.length == 2 || args.length == 3) {
                            Merchant MerchantData = GetMerchantData(args[1]);
                            if (MerchantData != null) {
                                Player player;
                                if (args.length == 3) {
                                    player = Bukkit.getPlayer(args[2]);
                                }
                                else {
                                    player = CommandExecutor;
                                }
                                if (player != null) {
                                    if (player.getInventory().addItem(MerchantData.item).isEmpty()) {
                                        if (args.length == 3) {
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("Gave a ", NamedTextColor.GOLD)).append(MerchantData.ItemName).append(Component.text(" to " + player.getName() + "", NamedTextColor.GOLD)));
                                        }
                                        else {
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("Gave a ", NamedTextColor.GOLD)).append(MerchantData.ItemName).append(Component.text("", NamedTextColor.GOLD)));
                                        }
                                    }
                                    else {
                                        if (args.length == 3) {
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("Cannot give ", NamedTextColor.RED)).append(MerchantData.ItemName).append(Component.text(" to " + player.getName() + ", their inventory is full", NamedTextColor.RED)));
                                        }
                                        else {
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("Cannot give ", NamedTextColor.RED)).append(MerchantData.ItemName).append(Component.text(", your inventory is full", NamedTextColor.RED)));
                                        }
                                    }
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Unknown player: " + args[2], NamedTextColor.RED));
                                }
                            }
                            else {
                                CommandExecutor.sendMessage(Component.text("Unknown merchant: " + args[1], NamedTextColor.RED));
                            }
                        }
                        else {
                            CommandExecutor.sendMessage(Component.text("Usage: /wm give <merchant id> [player]", NamedTextColor.RED));
                        }
                    }
                    else if (args[0].equals("recipe")) {
                        if (args.length == 2 || args.length == 3) {
                            Merchant MerchantData = GetMerchantData(args[1]);
                            if (MerchantData != null) {
                                Player player;
                                if (args.length == 3) {
                                    player = Bukkit.getPlayer(args[2]);
                                }
                                else {
                                    player = CommandExecutor;
                                }
                                if (player != null) {
                                    player.openInventory(MerchantData.ItemCraftingRecipeGUI);
                                    if (args.length == 3) {
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Opened crafting recipe GUI of ", NamedTextColor.GOLD)).append(MerchantData.ItemName).append(Component.text(" for " + player.getName() + "", NamedTextColor.GOLD)));
                                    }
                                    else {
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Opened crafting recipe GUI of ", NamedTextColor.GOLD)).append(MerchantData.ItemName));
                                    }
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Unknown player: " + args[2], NamedTextColor.RED));
                                }
                            }
                            else {
                                CommandExecutor.sendMessage(Component.text("Unknown merchant: " + args[1], NamedTextColor.RED));
                            }
                        }
                        else {
                            CommandExecutor.sendMessage(Component.text("Usage: /wm recipe <merchant id> [player]", NamedTextColor.RED));
                        }
                    }
                    else if (args[0].equals("get")) {
                        if (args.length == 2 || args.length == 3) {
                            if (args[1].equals("merchant_data")) {
                                if (args.length == 3) {
                                    Merchant MerchantData = GetMerchantData(args[2]);
                                    if (MerchantData != null) {
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("--- Merchant Data ---", NamedTextColor.GOLD)));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("ID: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.id)));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Name: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.name)));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shop: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.shop)));
                                        String SkinValue = MerchantData.SkinValue;
                                        if (SkinValue == null) {
                                            SkinValue = "null";
                                        }
                                        else {
                                            SkinValue = SkinValue.substring(0, 20) + "...";
                                        }
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Skin Value: ", NamedTextColor.GOLD)).append(Component.text(SkinValue)));
                                        String SkinSignature = MerchantData.SkinSignature;
                                        if (SkinSignature == null) {
                                            SkinSignature = "null";
                                        }
                                        else {
                                            SkinSignature = SkinSignature.substring(0, 20) + "...";
                                        }
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Skin Signature: ", NamedTextColor.GOLD)).append(Component.text(SkinSignature)));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Default Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.DefaultDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Correct Item Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.CorrectItemDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Incorrect Item Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.IncorrectItemDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Follow Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.FollowDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Non Shopkeeper Unfollow Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.NonShopkeeperUnfollowDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shopkeeper Unfollow Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.ShopkeeperUnfollowDialogue));
                                        Component SpawnText = MerchantData.SpawnText;
                                        if (SpawnText == null) {
                                            SpawnText = Component.text("null");
                                        }
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Spawn Text: ", NamedTextColor.GOLD)).append(SpawnText));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Glow: ", NamedTextColor.GOLD)).append(Component.text(String.valueOf(MerchantData.glow))));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item ID: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.ItemId)));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item Name: ", NamedTextColor.GOLD)).append(MerchantData.ItemName));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item Custom Model Data: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.ItemCMDS)));
                                        List<Component> ItemLore = MerchantData.ItemLore;
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item Lore:", NamedTextColor.GOLD)));
                                        for (int i = 0; i < ItemLore.size(); i++) {
                                            CommandExecutor.sendMessage(ItemLore.get(i));
                                        }
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item Crafting Recipe: ", NamedTextColor.GOLD)).append(Component.text("[View Recipe]", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/wm recipe " + MerchantData.id))));
                                        List<Component> ShopIconLore = MerchantData.ShopIconLore;
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shop Icon Material: ", NamedTextColor.GOLD)).append(Component.text(String.valueOf(MerchantData.ShopIconMaterial))));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shop Icon Name: ", NamedTextColor.GOLD)).append(MerchantData.ShopIconName));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shop Icon Lore:", NamedTextColor.GOLD)));
                                        for (int i = 0; i < ShopIconLore.size(); i++) {
                                            CommandExecutor.sendMessage(ShopIconLore.get(i));
                                        }
                                        List<Component> SellIconLore = MerchantData.SellIconLore;
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Sell Icon Material: ", NamedTextColor.GOLD)).append(Component.text(String.valueOf(MerchantData.SellIconMaterial))));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Sell Icon Name: ", NamedTextColor.GOLD)).append(MerchantData.SellIconName));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Sell Icon Lore:", NamedTextColor.GOLD)));
                                        for (int i = 0; i < SellIconLore.size(); i++) {
                                            CommandExecutor.sendMessage(SellIconLore.get(i));
                                        }
                                    }
                                    else {
                                        CommandExecutor.sendMessage(Component.text("Unknown merchant: " + args[2], NamedTextColor.RED));
                                    }
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Usage: /wm get merchant_data <merchant id>", NamedTextColor.RED));
                                }
                            }
                            else if (args[1].equals("npc_data")) {
                                if (args.length == 3) {
                                    try {
                                        NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
                                        if (npc != null) {
                                            MetadataStore NPCData = npc.data();
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("--- NPC Data ---", NamedTextColor.GOLD)));
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("merchant_id: ", NamedTextColor.GOLD)).append(Component.text(String.valueOf((Object) NPCData.get("merchant_id")))));
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("merchant_variant: ", NamedTextColor.GOLD)).append(Component.text(String.valueOf((Object) NPCData.get("merchant_variant")))));
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("merchant_viewers: ", NamedTextColor.GOLD)).append(Component.text(String.valueOf((Object) NPCData.get("merchant_viewers")))));
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("creation_time: ", NamedTextColor.GOLD)).append(Component.text(String.valueOf((Object) NPCData.get("creation_time")))));
                                            Entity following = npc.getOrAddTrait(FollowTrait.class).getFollowing();
                                            String FollowingName;
                                            if (following != null) {
                                                FollowingName = following.getName();
                                            }
                                            else {
                                                FollowingName = "null";
                                            }
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("Currently following: ", NamedTextColor.GOLD)).append(Component.text(FollowingName)));
                                        }
                                        else {
                                            CommandExecutor.sendMessage(Component.text("Unknown NPC id: " + args[2], NamedTextColor.RED));
                                        }
                                    }
                                    catch (Exception e) {
                                        CommandExecutor.sendMessage(Component.text("Invalid NPC id: " + args[2] + ", " + e, NamedTextColor.RED));
                                    }
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Usage: /wm get npc_data <npc id>", NamedTextColor.RED));
                                }
                            }
                            else if (args[1].equals("item_data")) {
                                if (args.length == 2) {
                                    ItemStack ItemInHand = CommandExecutor.getInventory().getItemInMainHand();
                                    ItemMeta meta = ItemInHand.getItemMeta();
                                    if (meta != null) {
                                        String id = meta.getPersistentDataContainer().get(MerchantItemKey, PersistentDataType.STRING);
                                        List<String> CMD = meta.getCustomModelDataComponent().getStrings();
                                        String CMDS;
                                        if (!CMD.isEmpty()) {
                                            CMDS = CMD.get(0); 
                                        }
                                        else {
                                            CMDS = "null";
                                        }
                                        CommandExecutor.sendMessage(Component.text("--- Item Data ---", NamedTextColor.GOLD));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("ID: ", NamedTextColor.GOLD)).append(Component.text(String.valueOf(id))));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Custom Model Data: ", NamedTextColor.GOLD)).append(Component.text(CMDS)));
                                    }
                                    else {
                                        CommandExecutor.sendMessage(Component.text("Your hand is empty", NamedTextColor.RED));
                                    }
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Usage: /wm get item_data", NamedTextColor.RED));
                                }
                            }
                            else {
                                CommandExecutor.sendMessage(Component.text("Invalid subcommand: " + args[1], NamedTextColor.RED));
                                CommandExecutor.sendMessage(Component.text("Usage: /wm get <merchant_data|npc_data|item_data>", NamedTextColor.RED));
                            }
                        }
                        else {
                            CommandExecutor.sendMessage(Component.text("Usage: /wm get <merchant_data|npc_data|item_data>", NamedTextColor.RED));
                        }
                    }
                    else if (args[0].equals("set")) {
                        if (args.length == 4 || args.length == 5) {
                            if (args[1].equals("npc_data")) {
                                if (args.length == 5) {
                                    try {
                                        NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
                                        if (npc != null) {
                                            MetadataStore NPCData = npc.data();
                                            if (args[3].equals("merchant_id")) {
                                                if (args[4].equals("NULL")) {
                                                    NPCData.remove("merchant_id");
                                                    CommandExecutor.sendMessage(Component.text("Removed merchant_id", NamedTextColor.GOLD));
                                                }
                                                else {
                                                    NPCData.setPersistent("merchant_id", args[4]);
                                                    CommandExecutor.sendMessage(Component.text("Set merchant_id to " + args[4], NamedTextColor.GOLD));
                                                }
                                            }
                                            else if (args[3].equals("merchant_variant")) {
                                                if (args[4].equals("stray") || args[4].equals("stray_moving") || args[4].equals("shopkeeper") || args[4].equals("shopkeeper_moving") || args[4].equals("NULL")) {
                                                    if (args[4].equals("NULL")) {
                                                        NPCData.remove("merchant_variant");
                                                        CommandExecutor.sendMessage(Component.text("Removed merchant_variant", NamedTextColor.GOLD));
                                                    }
                                                    else {
                                                        NPCData.setPersistent("merchant_variant", args[4]);
                                                        CommandExecutor.sendMessage(Component.text("Set merchant_variant to " + args[4], NamedTextColor.GOLD));
                                                    }
                                                }
                                                else {
                                                    CommandExecutor.sendMessage(Component.text("Invalid merchant_variant: " + args[4], NamedTextColor.RED));
                                                    CommandExecutor.sendMessage(Component.text("Usage: /wm set npc_data <npc id> <merchant_variant> <stray|stray_moving|shopkeeper|shopkeeper_moving|NULL>", NamedTextColor.RED));
                                                }
                                            }
                                            else if (args[3].equals("merchant_viewers")) {
                                                if (args[4].equals("NULL")) {
                                                    NPCData.remove("merchant_viewers");
                                                    CommandExecutor.sendMessage(Component.text("Removed merchant_viewers", NamedTextColor.GOLD));
                                                }
                                                else {
                                                    try {
                                                        NPCData.setPersistent("merchant_viewers", Integer.parseInt(args[4]));
                                                        CommandExecutor.sendMessage(Component.text("Set merchant_viewers to: " + args[4], NamedTextColor.GOLD));
                                                    }
                                                    catch (Exception e) {
                                                        CommandExecutor.sendMessage(Component.text("Invalid merchant_viewers: " + args[4], NamedTextColor.RED));
                                                        CommandExecutor.sendMessage(Component.text("merchant_viewers must be a integer", NamedTextColor.RED));
                                                    }
                                                }
                                            }
                                            else if (args[3].equals("creation_time")) {
                                                if (args[4].equals("NULL")) {
                                                    NPCData.remove("creation_time");
                                                    CommandExecutor.sendMessage(Component.text("Removed creation_time", NamedTextColor.GOLD));
                                                }
                                                else {
                                                    try {
                                                        NPCData.setPersistent("creation_time", Long.parseLong(args[4]));
                                                        CommandExecutor.sendMessage(Component.text("Set creation_time to " + args[4], NamedTextColor.GOLD));
                                                    }
                                                    catch (Exception e) {
                                                        CommandExecutor.sendMessage(Component.text("Invalid creation_time: " + args[4], NamedTextColor.RED));
                                                        CommandExecutor.sendMessage(Component.text("creation_time must be a long", NamedTextColor.RED));
                                                    }
                                                }
                                            }
                                            else if (args[3].equals("following")) {
                                                if (args[4].equals("NULL")) {
                                                    npc.getOrAddTrait(FollowTrait.class).follow(null);
                                                    CommandExecutor.sendMessage(Component.text("The NPC is no longer following anyone", NamedTextColor.GOLD));
                                                }
                                                else {
                                                    Player player = Bukkit.getPlayer(args[4]);
                                                    if (player != null) {
                                                        npc.getOrAddTrait(FollowTrait.class).follow(player);
                                                        CommandExecutor.sendMessage(Component.text("The NPC is now following " + player.getName() + "", NamedTextColor.GOLD));
                                                    }
                                                    else {
                                                        CommandExecutor.sendMessage(Component.text("Unknown player: " + args[4], NamedTextColor.RED));
                                                    }
                                                }
                                            }
                                            else {
                                                CommandExecutor.sendMessage(Component.text("Invalid subcommand: " + args[3], NamedTextColor.RED));
                                                CommandExecutor.sendMessage(Component.text("Usage: /wm set npc_data <npc id> <merchant_id|merchant_variant|merchant_viewers|creation_time|following> <value|player|NULL>", NamedTextColor.RED));
                                            }
                                        }
                                        else {
                                            CommandExecutor.sendMessage(Component.text("Unknown NPC id: " + args[2], NamedTextColor.RED));
                                        }
                                    }
                                    catch (Exception e) {
                                        CommandExecutor.sendMessage(Component.text("Invalid NPC id: " + args[2], NamedTextColor.RED));
                                    }
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Usage: /wm set npc_data <npc id> <merchant_id|merchant_variant|merchant_viewers|creation_time|following> <value|player|NULL>", NamedTextColor.RED));
                                }
                            }
                            else if (args[1].equals("item_data")) {
                                if (args.length == 4) {
                                    if (args[2].equals("id") || args[2].equals("cmd")) {
                                        ItemStack ItemInHand = CommandExecutor.getInventory().getItemInMainHand();
                                        ItemMeta meta = ItemInHand.getItemMeta();
                                        if (meta != null) {
                                            if (args[2].equals("id")) {
                                                PersistentDataContainer PDC = meta.getPersistentDataContainer();
                                                if (args[3].equals("NULL")) {
                                                    PDC.remove(MerchantItemKey);
                                                    CommandExecutor.sendMessage(Component.text("Removed item ID", NamedTextColor.GOLD));
                                                }
                                                else {
                                                    PDC.set(MerchantItemKey, PersistentDataType.STRING, args[3]);
                                                    CommandExecutor.sendMessage(Component.text("Item ID set to: " + args[3], NamedTextColor.GOLD));
                                                }
                                            }
                                            else {
                                                CustomModelDataComponent CMDS = meta.getCustomModelDataComponent();
                                                if (args[3].equals("NULL")) {
                                                    CMDS.setStrings(List.of());
                                                    CommandExecutor.sendMessage(Component.text("Removed custom model data", NamedTextColor.GOLD));
                                                }
                                                else {
                                                    CMDS.setStrings(List.of(args[3]));
                                                    CommandExecutor.sendMessage(Component.text("Custom model data set to: " + args[3], NamedTextColor.GOLD));
                                                }
                                                meta.setCustomModelDataComponent(CMDS);
                                            }
                                            ItemInHand.setItemMeta(meta);
                                        }
                                        else {
                                            CommandExecutor.sendMessage(Component.text("Your hand is empty", NamedTextColor.RED));
                                        }
                                    }
                                    else {
                                        CommandExecutor.sendMessage(Component.text("Invalid subcommand: " + args[2], NamedTextColor.RED));
                                        CommandExecutor.sendMessage(Component.text("Usage: /wm set item_data <id|cmd> <value|NULL>", NamedTextColor.RED));
                                    }
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Usage: /wm set item_data <id|cmd> <value|NULL>", NamedTextColor.RED));
                                }
                            }
                            else {
                                CommandExecutor.sendMessage(Component.text("Invalid subcommand: " + args[1], NamedTextColor.RED));
                                CommandExecutor.sendMessage(Component.text("Usage: /wm set <npc_data|item_data> <field> <value|NULL>", NamedTextColor.RED));
                            }
                        }
                        else {
                            CommandExecutor.sendMessage(Component.text("Usage: /wm set <npc_data|item_data> <field> <value|NULL>", NamedTextColor.RED));
                        }
                    }
                    else {
                        CommandExecutor.sendMessage(Component.text("Invalid subcommand: " + args[0], NamedTextColor.RED));
                        CommandExecutor.sendMessage(Component.text("Usage: /wm <spawn|give|recipe|get|set>", NamedTextColor.RED));
                    }
                }
                else {
                    CommandExecutor.sendMessage(Component.text("Usage: /wm <spawn|give|recipe|get|set>", NamedTextColor.RED));
                }
            }
            else {
                CommandExecutor.sendMessage(NoPermissionMessage);
            }
        }
        else {
            sender.sendMessage("This command can only be executed by a player");
        }
        return true;
    }
}
