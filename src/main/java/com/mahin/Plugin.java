package com.mahin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.Entity;
import java.util.Scanner;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import net.citizensnpcs.trait.TargetableTrait;
import net.citizensnpcs.trait.waypoint.Waypoints;
import org.bukkit.command.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.LivingEntity;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.persistence.PersistentDataType;
public class Plugin extends JavaPlugin implements Listener {
    private NamespacedKey MerchantItemKey;
    private List<Merchant> merchants = new ArrayList<>();
    private int MerchantCount;
    private String UpdateStatus;
    private long SellGUIPermissionRevokeDelay;
    private boolean NPCMigrationNeeded = false;
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
        public String ItemId;
        public Component ItemName;
        public String ItemCMDS;
        public Inventory ItemCraftingRecipeGUI;
        public ItemStack ShopIcon;
        public ItemStack SellGUIIcon;
        public boolean enabled;
        public Merchant(String id, String name, String shop, String SkinValue, String SkinSignature, Component DefaultDialogue, Component CorrectItemDialogue, Component IncorrectItemDialogue, Component FollowDialogue, Component NonShopkeeperUnfollowDialogue, Component ShopkeeperUnfollowDialogue, Component SpawnText, boolean glow, String ItemId, Component ItemName, String ItemCMDS, Inventory ItemCraftingRecipeGUI, ItemStack ShopIcon, ItemStack SellGUIIcon, boolean enabled) {
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
            this.ItemId = ItemId;
            this.ItemName = ItemName;
            this.ItemCMDS = ItemCMDS;
            this.ItemCraftingRecipeGUI = ItemCraftingRecipeGUI;
            this.ShopIcon = ShopIcon;
            this.SellGUIIcon = SellGUIIcon;
            this.enabled = enabled;
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
        for (int i=0; i<MerchantCount; i++) {
            Merchant MerchantData = merchants.get(i);
            if (MerchantData.id.equals(id)) {
                return MerchantData;
            }
        }
        return null;
    }
    // ---------------------------------------------
    // LEGACY CODE
    // ---------------------------------------------
    private int MigrateItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 2;
        }
        if (meta.getPersistentDataContainer().has(MerchantItemKey, PersistentDataType.STRING)) {
            return 1;
        }
        if (meta.hasLore() && !meta.lore().isEmpty()
                && PlainTextComponentSerializer.plainText().serialize(meta.lore().get(0)).startsWith("Hand this to")) {
            String oldCMD = meta.getCustomModelDataComponent().getStrings().get(0);
            if (oldCMD.equals("rufus_barrel")) {
                oldCMD = "rufuss_barrel";
            }
            meta.getPersistentDataContainer().set(MerchantItemKey, PersistentDataType.STRING, oldCMD);
            item.setItemMeta(meta);
            return 0;
        }
        return 2;
    }
    // ---------------------------------------------
    @Override
    public void onEnable() {
        MerchantItemKey = new NamespacedKey(this, "merchant_item");
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        // ---------------------------------------------
        // LEGACY CODE
        // ---------------------------------------------
        int config_version = config.getInt("config_version");
        if (!config.isSet("config_version") || config_version < 2) {
            NPCMigrationNeeded = true;
            int OldMerchantSpawnFrequency = config.getInt("merchant_spawn_frequency");
            int OldMerchantDespawnTime = config.getInt("merchant_despawn_time");
            int OldMerchantDespawnCheckFrequency = config.getInt("merchant_despawn_check_frequency");
            saveResource("config.yml", true);
            reloadConfig();
            config = getConfig();
            config.set("merchant_spawn_frequency", OldMerchantSpawnFrequency);
            config.set("merchant_despawn_time", OldMerchantDespawnTime);
            config.set("merchant_despawn_check_frequency", OldMerchantDespawnCheckFrequency);
            saveConfig();
        }
        // ---------------------------------------------
        long MerchantSpawnFrequency = config.getInt("merchant_spawn_frequency") * 20L;
        long MerchantDespawnTime = config.getInt("merchant_despawn_time") * 1000L;
        long MerchantDespawnCheckFrequency = config.getInt("merchant_despawn_check_frequency") * 20L;
        SellGUIPermissionRevokeDelay = config.getLong("sellgui_permission_revoke_delay");
        int MerchantSpawnRadius = config.getInt("merchant_spawn_radius");
        ConfigurationSection MerchantsSection = config.getConfigurationSection("merchants");
        if (MerchantsSection != null) {
            MiniMessage MiniMessageInstance = MiniMessage.miniMessage();
            for (String key: MerchantsSection.getKeys(false)) {
                ConfigurationSection MerchantSection = MerchantsSection.getConfigurationSection(key);
                String id = MerchantSection.getString("id");
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
                ConfigurationSection ItemSection = MerchantSection.getConfigurationSection("item");
                String ItemId = ItemSection.getString("id");
                Component ItemName = MiniMessageInstance.deserialize(ItemSection.getString("name"));
                List<Component> ItemLore = new ArrayList<>();
                for (String line: ItemSection.getStringList("lore")) {
                    ItemLore.add(MiniMessageInstance.deserialize(line));
                }
                String ItemCMDS = ItemSection.getString("custom_model_data");
                ConfigurationSection RecipeSection = ItemSection.getConfigurationSection("recipe");
                List<String> shape = RecipeSection.getStringList("shape");
                ConfigurationSection IngredientsSection = RecipeSection.getConfigurationSection("ingredients");
                Inventory ItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, ItemName);
                for (int i=0; i<3; i++) {
                    String LetterRow = shape.get(i);
                    for (int j=0; j<3; j++) {
                        char letter = LetterRow.charAt(j);
                        if (letter != ' ') {
                            ItemCraftingRecipeGUI.setItem(12 + i*9 + j, CreateItem(Material.valueOf(IngredientsSection.getString(String.valueOf(letter))), null, null, null, null));
                        }
                    }
                }
                ConfigurationSection ShopIconSection = MerchantSection.getConfigurationSection("shop_icon");
                Material ShopIconMaterial = Material.valueOf(ShopIconSection.getString("material"));
                Component ShopIconName = MiniMessageInstance.deserialize(ShopIconSection.getString("name"));
                List<Component> ShopIconLore = new ArrayList<>();
                for (String line: ShopIconSection.getStringList("lore")) {
                    ShopIconLore.add(MiniMessageInstance.deserialize(line));
                }
                ItemStack ShopIcon = CreateItem(ShopIconMaterial, ShopIconName, ShopIconLore, null, null);
                ConfigurationSection SellIconSection = MerchantSection.getConfigurationSection("sell_icon");
                Material SellIconMaterial = Material.valueOf(SellIconSection.getString("material"));
                Component SellIconName = MiniMessageInstance.deserialize(SellIconSection.getString("name"));
                List<Component> SellIconLore = new ArrayList<>();
                for (String line: SellIconSection.getStringList("lore")) {
                    SellIconLore.add(MiniMessageInstance.deserialize(line));
                }
                ItemStack SellGUIIcon = CreateItem(SellIconMaterial, SellIconName, SellIconLore, null, null);
                boolean enabled = MerchantSection.getBoolean("enabled");
                Merchant merchant = new Merchant(id, name, shop, SkinValue, SkinSignature, DefaultDialogue, CorrectItemDialogue, IncorrectItemDialogue, FollowDialogue, NonShopkeeperUnfollowDialogue, ShopkeeperUnfollowDialogue, SpawnText, glow, ItemId, ItemName, ItemCMDS, ItemCraftingRecipeGUI, ShopIcon, SellGUIIcon, enabled);
                merchants.add(merchant);
                if (enabled) {
                    ShapedRecipe ItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, ItemId), CreateItem(Material.ENCHANTED_BOOK, ItemName, ItemLore, ItemCMDS, ItemId));
                    ItemCraftingRecipe.shape(shape.get(0), shape.get(1), shape.get(2));
                    for (String ingredient: IngredientsSection.getKeys(false)) {
                        ItemCraftingRecipe.setIngredient(ingredient.charAt(0), Material.valueOf(IngredientsSection.getString(ingredient)));
                    }
                    Bukkit.addRecipe(ItemCraftingRecipe);
                }
            }
        }
        MerchantCount = merchants.size();
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
        if (VersionInteger > 115) {
            UpdateStatus = "There is a new update: v" + VersionString;
        }
        else if (VersionInteger == 115) {
            UpdateStatus = "You are using the latest version";
        }
        else {
            UpdateStatus = "Unable to check for updates";
        }
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            List<Player> OverworldPlayers = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().getName().equals("world")) {
                    OverworldPlayers.add(player);
                }
            }
            int OverworldPlayerCount = OverworldPlayers.size();
            if (OverworldPlayerCount != 0) {
                Random RandomInstance = new Random();
                Player RandomPlayer = OverworldPlayers.get(RandomInstance.nextInt(OverworldPlayerCount));
                Merchant RandomMerchant = merchants.get(RandomInstance.nextInt(MerchantCount));
                NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, RandomMerchant.name);
                MetadataStore NPCData = npc.data();
                NPCData.setPersistent("merchant_id", RandomMerchant.id);
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
                World world = Bukkit.getWorld("world");
                int NewX = RandomPlayer.getLocation().blockX() + RandomInstance.nextInt(MerchantSpawnRadius*2 + 1) - MerchantSpawnRadius;
                int NewZ = RandomPlayer.getLocation().blockZ() + RandomInstance.nextInt(MerchantSpawnRadius*2 + 1) - MerchantSpawnRadius;
                npc.spawn(new Location(world, NewX + 0.5, world.getHighestBlockYAt(NewX, NewZ) + 1, NewZ + 0.5));
                if (RandomMerchant.SpawnText != null) {
                    RandomPlayer.sendMessage(RandomMerchant.SpawnText);
                }
            }
        }, MerchantSpawnFrequency, MerchantSpawnFrequency);
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (NPC npc : CitizensAPI.getNPCRegistry()) {
                MetadataStore NPCData = npc.data();
                String NPCMerchantVariant = NPCData.get("merchant_variant");
                if (NPCMerchantVariant != null && (NPCMerchantVariant.equals("stray") || NPCMerchantVariant.equals("stray_moving"))) {
                    if (System.currentTimeMillis() - (long) NPCData.get("creation_time") >= MerchantDespawnTime) {
                        npc.destroy();
                    }
                }
            }
        }, MerchantDespawnCheckFrequency, MerchantDespawnCheckFrequency);
        Bukkit.getPluginManager().registerEvents(this, this);
        new Metrics(this, 34647);
    }
    @EventHandler
    public void onCitizensEnable(CitizensEnableEvent event) {
        // ---------------------------------------------
        // LEGACY CODE
        // ---------------------------------------------
        if (NPCMigrationNeeded) {
            for (NPC npc: CitizensAPI.getNPCRegistry()) {
                MetadataStore NPCData = npc.data();
                String NPCMerchantVariant = NPCData.get("variant");
                if (NPCMerchantVariant != null) {
                    NPCMerchantVariant = NPCMerchantVariant.replace("shop", "shopkeeper");
                    NPCData.remove("variant");
                    NPCData.setPersistent("merchant_variant", NPCMerchantVariant);
                    NPCData.setPersistent("merchant_id", npc.getName().toLowerCase());
                    NPCData.remove("in_interaction");
                    NPCData.remove("shop_type");
                }
            }
        }
        // ---------------------------------------------
        int NPCDeleted = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            MetadataStore NPCData = npc.data();
            String NPCMerchantId = NPCData.get("merchant_id");
            if (NPCMerchantId != null) {
                Merchant MerchantData = GetMerchantData(NPCMerchantId);
                if (MerchantData != null) {
                    if (MerchantData.enabled) {
                        if (NPCData.get("merchant_variant").equals("stray")) {
                            NPCData.setPersistent("merchant_viewers", 0);
                            npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                        }
                        npc.setName(MerchantData.name);
                        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(MerchantData.name, MerchantData.SkinSignature, MerchantData.SkinValue);
                        npc.spawn(npc.getStoredLocation());
                    } else {
                        npc.despawn(DespawnReason.REMOVAL);
                    }
                } else {
                    npc.destroy();
                    NPCDeleted++;
                }
            }
        }
        int TotalMerchants = MerchantCount;
        for (int i = 0; i < MerchantCount; i++) {
            if (!merchants.get(i).enabled) {
                merchants.remove(i);
                MerchantCount--;
                i--;
            }
        }
        getLogger().info("=============================================");
        getLogger().info("Wandering Merchants v1.1.5");
        getLogger().info("Made by MahinBro001238");
        getLogger().info("Licensed under the Apache License 2.0");
        getLogger().info("For support: https://discord.gg/8yrbGUCFFD");
        getLogger().info(UpdateStatus);
        getLogger().info("Merchants Loaded: " + TotalMerchants);
        getLogger().info("Merchants Enabled: " + MerchantCount);
        getLogger().info("Merchants Disabled: " + (TotalMerchants - MerchantCount));
        getLogger().info("NPCs Deleted: " + NPCDeleted);
        getLogger().info("Wandering Merchants has been enabled");
        getLogger().info("=============================================");
        if (MerchantCount == 0) {
            getLogger().info("No merchants are enabled in the config");
            getLogger().info("Wandering Merchants will now disable itself.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
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
    public void onInventoryClick(InventoryClickEvent event) {
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
                                    // ---------------------------------------------
                                    // LEGACY CODE
                                    // ---------------------------------------------
                                    MigrateItem(ItemInHand);
                                    // ---------------------------------------------
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
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder owner = event.getInventory().getHolder();
        if (owner instanceof InventoryOwner) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(((InventoryOwner) owner).owner.split(" ")[1]));
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
        else if (owner != null && owner.getClass().getSimpleName().equals("SellGUI")) {
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    Player player = (Player) event.getPlayer();
                    if (player.isOnline()) {
                        if (player.hasMetadata("sellgui_permission_container")) {
                            PermissionAttachment permission = (PermissionAttachment) player.getMetadata("sellgui_permission_container").get(0).value();
                            player.removeAttachment(permission);
                            player.removeMetadata("sellgui_permission_container", Plugin.this);
                            player.recalculatePermissions();
                        }
                    }
                }
            }, SellGUIPermissionRevokeDelay);
        }
    }
    @EventHandler
    public void onNPCDeath(NPCDeathEvent event) {
        NPC npc = event.getNPC();
        if (npc.data().get("merchant_id") != null) {
            npc.destroy();
        }
    }
    @Override
    public void onDisable() {
        getLogger().info("Wandering Merchants v1.1.5 has been disabled");
    }
    // ---------------------------------------------
    // LEGACY CODE
    // ---------------------------------------------
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("migrate_item")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Only players can use this command"));
                return true;
            }
            ItemStack ItemInHand = player.getInventory().getItemInMainHand();
            int result = MigrateItem(ItemInHand);
            if (result == 0) {
                player.sendMessage(Component.text("Item converted to the new format"));
            } else if (result == 1) {
                player.sendMessage(Component.text("This item is already in the new format"));
            } else {
                player.sendMessage(Component.text("This item is not a merchant item"));
            }
            return true;
        }
        return false;
    }
    // ---------------------------------------------
}
