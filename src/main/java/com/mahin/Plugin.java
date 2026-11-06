package com.mahin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
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
import org.bukkit.event.player.PlayerQuitEvent;
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
    private NamespacedKey MerchantItemKey = new NamespacedKey(this, "merchant_item");
    private Map<String, BukkitTask> tasks = new HashMap<>();
    private long MerchantDespawnCheckFrequency;
    private long SellGUIPermissionRevokeDelay;
    private boolean AllowItemCraftingWithoutMerchant;
    private boolean SuppressMerchantDeathMessage;
    private Component NoPermissionMessage;
    private Component ItemCraftingNotAllowedMessage;
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
        public List<MerchantConfiguration> UsedMerchants;
        public int TotalRarity;
        public WorldConfiguration(String name, int MerchantSpawnFrequency, List<MerchantConfiguration> UsedMerchants, int TotalRarity) {
            this.name = name;
            this.MerchantSpawnFrequency = MerchantSpawnFrequency;
            this.UsedMerchants = UsedMerchants;
            this.TotalRarity = TotalRarity;
        }
        public String MerchantQuery(String MerchantId) {
            for (MerchantConfiguration MerchantConfiguration: UsedMerchants) {
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
            for (MerchantConfiguration MerchantConfiguration: UsedMerchants) {
                if (MerchantConfiguration.enabled) {
                    RandomValue -= MerchantConfiguration.rarity;
                    if (RandomValue <= 0) {
                        return MerchantConfiguration.id;
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
        public Component SpawnText;
        public Component InteractionDialogue;
        public Component CorrectItemDialogue;
        public Component IncorrectItemDialogue;
        public Component FollowDialogue;
        public Component NonShopkeeperUnfollowDialogue;
        public Component ShopkeeperUnfollowDialogue;
        public int LostDistance;
        public Component StuckDialogue;
        public Component NonShopkeeperLostDialogue;
        public Component ShopkeeperLostDialogue;
        public boolean glow;
        public int SpawnRadius;
        public int DespawnTime;
        public String ItemId;
        public Component ItemName;
        public List<Component> ItemLore;
        public String ItemCMD;
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
        public Merchant(String id, String name, String shop, String SkinValue, String SkinSignature, Component SpawnText, Component InteractionDialogue, Component CorrectItemDialogue, Component IncorrectItemDialogue, Component FollowDialogue, Component NonShopkeeperUnfollowDialogue, Component ShopkeeperUnfollowDialogue, int LostDistance, Component StuckDialogue, Component NonShopkeeperLostDialogue, Component ShopkeeperLostDialogue, boolean glow, int SpawnRadius, int DespawnTime, String ItemId, Component ItemName, List<Component> ItemLore, String ItemCMD, ItemStack item, Inventory ItemCraftingRecipeGUI, Material ShopIconMaterial, Component ShopIconName, List<Component> ShopIconLore, ItemStack ShopIcon, Material SellIconMaterial, Component SellIconName, List<Component> SellIconLore, ItemStack SellGUIIcon) {
            this.id = id;
            this.name = name;
            this.shop = shop;
            this.SkinValue = SkinValue;
            this.SkinSignature = SkinSignature;
            this.SpawnText = SpawnText;
            this.InteractionDialogue = InteractionDialogue;
            this.CorrectItemDialogue = CorrectItemDialogue;
            this.IncorrectItemDialogue = IncorrectItemDialogue;
            this.FollowDialogue = FollowDialogue;
            this.NonShopkeeperUnfollowDialogue = NonShopkeeperUnfollowDialogue;
            this.ShopkeeperUnfollowDialogue = ShopkeeperUnfollowDialogue;
            this.LostDistance = LostDistance;
            this.StuckDialogue = StuckDialogue;
            this.NonShopkeeperLostDialogue = NonShopkeeperLostDialogue;
            this.ShopkeeperLostDialogue = ShopkeeperLostDialogue;
            this.glow = glow;
            this.SpawnRadius = SpawnRadius;
            this.DespawnTime = DespawnTime;
            this.ItemId = ItemId;
            this.ItemName = ItemName;
            this.ItemLore = ItemLore;
            this.ItemCMD = ItemCMD;
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
    private ItemStack CreateItem(Material material, Component name, List<Component> lore, String CMD, String key) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.displayName(name);
        }
        if (lore != null) {
            meta.lore(lore);
        }
        if (CMD != null) {
            CustomModelDataComponent ItemCMD = meta.getCustomModelDataComponent();
            ItemCMD.setStrings(List.of(CMD));
            meta.setCustomModelDataComponent(ItemCMD);
        }
        if (key != null) {
            meta.getPersistentDataContainer().set(MerchantItemKey, PersistentDataType.STRING, key);
        }
        item.setItemMeta(meta);
        return item;
    }
    private void CreateMerchant(Merchant MerchantData, Location location, Player player) {
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
        npc.spawn(location);
        npc.getNavigator().getDefaultParameters().stuckAction((StuckNPC, navigator) -> {
            MetadataStore StuckNPCData = StuckNPC.data();
            String StuckNPCMerchantVariant = StuckNPCData.get("merchant_variant");
            if (StuckNPCMerchantVariant.endsWith("_moving")) {
                Merchant StuckMerchantData = GetMerchantData(StuckNPCData.get("merchant_id"));
                Player FollowingPlayer = (Player) StuckNPC.getOrAddTrait(FollowTrait.class).getFollowing();
                Location StuckNPCLocation = StuckNPC.getStoredLocation();
                Location FollowingPlayerLocation = FollowingPlayer.getLocation();
                if (StuckNPCLocation.getWorld().equals(FollowingPlayerLocation.getWorld()) && StuckNPCLocation.distance(FollowingPlayerLocation) <= StuckMerchantData.LostDistance) {
                    FollowingPlayer.sendMessage(StuckMerchantData.StuckDialogue);
                }
                else {
                    StuckNPCData.setPersistent("merchant_variant", StuckNPCMerchantVariant.replace("_moving", ""));
                    StuckNPC.getOrAddTrait(FollowTrait.class).follow(null);
                    if (StuckNPCMerchantVariant.equals("stray_moving")) {
                        StuckNPCData.setPersistent("merchant_viewers", 0);
                        StuckNPC.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                        FollowingPlayer.sendMessage(StuckMerchantData.NonShopkeeperLostDialogue);
                    }
                    else {
                        FollowingPlayer.sendMessage(StuckMerchantData.ShopkeeperLostDialogue);
                    }
                    return false;
                }
            }
            return true;
        }).stationaryTicks(60);
        if (MerchantData.SpawnText != null) {
            player.sendMessage(MerchantData.SpawnText);
        }
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
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        // ---------------------------------------------
        // LEGACY CODE
        // ---------------------------------------------
        if (config.getDouble("config_version") < 3.3)
        {
            File OldConfigFile = new File(getDataFolder(), "config.yml");
            File BackupFile = new File(getDataFolder(), "old_config_3.yml");
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
            getLogger().warning("Old config backed up to old_config_3.yml. A new default config has been generated.");
            config = getConfig();
        }
        // ---------------------------------------------
        MerchantDespawnCheckFrequency = config.getInt("merchant_despawn_check_frequency") * 20L;
        tasks.put("merchant_despawner", Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (NPC npc: CitizensAPI.getNPCRegistry()) {
                MetadataStore NPCData = npc.data();
                String NPCMerchantVariant = NPCData.get("merchant_variant");
                if (NPCMerchantVariant != null && (NPCMerchantVariant.equals("stray") || NPCMerchantVariant.equals("stray_moving"))) {
                    if (System.currentTimeMillis() - (long) NPCData.get("creation_time") >= GetMerchantData(NPCData.get("merchant_id")).DespawnTime * 1000L) {
                        npc.destroy();
                    }
                }
            }
        }, MerchantDespawnCheckFrequency, MerchantDespawnCheckFrequency));
        SellGUIPermissionRevokeDelay = config.getLong("sellgui_permission_revoke_delay");
        AllowItemCraftingWithoutMerchant = config.getBoolean("allow_item_crafting_without_merchant");
        SuppressMerchantDeathMessage = config.getBoolean("suppress_merchant_death_message");
        MiniMessage MiniMessageInstance = MiniMessage.miniMessage();
        NoPermissionMessage = MiniMessageInstance.deserialize(config.getString("messages.no_permission"));
        String RawItemCraftingNotAllowedMessage = config.getString("messages.item_crafting_not_allowed");
        if (RawItemCraftingNotAllowedMessage != null) {
            ItemCraftingNotAllowedMessage = MiniMessageInstance.deserialize(RawItemCraftingNotAllowedMessage);
        }
        ConfigurationSection MerchantsSection = config.getConfigurationSection("merchants");
        HashMap<String, String> ItemIds = new HashMap<>();
        for (String id: MerchantsSection.getKeys(false)) {
            ConfigurationSection MerchantSection = MerchantsSection.getConfigurationSection(id);
            String name = MerchantSection.getString("name");
            String shop = MerchantSection.getString("shop");
            String SkinValue = MerchantSection.getString("skin_value");
            String SkinSignature = MerchantSection.getString("skin_signature");
            String RawSpawnText = MerchantSection.getString("spawn_text");
            Component SpawnText = null;
            if (RawSpawnText != null) {
                SpawnText = MiniMessageInstance.deserialize(RawSpawnText);
            }
            Component InteractionDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("interaction_dialogue"));
            Component CorrectItemDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("correct_item_dialogue"));
            Component IncorrectItemDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("incorrect_item_dialogue"));
            Component FollowDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("follow_dialogue"));
            Component NonShopkeeperUnfollowDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("non_shopkeeper_unfollow_dialogue"));
            Component ShopkeeperUnfollowDialogue = MiniMessageInstance.deserialize(MerchantSection.getString("shopkeeper_unfollow_dialogue"));
            int LostDistance = MerchantSection.getInt("lost_distance");
            Component StuckDialogue = MerchantSection.getString("stuck_dialogue") != null ? MiniMessageInstance.deserialize(MerchantSection.getString("stuck_dialogue")) : null;
            Component NonShopkeeperLostDialogue = MerchantSection.getString("non_shopkeeper_lost_dialogue") != null ? MiniMessageInstance.deserialize(MerchantSection.getString("non_shopkeeper_lost_dialogue")) : null;
            Component ShopkeeperLostDialogue = MerchantSection.getString("shopkeeper_lost_dialogue") != null ? MiniMessageInstance.deserialize(MerchantSection.getString("shopkeeper_lost_dialogue")) : null;
            boolean glow = MerchantSection.getBoolean("glow");
            int SpawnRadius = MerchantSection.getInt("spawn_radius");
            int DespawnTime = MerchantSection.getInt("despawn_time");
            ConfigurationSection ItemSection = MerchantSection.getConfigurationSection("item");
            String ItemId = ItemSection.getString("id");
            for (HashMap.Entry<String, String> record: ItemIds.entrySet()) {
                if (record.getValue().equals(ItemId)) {
                    getLogger().severe("Merchant " + id + " and merchant " + record.getKey() + " have the same item ID: " + ItemId);
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
            }
            ItemIds.put(id, ItemId);
            Component ItemName = MiniMessageInstance.deserialize(ItemSection.getString("name"));
            List<Component> ItemLore = new ArrayList<>();
            for (String line: ItemSection.getStringList("lore")) {
                ItemLore.add(MiniMessageInstance.deserialize(line));
            }
            String ItemCMD = ItemSection.getString("custom_model_data");
            ItemStack item = CreateItem(Material.ENCHANTED_BOOK, ItemName, ItemLore, ItemCMD, ItemId);
            ConfigurationSection RecipeSection = ItemSection.getConfigurationSection("recipe");
            List<String> shape = RecipeSection.getStringList("shape");
            ConfigurationSection IngredientsSection = RecipeSection.getConfigurationSection("ingredients");
            Inventory ItemCraftingRecipeGUI = Bukkit.createInventory(new InventoryOwner("Wandering Merchants Item Crafting Recipe GUI"), 45, ItemName);
            for (int i=0; i<3; i++) {
                String LetterRow = shape.get(i);
                for (int j=0; j<3; j++) {
                    char letter = LetterRow.charAt(j);
                    if (letter != ' ') {
                        ItemCraftingRecipeGUI.setItem(12 + i*9 + j, CreateItem(Material.valueOf(IngredientsSection.getString(Character.toString(letter))), null, null, null, null));
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
            Merchant merchant = new Merchant(id, name, shop, SkinValue, SkinSignature, SpawnText, InteractionDialogue, CorrectItemDialogue, IncorrectItemDialogue, FollowDialogue, NonShopkeeperUnfollowDialogue, ShopkeeperUnfollowDialogue, LostDistance, StuckDialogue, NonShopkeeperLostDialogue, ShopkeeperLostDialogue, glow, SpawnRadius, DespawnTime, ItemId, ItemName, ItemLore, ItemCMD, item, ItemCraftingRecipeGUI, ShopIconMaterial, ShopIconName, ShopIconLore, ShopIcon, SellGUIIconMaterial, SellGUIIconName, SellGUIIconLore, SellGUIIcon);
            merchants.add(merchant);
        }
        if (merchants.isEmpty()) {
            getLogger().severe("You must have atleast one merchant defined");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        ConfigurationSection WorldsSection = config.getConfigurationSection("worlds");
        if (WorldsSection != null) {
            for (String WorldName: WorldsSection.getKeys(false)) {
                World world = Bukkit.getWorld(WorldName);
                if (world == null) {
                    getLogger().severe("World " + WorldName + " does not exist");
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
                ConfigurationSection WorldSection = WorldsSection.getConfigurationSection(WorldName);
                ConfigurationSection WorldMerchantsSection = WorldSection.getConfigurationSection("merchants");
                List<MerchantConfiguration> UsedMerchants = new ArrayList<>();
                int TotalRarity = 0;
                boolean HasEnabledMerchants = false;
                for (String MerchantId: WorldMerchantsSection.getKeys(false)) {
                    if (GetMerchantData(MerchantId) == null) {
                        getLogger().severe("World " + WorldName + " uses an unknown merchant: " + MerchantId);
                        Bukkit.getPluginManager().disablePlugin(this);
                        return;
                    }
                    ConfigurationSection MerchantSection = WorldMerchantsSection.getConfigurationSection(MerchantId);
                    boolean enabled = MerchantSection.getBoolean("enabled");
                    int rarity = MerchantSection.getInt("rarity");
                    if (enabled) {
                        TotalRarity += rarity;
                        HasEnabledMerchants = true;
                    }
                    UsedMerchants.add(new MerchantConfiguration(MerchantId, rarity, enabled));
                }
                if (UsedMerchants.isEmpty()) {
                    getLogger().severe("World " + WorldName + " must have atleast one merchant listed");
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }
                WorldConfiguration WorldConfiguration = new WorldConfiguration(WorldName, WorldSection.getInt("merchant_spawn_frequency"), UsedMerchants, TotalRarity);
                if (HasEnabledMerchants) {
                    if (TotalRarity != 0) {
                        long MerchantSpawnFrequency = WorldConfiguration.MerchantSpawnFrequency * 20L;
                        tasks.put(WorldConfiguration.name + "_merchant_spawner", Bukkit.getScheduler().runTaskTimer(this, () -> {
                            List<Player> WorldPlayers = world.getPlayers();
                            if (!WorldPlayers.isEmpty()) {
                                Player RandomPlayer = WorldPlayers.get(RandomInstance.nextInt(WorldPlayers.size()));
                                Merchant MerchantData = GetMerchantData(WorldConfiguration.RandomMerchant(RandomInstance));
                                int NewX = RandomPlayer.getLocation().blockX() + RandomInstance.nextInt(MerchantData.SpawnRadius * 2 + 1) - MerchantData.SpawnRadius;
                                int NewZ = RandomPlayer.getLocation().blockZ() + RandomInstance.nextInt(MerchantData.SpawnRadius * 2 + 1) - MerchantData.SpawnRadius;
                                CreateMerchant(MerchantData, new Location(world, NewX + 0.5, world.getHighestBlockYAt(NewX, NewZ) + 1, NewZ + 0.5), RandomPlayer);
                            }
                        }, MerchantSpawnFrequency, MerchantSpawnFrequency));
                    }
                    else {
                        getLogger().severe("Total rarity of enabled merchants for world " + WorldName + " cannot be 0");
                        Bukkit.getPluginManager().disablePlugin(this);
                        return;
                    }
                }
                worlds.add(WorldConfiguration);
            }
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        new Metrics(this, 34647);
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
        if (VersionInteger > 119) {
            UpdateStatus = "There is a new version available: v" + VersionString;
        }
        else if (VersionInteger == 119) {
            UpdateStatus = "You are using the latest version";
        }
        else {
            UpdateStatus = "Unable to check for updates";
        }
    }
    @EventHandler
    public void OnCitizensEnable(CitizensEnableEvent event) {
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            MetadataStore NPCData = npc.data();
            String NPCMerchantId = NPCData.get("merchant_id");
            if (NPCMerchantId != null) {
                Merchant MerchantData = GetMerchantData(NPCMerchantId);
                if (MerchantData != null) {
                    String WorldName = npc.getStoredLocation().getWorld().getName();
                    boolean WorldFound = false;
                    for (WorldConfiguration WorldConfiguration: worlds) {
                        if (WorldConfiguration.name.equals(WorldName)) {
                            WorldFound = true;
                            String MerchantStatus = WorldConfiguration.MerchantQuery(NPCMerchantId);
                            if (MerchantStatus.equals("Enabled")) {
                                String NPCMerchantVariant = NPCData.get("merchant_variant");
                                if (NPCMerchantVariant.equals("stray")) {
                                    NPCData.setPersistent("merchant_viewers", 0);
                                    npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                                }
                                else if (NPCMerchantVariant.endsWith("_moving")) {
                                    NPCData.setPersistent("merchant_variant", NPCMerchantVariant.replace("_moving", ""));
                                    npc.getOrAddTrait(FollowTrait.class).follow(null);
                                    if (NPCMerchantVariant.equals("stray_moving")) {
                                        NPCData.setPersistent("merchant_viewers", 0);
                                        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                                    }
                                }
                                npc.setName(MerchantData.name);
                                npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(MerchantData.name, MerchantData.SkinSignature, MerchantData.SkinValue);
                                npc.spawn(npc.getStoredLocation());
                                npc.getNavigator().getDefaultParameters().stuckAction((StuckNPC, navigator) -> {
                                    MetadataStore StuckNPCData = StuckNPC.data();
                                    String StuckNPCMerchantVariant = StuckNPCData.get("merchant_variant");
                                    if (StuckNPCMerchantVariant.endsWith("_moving")) {
                                        Merchant StuckMerchantData = GetMerchantData(StuckNPCData.get("merchant_id"));
                                        Player FollowingPlayer = (Player) StuckNPC.getOrAddTrait(FollowTrait.class).getFollowing();
                                        Location StuckNPCLocation = StuckNPC.getStoredLocation();
                                        Location FollowingPlayerLocation = FollowingPlayer.getLocation();
                                        if (StuckNPCLocation.getWorld().equals(FollowingPlayerLocation.getWorld()) && StuckNPCLocation.distance(FollowingPlayerLocation) <= StuckMerchantData.LostDistance) {
                                            FollowingPlayer.sendMessage(StuckMerchantData.StuckDialogue);
                                        }
                                        else {
                                            StuckNPCData.setPersistent("merchant_variant", StuckNPCMerchantVariant.replace("_moving", ""));
                                            StuckNPC.getOrAddTrait(FollowTrait.class).follow(null);
                                            if (StuckNPCMerchantVariant.equals("stray_moving")) {
                                                StuckNPCData.setPersistent("merchant_viewers", 0);
                                                StuckNPC.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                                                FollowingPlayer.sendMessage(StuckMerchantData.NonShopkeeperLostDialogue);
                                            }
                                            else {
                                                FollowingPlayer.sendMessage(StuckMerchantData.ShopkeeperLostDialogue);
                                            }
                                            return false;
                                        }
                                    }
                                    return true;
                                }).stationaryTicks(60);
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
        getLogger().info("Wandering Merchants v1.1.9");
        getLogger().info("Made by MahinBro001238");
        getLogger().info("Licensed under the Apache License 2.0");
        getLogger().info("For support: https://discord.gg/8yrbGUCFFD");
        getLogger().info(UpdateStatus);
        getLogger().info("Wandering Merchants has been enabled");
        getLogger().info("=============================================");
    }
    @EventHandler
    public void OnItemCraft(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        ItemMeta ItemMeta = result.getItemMeta();
        if (ItemMeta != null && ItemMeta.getPersistentDataContainer().has(MerchantItemKey)) {
            if (!AllowItemCraftingWithoutMerchant) {
                Player player = (Player) event.getWhoClicked();
                String WorldName = player.getWorld().getName();
                boolean WorldFound = false;
                for (WorldConfiguration WorldConfiguration: worlds) {
                    if (WorldConfiguration.name.equals(WorldName)) {
                        WorldFound = true;
                        String ItemId = ItemMeta.getPersistentDataContainer().get(MerchantItemKey, PersistentDataType.STRING);
                        boolean ItemsMerchantFound = false;
                        for (MerchantConfiguration MerchantConfiguration: WorldConfiguration.UsedMerchants) {
                            if (GetMerchantData(MerchantConfiguration.id).ItemId.equals(ItemId)) {
                                ItemsMerchantFound = true;
                                break;
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
            Player player = event.getClicker();
            String PlayerName = player.getName();
            Merchant MerchantData = GetMerchantData(NPCMerchantId);
            Inventory GUI = Bukkit.createInventory(new InventoryOwner(npc.getId() + " " + NPCMerchantVariant), 45, Component.text(NPCName));
            ItemStack TalkToMerchant = CreateItem(Material.PAPER, Component.text("Talk to " + NPCName), null, null, null);
            ItemStack MerchantItemInteraction = CreateItem(Material.ENCHANTED_BOOK, MerchantData.ItemName, List.of(Component.text("Left click to give ").append(MerchantData.ItemName).append(Component.text(" to " + NPCName)), Component.text("Right click to view ").append(MerchantData.ItemName).append(Component.text(" recipe"))), MerchantData.ItemCMD, null);
            ItemStack ShopIcon = MerchantData.ShopIcon;
            ItemStack SellGUIIcon = MerchantData.SellGUIIcon;
            ItemStack follow = CreateItem(Material.LIME_CONCRETE, Component.text("Follow"), List.of(Component.text("Make " + NPCName + " follow you")), null, null);
            ItemStack unfollow = CreateItem(Material.RED_CONCRETE, Component.text("Unfollow"), List.of(Component.text("Make " + NPCName + " stop following you")), null, null);
            String FollowingName;
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
                NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(InventoryOwner[0]));
                if (npc != null) {
                    MetadataStore NPCData = npc.data();
                    String NPCMerchantVariant = NPCData.get("merchant_variant");
                    if (InventoryOwner[1].equals(NPCMerchantVariant)) {
                        Merchant MerchantData = GetMerchantData(NPCData.get("merchant_id"));
                        Player player = (Player) event.getWhoClicked();
                        int slot = event.getRawSlot();
                        if (slot == 20) {
                            if (NPCMerchantVariant.equals("stray") || NPCMerchantVariant.equals("stray_moving")) {
                                player.sendMessage(MerchantData.InteractionDialogue);
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
                NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(OwnerString.split(" ")[0]));
                if (npc != null) {
                    MetadataStore NPCData = npc.data();
                    String NPCMerchantVariant = NPCData.get("merchant_variant");
                    if (NPCMerchantVariant.equals("stray") && OwnerString.split(" ")[1].equals(NPCMerchantVariant)) {
                        int NPCMerchantViewers = (int) NPCData.get("merchant_viewers") - 1;
                        if (NPCMerchantViewers < 0) {
                            NPCMerchantViewers = 0;
                        }
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
    public void OnPlayerQuit(PlayerQuitEvent event) {
        String PlayerName = event.getPlayer().getName();
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            MetadataStore NPCData = npc.data();
            if (NPCData.has("merchant_id")) {
                Entity following = npc.getOrAddTrait(FollowTrait.class).getFollowing();
                if (following != null && following.getName().equals(PlayerName)) {
                    String NPCMerchantVariant = NPCData.get("merchant_variant");
                    NPCData.setPersistent("merchant_variant", NPCMerchantVariant.replace("_moving", ""));
                    npc.getOrAddTrait(FollowTrait.class).follow(null);
                    if (NPCMerchantVariant.equals("stray_moving")) {
                        NPCData.setPersistent("merchant_viewers", 0);
                        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                    }
                }
            }
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
        getLogger().info("Wandering Merchants v1.1.9 has been disabled");
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
                                    CreateMerchant(MerchantData, player.getLocation(), player);
                                    if (args.length == 3) {
                                        CommandExecutor.sendMessage(Component.text("Spawned " + MerchantData.id + " at " + player.getName() + "'s location", NamedTextColor.GOLD));
                                    }
                                    else {
                                        CommandExecutor.sendMessage(Component.text("Spawned " + MerchantData.id, NamedTextColor.GOLD));
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
                            if (args[1].equals("general_config")) {
                                if (args.length == 2) {
                                    CommandExecutor.sendMessage(Component.text("---------- General Config ----------", NamedTextColor.GOLD));
                                    CommandExecutor.sendMessage(Component.empty().append(Component.text("Merchant Despawn Check Frequency: ", NamedTextColor.GOLD)).append(Component.text(Long.toString(MerchantDespawnCheckFrequency) + " seconds")));
                                    CommandExecutor.sendMessage(Component.empty().append(Component.text("Sell GUI Permission Revoke Delay: ", NamedTextColor.GOLD)).append(Component.text(Long.toString(SellGUIPermissionRevokeDelay) + " tick")));
                                    CommandExecutor.sendMessage(Component.empty().append(Component.text("Allow Item Crafting Without Merchant: ", NamedTextColor.GOLD)).append(Component.text(Boolean.toString(AllowItemCraftingWithoutMerchant))));
                                    CommandExecutor.sendMessage(Component.empty().append(Component.text("Suppress Merchant Death Message: ", NamedTextColor.GOLD)).append(Component.text(Boolean.toString(SuppressMerchantDeathMessage))));
                                    CommandExecutor.sendMessage(Component.empty().append(Component.text("No Permission Message: ", NamedTextColor.GOLD)).append(NoPermissionMessage));
                                    CommandExecutor.sendMessage(Component.empty().append(Component.text("Item Crafting Not Allowed Message: ", NamedTextColor.GOLD)).append(ItemCraftingNotAllowedMessage));
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Usage: /wm get general_config", NamedTextColor.RED));
                                }
                            }
                            else if (args[1].equals("worlds_config")) {
                                if (args.length == 3) {
                                    boolean WorldFound = false;
                                    for (WorldConfiguration WorldConfiguration: worlds) {
                                        if (WorldConfiguration.name.equals(args[2])) {
                                            WorldFound = true;
                                            CommandExecutor.sendMessage(Component.text("---------- Worlds Config ----------", NamedTextColor.GOLD));
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("Name: ", NamedTextColor.GOLD)).append(Component.text(args[2])));
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("Merchant Spawn Frequency: ", NamedTextColor.GOLD)).append(Component.text(Integer.toString(WorldConfiguration.MerchantSpawnFrequency) + " seconds")));
                                            CommandExecutor.sendMessage(Component.text("--- Merchant Data ---", NamedTextColor.GOLD));
                                            for (MerchantConfiguration MerchantConfiguration: WorldConfiguration.UsedMerchants) {
                                                CommandExecutor.sendMessage(Component.empty());
                                                CommandExecutor.sendMessage(Component.empty().append(Component.text("ID: ", NamedTextColor.GOLD)).append(Component.text(MerchantConfiguration.id)));
                                                CommandExecutor.sendMessage(Component.empty().append(Component.text("Rarity: ", NamedTextColor.GOLD)).append(Component.text(Integer.toString(MerchantConfiguration.rarity))));
                                                CommandExecutor.sendMessage(Component.empty().append(Component.text("Enabled: ", NamedTextColor.GOLD)).append(Component.text(Boolean.toString(MerchantConfiguration.enabled))));
                                            }
                                            break;
                                        }
                                    }
                                    if (!WorldFound) {
                                        CommandExecutor.sendMessage(Component.text("World " + args[2] + " is not configured in the config", NamedTextColor.YELLOW));
                                    }
                                }
                                else {
                                    CommandExecutor.sendMessage(Component.text("Usage: /wm get worlds_config <world name>", NamedTextColor.RED));
                                }
                            }
                            else if (args[1].equals("merchants_config")) {
                                if (args.length == 3) {
                                    Merchant MerchantData = GetMerchantData(args[2]);
                                    if (MerchantData != null) {
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("---------- Merchants Config ----------", NamedTextColor.GOLD)));
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
                                        Component SpawnText = MerchantData.SpawnText;
                                        if (SpawnText == null) {
                                            SpawnText = Component.text("null");
                                        }
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Spawn Text: ", NamedTextColor.GOLD)).append(SpawnText));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Interaction Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.InteractionDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Correct Item Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.CorrectItemDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Incorrect Item Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.IncorrectItemDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Follow Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.FollowDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Non Shopkeeper Unfollow Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.NonShopkeeperUnfollowDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shopkeeper Unfollow Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.ShopkeeperUnfollowDialogue));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Lost Distance: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.LostDistance)));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Stuck Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.StuckDialogue != null ? MerchantData.StuckDialogue : Component.text("null")));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Non Shopkeeper Lost Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.NonShopkeeperLostDialogue != null ? MerchantData.NonShopkeeperLostDialogue : Component.text("null")));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shopkeeper Lost Dialogue: ", NamedTextColor.GOLD)).append(MerchantData.ShopkeeperLostDialogue != null ? MerchantData.ShopkeeperLostDialogue : Component.text("null")));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Glow: ", NamedTextColor.GOLD)).append(Component.text(Boolean.toString(MerchantData.glow))));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Spawn Radius: ", NamedTextColor.GOLD)).append(Component.text(Integer.toString(MerchantData.SpawnRadius) + " blocks")));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Despawn Time: ", NamedTextColor.GOLD)).append(Component.text(Integer.toString(MerchantData.DespawnTime) + " seconds")));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item ID: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.ItemId)));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item Name: ", NamedTextColor.GOLD)).append(MerchantData.ItemName));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item Custom Model Data: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.ItemCMD)));
                                        List<Component> ItemLore = MerchantData.ItemLore;
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item Lore:", NamedTextColor.GOLD)));
                                        for (int i = 0; i < ItemLore.size(); i++) {
                                            CommandExecutor.sendMessage(ItemLore.get(i));
                                        }
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Item Crafting Recipe: ", NamedTextColor.GOLD)).append(Component.text("[View Recipe]", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/wm recipe " + MerchantData.id))));
                                        List<Component> ShopIconLore = MerchantData.ShopIconLore;
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shop Icon Material: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.ShopIconMaterial.name())));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shop Icon Name: ", NamedTextColor.GOLD)).append(MerchantData.ShopIconName));
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Shop Icon Lore:", NamedTextColor.GOLD)));
                                        for (int i = 0; i < ShopIconLore.size(); i++) {
                                            CommandExecutor.sendMessage(ShopIconLore.get(i));
                                        }
                                        List<Component> SellIconLore = MerchantData.SellIconLore;
                                        CommandExecutor.sendMessage(Component.empty().append(Component.text("Sell Icon Material: ", NamedTextColor.GOLD)).append(Component.text(MerchantData.SellIconMaterial.name())));
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
                                    CommandExecutor.sendMessage(Component.text("Usage: /wm get merchants_config <merchant id>", NamedTextColor.RED));
                                }
                            }
                            else if (args[1].equals("npc_data")) {
                                if (args.length == 3) {
                                    try {
                                        NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[2]));
                                        if (npc != null) {
                                            MetadataStore NPCData = npc.data();
                                            CommandExecutor.sendMessage(Component.empty().append(Component.text("---------- NPC Data ----------", NamedTextColor.GOLD)));
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
                                        CommandExecutor.sendMessage(Component.text("---------- Item Data ----------", NamedTextColor.GOLD));
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
                                CommandExecutor.sendMessage(Component.text("Usage: /wm get <general_config|worlds_config|merchants_config|npc_data|item_data>", NamedTextColor.RED));
                            }
                        }
                        else {
                            CommandExecutor.sendMessage(Component.text("Usage: /wm get <general_config|worlds_config|merchants_config|npc_data|item_data>", NamedTextColor.RED));
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
