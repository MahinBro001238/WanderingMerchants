package com.mahin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.MetadataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.FollowTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.command.CommandSender;
import net.citizensnpcs.trait.TargetableTrait;
import net.citizensnpcs.trait.waypoint.Waypoints;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
public class Plugin extends JavaPlugin implements Listener {
    private static class Merchant {
        public String name;
        public String shop;
        public String SkinTexture;
        public String SkinSignature;
        public String dialogue;
        public String ItemName;
        public Inventory ItemCraftingRecipieGUI;
        public ItemStack ShopIcon;
        public ItemStack SellGUIIcon;
        public Merchant(String name, String shop, String SkinTexture, String SkinSignature, String dialogue, String ItemName, Inventory ItemCraftingRecipieGUI, ItemStack ShopIcon, ItemStack SellGUIIcon) {
            this.name = name;
            this.shop = shop;
            this.SkinTexture = SkinTexture;
            this.SkinSignature = SkinSignature;
            this.dialogue = dialogue;
            this.ItemName = ItemName;
            this.ItemCraftingRecipieGUI = ItemCraftingRecipieGUI;
            this.ShopIcon = ShopIcon;
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
    private ItemStack CreateItem(Material material, String name, List<String> lore, String CMDS) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (!name.equals("")) {
            meta.displayName(Component.text(name));
        }

        if (lore.size() != 0) {
            List<Component> loreList = new ArrayList<>();
            for (String line : lore) {
                loreList.add(Component.text(line));
            }
            meta.lore(loreList);
        }

        if (!CMDS.equals("")) {
            CustomModelDataComponent CMD = meta.getCustomModelDataComponent();
            CMD.setStrings(List.of(CMDS));
            meta.setCustomModelDataComponent(CMD);
        }

        item.setItemMeta(meta);
        return item;
    }
    private List<Merchant> merchants = new ArrayList<>();
    private CommandSender console;
    @Override
    public void onEnable() {
        ItemStack Item1;
        ItemStack Item2;
        ItemStack Item3;
        ItemStack Item4;
        Inventory GarrickItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Garrick").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.IRON_INGOT, "", List.of(), "");
        Item2 = CreateItem(Material.IRON_AXE, "", List.of(), "");
        GarrickItemCraftingRecipeGUI.setItem(12, Item1);
        GarrickItemCraftingRecipeGUI.setItem(13, Item1);
        GarrickItemCraftingRecipeGUI.setItem(14, Item1);
        GarrickItemCraftingRecipeGUI.setItem(21, Item1);
        GarrickItemCraftingRecipeGUI.setItem(22, Item2);
        GarrickItemCraftingRecipeGUI.setItem(23, Item1);
        GarrickItemCraftingRecipeGUI.setItem(30, Item1);
        GarrickItemCraftingRecipeGUI.setItem(31, Item1);
        GarrickItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Garrick",
            "wood",
            "ewogICJ0aW1lc3RhbXAiIDogMTYxMjU4ODQ0NzM1NCwKICAicHJvZmlsZUlkIiA6ICI5OTdjZjFlMmY1NGQ0YzEyOWY2ZjU5ZTVlNjU1YjZmNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJpbzEyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMwMGIxMDhhMzBiYWYyYjAyYmYzZGI0OTJjY2U4MmM5MDJhZTQwNGVmN2VkYWQ5YTc4NzlhNmFiNmQ2ZDcxZDciCiAgICB9CiAgfQp9",
            "vR3Z26c2O8pgqzG/PId9Mby0ykepa/Ve7JbHi/6w/j4NMwMb7jQiVeXm32JbJMFYvx+j/ldUniq6FJgdAZiJ4DgVsHQfWDmzaw//nqvwNGOFGNNyi+I2xOFk62t+CghQIWMgcclnDv94ybR8bsN0zXqoAOQCQ5KrzGFdQ3i5y/Wups19SabZ5s/66bBFpme8ycK4vK4qoZfvGviBtFc15NX0dHAy0O6IWRy4TQhU0J2Rf5rx+3+ZbnFnTbOdk/x1K6BvcJd6qPkgoHQPisZ2DAbPBxgSTWhoHpCrzIC7koiRLIbHgsZRlskdjPAA+lNxCdTPB4OB5QqXnT2rzqio1rm9rEKHLpyV4IWrQ3ijB5A2EvoLyLB3jZm9jlJaCC6yLoNfddfp+7ENpakKwfcvMo/xFitoMdQsMOj0X1z4f19mThlW0EBERu39X8Nq6oPCBlJ249iOj/QJHtV8mgzWlrtevfT0h5cJw3yfcte50QiBS2n54a+nlhX/KRSliCv6bc0HR0Wf0Cb7Y8ZOtNWA4r+M/x5kbszosb9KfTB3nShaYJjTjrIbQU61Fi8bJVVxfCZDl+6Zpkmj10vZuJsUqustanY4/MTHj8NZl2wQK68dFLH6LMBhxyIKbqRue1AmTT5Zws7n4PdQuEAiMbCv2sJOeqTQ+IlryIma0bJdalM=",
            "Lost my hatchet, lost my work. Bring it back, and I will keep you stocked with wood",
            "Hatchet",
            GarrickItemCraftingRecipeGUI,
            CreateItem(Material.OAK_LOG, "Buy wood", List.of("Buy wood related items"), ""),
            CreateItem(Material.IRON_AXE, "Sell items", List.of("Sell wood items in one go"), "")
        ));
        ShapedRecipe GarrickItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "garricks_hatchet"), CreateItem(Material.ENCHANTED_BOOK, "Garrick's Hatchet", List.of("Hand this to Garrick"), "garricks_hatchet"));
        GarrickItemCraftingRecipe.shape("III", "IXI", "III");
        GarrickItemCraftingRecipe.setIngredient('I', Material.IRON_INGOT);
        GarrickItemCraftingRecipe.setIngredient('X', Material.IRON_AXE);
        Bukkit.addRecipe(GarrickItemCraftingRecipe);
        Inventory BrunaItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Bruna").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.STONE, "", List.of(), "");
        Item2 = CreateItem(Material.COBBLESTONE, "", List.of(), "");
        Item3 = CreateItem(Material.DEEPSLATE, "", List.of(), "");
        Item4 = CreateItem(Material.STONECUTTER, "", List.of(), "");
        BrunaItemCraftingRecipeGUI.setItem(12, Item1);
        BrunaItemCraftingRecipeGUI.setItem(13, Item2);
        BrunaItemCraftingRecipeGUI.setItem(14, Item1);
        BrunaItemCraftingRecipeGUI.setItem(21, Item3);
        BrunaItemCraftingRecipeGUI.setItem(22, Item4);
        BrunaItemCraftingRecipeGUI.setItem(23, Item3);
        BrunaItemCraftingRecipeGUI.setItem(30, Item1);
        BrunaItemCraftingRecipeGUI.setItem(31, Item2);
        BrunaItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Bruna",
            "stone",
            "ewogICJ0aW1lc3RhbXAiIDogMTc3NzQ1NzIwMjIyNSwKICAicHJvZmlsZUlkIiA6ICJiYzRlZGZiNWYzNmM0OGE3YWM5ZjFhMzlkYzIzZjRmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICI4YWNhNjgwYjIyNDYxMzQwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUxZTg1MzdhZTA2Yzg5NzZhMDY5YjdkMzhjZmUxY2E4MjE3ZTE4OTlkYmY0MzA5YmU4NjkyNDc1ZDZmOTk3OTAiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "Hv10YBMSjmoouu/FMB8hcO2rYTs7cTccSYYq3FgcfeJ77D86ZiUnlwj+uB7a0xYVuIsRaDNapu0Fd/51xCILUoZsLcQZpprN1uwul0lrSSmc3pOKkQGY81NY8DInhVrEb7E5PHpb9KtTY95g3SgJRIbfjs7OWwr6gw9mGFBEZbdUX62FxjmCPa2+x2mTj5O27KAWWYQgVg+fKgMGc62KAaaeXYs11QWkzi11dA6eFZU5WIMB2SNkEw0MzRlVvEQc61pg8KzJOSSVgGXzhwT8rhmqx13AchcRP/q7llOaXt0QcYW2Ft7wCulEk6hhKFf+EKJ5I8rFWPbYRyjPBT6jv0V66wEvLp3/Am3W7yXLQD8yYMSwMxM9XlEC5E2FBqAKi+bxpc+9aBYTeLs3M9zD5ixsZ26zPT5N6hfDlVrNNdTYerjp7L6soLQ5knw0/mWeD2BkmEQpiDYwVzCXYSlsybjiS6knZZmKk0nXMAFPytSiF+Jty5TNu7vYzeiKW6eYSf2+SkWArrLhUsK9zoWDBDTbabQf+y7/4wKlubvBXeXdc5F89zCEnjfkDkIr33cyc38YHcUCSUTVBTCUxZyCefjXQ6jm+lLZCJFqnksDHjHnuWn0UN8E1vuqAh5ATm8f6ZRVbDPpzwZ28Ahbim1wGjearKGrpvl0UJF3GLnicqA=",
            "I am Bruna. I work stone into something usable. Bring back my stonecutter, and I will supply you with everything carved from the earth",
            "Stonecutter",
            BrunaItemCraftingRecipeGUI,
            CreateItem(Material.STONE, "Buy stone", List.of("Buy stone related items"), ""),
            CreateItem(Material.STONECUTTER, "Sell items", List.of("Sell stone items in one go"), "")
        ));
        ShapedRecipe BrunaItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "brunas_stonecutter"), CreateItem(Material.ENCHANTED_BOOK, "Bruna's Stonecutter", List.of("Hand this to Bruna"), "brunas_stonecutter"));
        BrunaItemCraftingRecipe.shape("SCS", "DTD", "SCS");
        BrunaItemCraftingRecipe.setIngredient('S', Material.STONE);
        BrunaItemCraftingRecipe.setIngredient('C', Material.COBBLESTONE);
        BrunaItemCraftingRecipe.setIngredient('D', Material.DEEPSLATE);
        BrunaItemCraftingRecipe.setIngredient('T', Material.STONECUTTER);
        Bukkit.addRecipe(BrunaItemCraftingRecipe);
        Inventory MaraItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Mara").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.DIRT, "", List.of(), "");
        Item2 = CreateItem(Material.SAND, "", List.of(), "");
        Item3 = CreateItem(Material.MUD, "", List.of(), "");
        Item4 = CreateItem(Material.IRON_SHOVEL, "", List.of(), "");
        MaraItemCraftingRecipeGUI.setItem(12, Item1);
        MaraItemCraftingRecipeGUI.setItem(13, Item2);
        MaraItemCraftingRecipeGUI.setItem(14, Item1);
        MaraItemCraftingRecipeGUI.setItem(21, Item3);
        MaraItemCraftingRecipeGUI.setItem(22, Item4);
        MaraItemCraftingRecipeGUI.setItem(23, Item3);
        MaraItemCraftingRecipeGUI.setItem(30, Item1);
        MaraItemCraftingRecipeGUI.setItem(31, Item2);
        MaraItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Mara",
            "natural",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NzA3NjY5NDkxNiwKICAicHJvZmlsZUlkIiA6ICI0YmEzMzMxNjA0YjU0NzgwYmQ0MzBkMTc5NjI4Yjk0YSIsCiAgInByb2ZpbGVOYW1lIiA6ICJ1bnNwZW5kaW5nIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFiNjU0NGFhMWI5MjkyM2M1N2IzMWYxYmZjMTZhOGNiNjg2NjVjYTMwMTUxZWY3NzE3YjBiN2I2MjQzM2NlMjkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "XEDs9WFvK3tOEsjBKQAyqub+8bb0jyssaKyceFkDzIW+jBtGN+Ump7iv0FnbfLERUa3t3uRjdgNLHHv8Gr8EYAPlYaBDFkBLJ4u0O+mrIHtEpP546UcU36qmgDlx1SPESm36wb4fqNmC/58Lwx2tjmqfCqgGKEdftHq7MfOOaawZAqn0B6TWFAQnFmcIfxEw41arhzPB5Yt5QZ/TLj2EDqETEGY6Na1vT6ARDaKvSe72MemYP6J+ZRJHglNwHXEUA1E+x2JiUP6V3ad9RNimzFRazc9o8/C7hfLT2xG9eHN5q1zFscpzg0fgK735bEejfrFpHonyUVrlPtJX50CpZ0cJAPWyZIAy7usZh+cJ5JUco83i48b36npXTvFK3+OorWU6YBEgTiG+ln/iapuCcJyMTfYxI3onbTRX3RmkoOVilcLonrtOvdP4B5YSoDWbNc2uMWFe4BkKEAfyayaCyhdkkqxA5xYO5juvzNxNXEiiFIFedvjmZ+JjaozvOqlpb+pCC4k5+28daKnKqtSSJ0qqufMW98RWFtcexy6KPqCutuAlDZj3Q5hEHObKNR6Jq7wCrbnnPCppedRbfWcvoD4skYRYPfRD094O9o2sv0SwHzFrMBCE/leRZsXzCrQCSpGrXFTZJkW2BuWXMSb81yj7/4hwxWAmcHz3z5tXryU=",
            "You can't collect what you can't dig. Someone walked off with my trowel and now I'm useless. Craft me a new one and I'll sell you everything I've gathered dirt, sand, ice, netherrack, all of it",
            "Trowel",
            MaraItemCraftingRecipeGUI,
            CreateItem(Material.DIRT, "Buy natural blocks", List.of("Buy natural blocks from all biomes"), ""),
            CreateItem(Material.SAND, "Sell items", List.of("Sell natural blocks in one go"), "")
        ));
        ShapedRecipe MaraItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "maras_trowel"), CreateItem(Material.ENCHANTED_BOOK, "Mara's Trowel", List.of("Hand this to Mara"), "maras_trowel"));
        MaraItemCraftingRecipe.shape("DSD", "MXM", "DSD");
        MaraItemCraftingRecipe.setIngredient('D', Material.DIRT);
        MaraItemCraftingRecipe.setIngredient('S', Material.SAND);
        MaraItemCraftingRecipe.setIngredient('M', Material.MUD);
        MaraItemCraftingRecipe.setIngredient('X', Material.IRON_SHOVEL);
        Bukkit.addRecipe(MaraItemCraftingRecipe);
        Inventory DorianItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Dorian").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.GLASS, "", List.of(), "");
        Item2 = CreateItem(Material.TERRACOTTA, "", List.of(), "");
        Item3 = CreateItem(Material.WHITE_DYE, "", List.of(), "");
        Item4 = CreateItem(Material.CLAY_BALL, "", List.of(), "");
        DorianItemCraftingRecipeGUI.setItem(12, Item1);
        DorianItemCraftingRecipeGUI.setItem(13, Item2);
        DorianItemCraftingRecipeGUI.setItem(14, Item1);
        DorianItemCraftingRecipeGUI.setItem(21, Item3);
        DorianItemCraftingRecipeGUI.setItem(22, Item4);
        DorianItemCraftingRecipeGUI.setItem(23, Item3);
        DorianItemCraftingRecipeGUI.setItem(30, Item1);
        DorianItemCraftingRecipeGUI.setItem(31, Item2);
        DorianItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Dorian",
            "decorative_blocks",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NzA3ODA1MDY1MSwKICAicHJvZmlsZUlkIiA6ICI2N2M1NzUxMzY1Mzg0MWJjYTQ1MTkwY2IyZGFiMWEwOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJuaXRlc3RhbGtlcjg3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M5NDg3ZjA3Y2Y3OGJmNGIwN2JjNzRlZWE1ZDA4MDU5ZTk4M2RlMjQ2ZjI3NjI2MGMxNjhiNGFmMmQ4NzViNDIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "thBMnhussTmaBs3P9JMz77OkG8pU3kFxGar565JaJvTbyaVvT3iSA7bHpTjkl3xcp7S4fT+FrBNvTEVn9zH7Dl8Kg9X8tEf6DqAK1nXwsGfhlC4AiMPzEntVVJLwApB0gPBw/liAKi1qOV9L4bh4OP405oG0R4ADokjsykcmoRB72G+ZsI2NsuFTRS8O99dB+nKyIg+regZVS7ANpmGk6nkoIdaer0Cazi6XcpqgAWqDkZe3QQiSokP7rEytQy7Mg5fszN7FhzQsNXMNJAuDqyaNiNsA/jkXiaZoudPIecy2RRHnFF+VGhROpCk3UXnP0pz/JeFDGa/CVPSiuj/GLItkp080G5NcSku19sTCZcFhi5jYuI7wk9gI2YNX8uzHpFADBo9DmLWyHi9GPp7CMxRtHiN5nt6auNNZdtd4eGCa4TD4ovv4hi2n0fe7fQNPSbhD/TozbOTlqq+P9DbBvGo8Vm+UMJDrUBya1iUKOmR9QXO4dJSbfNbvXtvxK487TPcWr7KLVMx8Yf5xSb2T9Bl+4ZltYWsJjGj5LmjoUvFRhffz/bCVnyJyByV+oBe0Y6cZx+jZ3sPOHHWdhKvYTuWq5h5Q7InArekHLW2Ep/Gvw17fZTJ5c09+dzgY/AJQeaJQNuK5RkcXQLXsDqgbJz/ia7yL3O0kPZMQCM7773o=",
            "A craftsman without his palette is nothing. Mine was taken from me or lost, I'd rather not say. Bring it back, and I shall ensure your structures are worthy of being called art",
            "Palette",
            DorianItemCraftingRecipeGUI,
            CreateItem(Material.GLASS, "Buy decorative blocks", List.of("Buy coloured glass, concrete, terracotta and more"), ""),
            CreateItem(Material.TERRACOTTA, "Sell items", List.of("Sell decorative blocks in one go"), "")
        ));
        ShapedRecipe DorianItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "dorians_palette"), CreateItem(Material.ENCHANTED_BOOK, "Dorian's Palette", List.of("Hand this to Dorian"), "dorians_palette"));
        DorianItemCraftingRecipe.shape("GTG", "DXD", "GTG");
        DorianItemCraftingRecipe.setIngredient('G', Material.GLASS);
        DorianItemCraftingRecipe.setIngredient('T', Material.TERRACOTTA);
        DorianItemCraftingRecipe.setIngredient('D', Material.WHITE_DYE);
        DorianItemCraftingRecipe.setIngredient('X', Material.CLAY_BALL);
        Bukkit.addRecipe(DorianItemCraftingRecipe);
        Inventory MarcusItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Marcus").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.GOLD_INGOT, "", List.of(), "");
        Item2 = CreateItem(Material.DIAMOND, "", List.of(), "");
        Item3 = CreateItem(Material.AMETHYST_SHARD, "", List.of(), "");
        MarcusItemCraftingRecipeGUI.setItem(12, Item1);
        MarcusItemCraftingRecipeGUI.setItem(13, Item2);
        MarcusItemCraftingRecipeGUI.setItem(14, Item1);
        MarcusItemCraftingRecipeGUI.setItem(21, Item2);
        MarcusItemCraftingRecipeGUI.setItem(22, Item3);
        MarcusItemCraftingRecipeGUI.setItem(23, Item2);
        MarcusItemCraftingRecipeGUI.setItem(30, Item1);
        MarcusItemCraftingRecipeGUI.setItem(31, Item2);
        MarcusItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Marcus",
            "resources",
            "ewogICJ0aW1lc3RhbXAiIDogMTc1MjAwMDMxMTkwMCwKICAicHJvZmlsZUlkIiA6ICI1NzI1YWJkMzY4MGI0MWVlOWJjMzhhNmUyM2M5OGNiMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBYmVuZHJlZ2VuXyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zOWU3Yjg4ZTQ2MmVmZmYwYWVkNDlmZTZiODQwNzc5ODE1ZDZkOTE3YmZlYTA3NzQzMGJmYjIwNGZhYzY3ZTIzIgogICAgfQogIH0KfQ==",
            "Sm46jOpiP4VQql08UdbRXuqlNMXN3pYw1H9nfl2XvxDVmAQc6avY7JZA3dOjLydG6qvIdeWf5QA0i8cKKDufgyPfb2TZtbVeIfx37NNQbBKWWbSKAN5Cd3gHY2+TxMjAFNpQxHqibOiz9uHe98hchvNq8s2kiAo43bVtB6ejrsohhvt5wPCpBljcl2+emFqIryghJigcpGpROfy2rakQ7lf/y/PFQB0AI1X2e4J/lV/A5JZEz523y5XlmQwT0tpNBmj7kDevJYj3Z+eWkEj0awBTDFjgGuzfl+iuFecL93cWNnPWEj2JwzP2jBBqnXJKX3boq+BFnFurUCC5AA2U0Zkm2pZ6P6yPvpHgw6CZeDH5j5J5FmEArOKYgqcUlGAxhbmclAnw1WbLmtTuf8n5UKW2EXGLcZAZF21eaveve98nlw29rVL+d9hFPybMY7dtMuJqy8bvcWJq7KFATkqv/gxle+Bul+DdfQAxlwDIohZaPtBS8CB9F05emEsrLW8LJyl3qnuu45pc/3OSjGvrlGGyi7GLAIKyFLCUebJNrq3mhAkSNKdD79qoFJBRVzvW+49qWP0xxawzHU+U5kTxLNV96KT72ePccFhlqMh3RIiVkeE0MkFcSjv4Yx0i85+AXRw7GbTdIfm6ZDlg4VaUAbbBDWY6gDLMIqft5g8Q5+c=",
            "No Charm, no trade. Bring it back, and you will have access to my valuables",
            "Charm",
            MarcusItemCraftingRecipeGUI,
            CreateItem(Material.DIAMOND, "Buy resources", List.of("Buy valuable resources"), ""),
            CreateItem(Material.GOLD_INGOT, "Sell items", List.of("Sell resource in one go"), "")
        ));
        ShapedRecipe MarcusItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "marcuss_charm"), CreateItem(Material.ENCHANTED_BOOK, "Marcus's Charm", List.of("Hand this to Marcus"), "marcuss_charm"));
        MarcusItemCraftingRecipe.shape("GDG", "DAD", "GDG");
        MarcusItemCraftingRecipe.setIngredient('G', Material.GOLD_INGOT);
        MarcusItemCraftingRecipe.setIngredient('D', Material.DIAMOND);
        MarcusItemCraftingRecipe.setIngredient('A', Material.AMETHYST_SHARD);
        Bukkit.addRecipe(MarcusItemCraftingRecipe);
        Inventory EdmundItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Edmund").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.WHEAT, "", List.of(), "");
        Item2 = CreateItem(Material.WHEAT_SEEDS, "", List.of(), "");
        Item3 = CreateItem(Material.CARROT, "", List.of(), "");
        Item4 = CreateItem(Material.BONE_MEAL, "", List.of(), "");
        EdmundItemCraftingRecipeGUI.setItem(12, Item1);
        EdmundItemCraftingRecipeGUI.setItem(13, Item2);
        EdmundItemCraftingRecipeGUI.setItem(14, Item1);
        EdmundItemCraftingRecipeGUI.setItem(21, Item3);
        EdmundItemCraftingRecipeGUI.setItem(22, Item4);
        EdmundItemCraftingRecipeGUI.setItem(23, Item3);
        EdmundItemCraftingRecipeGUI.setItem(30, Item1);
        EdmundItemCraftingRecipeGUI.setItem(31, Item2);
        EdmundItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Edmund",
            "crops",
            "ewogICJ0aW1lc3RhbXAiIDogMTczMTA3NTM5NTk5NywKICAicHJvZmlsZUlkIiA6ICIzMzU3MWJiY2UyMDE0MTRiYmNkMDYyMjEyZTI4MjBlMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFkb21JbmF0b3I0NzgiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTRiMDY1MWZmZTc4NjI3N2Y1YzQ2ZDA0MmUzMjk5MmQxODEzZThmYTIwMGZiZTY0N2U3ZTExNzU1NTQ0ZjAxMCIKICAgIH0KICB9Cn0=",
            "Owl0tKu5XvedgGFcYWJSaOqi4gcHMi+G1KcTwFJWcjHc1edxJj3nponwC0S8j3Ro1uSvlSjZ8EhkgBDH86CP6Wc/lENoPw78EzhLfO97AHsWKK9TddhTzrkGgDhSiBpRRfDnX/FUzEfT/K2VRSqYTi+ZaZfj31Q9aYXu3mXPJLXKUpWsPz6BAoz93cOBIY+XEk42AwZgo4LBXePcavC0Iw5FeDk60iP0VH2Re94pAOwGHfoJ+thv5uoqwGMbHQPmESeYP0AgNBH6h9FGzrTjlrJKkSI2NM3Ed7ll9/kWPEzJ5TclBLDYA9TAhJSz2r0T2IS2F0YNPcBbyUbYy0lgptOFW/rqx47ViWm4bEOdE4uGncYN2X1OQCD3hHVkKn6EeTX5/apcUKNIXtbbI6DmdvGYAuAhXTMB8FcQRcUooxLRSoaWKjCmWJ7F/2gcaES7EmywrhwK28vmQMQGYFq1bxmfInTruuVmnA41fUqO/jRKAYbBi6qnweIQqNTR4uvEtWN195gzZe087uSwFKrqSyJgXSfD0/NDacQAd5VyIMkD8KqiT5j7ZqBddKF4L01ETvy+8QiJ+AQ4upi1ghWxLPz7FHbPCHHr1kg+KbBkWU2OQdW8KLvLikAXolnRnPZvlraj01ik5JuUAVvvMxWs3ehqYqmZT3HyYIYH2w8j2tA=",
            "My seed pouch went missing and I won't plant a thing without it. It'll turn up somewhere or maybe you can piece one together. Either way, bring it back and you'll have all the crops you need",
            "Seed Pouch",
            EdmundItemCraftingRecipeGUI,
            CreateItem(Material.WHEAT, "Buy crops", List.of("Buy seeds, crops and farming supplies"), ""),
            CreateItem(Material.CARROT, "Sell items", List.of("Sell crops in one go"), "")
        ));
        ShapedRecipe EdmundItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "edmunds_seed_pouch"), CreateItem(Material.ENCHANTED_BOOK, "Edmund's Seed Pouch", List.of("Hand this to Edmund"), "edmunds_seed_pouch"));
        EdmundItemCraftingRecipe.shape("WSW", "SBS", "WSW");
        EdmundItemCraftingRecipe.setIngredient('W', Material.WHEAT);
        EdmundItemCraftingRecipe.setIngredient('S', Material.WHEAT_SEEDS);
        EdmundItemCraftingRecipe.setIngredient('B', Material.BUNDLE);
        Bukkit.addRecipe(EdmundItemCraftingRecipe);
        Inventory GordanItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Gordan").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.STICK, "", List.of(), "");
        Item2 = CreateItem(Material.IRON_INGOT, "", List.of(), "");
        Item3 = CreateItem(Material.BARRIER, "", List.of(), "");
        GordanItemCraftingRecipeGUI.setItem(12, Item1);
        GordanItemCraftingRecipeGUI.setItem(13, Item2);
        GordanItemCraftingRecipeGUI.setItem(14, Item2);
        GordanItemCraftingRecipeGUI.setItem(21, Item1);
        GordanItemCraftingRecipeGUI.setItem(22, Item2);
        GordanItemCraftingRecipeGUI.setItem(23, Item2);
        GordanItemCraftingRecipeGUI.setItem(30, Item1);
        GordanItemCraftingRecipeGUI.setItem(31, Item3);
        GordanItemCraftingRecipeGUI.setItem(32, Item3);
        merchants.add(new Merchant("Gordan",
            "food",
            "ewogICJ0aW1lc3RhbXAiIDogMTYxNzMwMDU2MzQ4OCwKICAicHJvZmlsZUlkIiA6ICIzOWEzOTMzZWE4MjU0OGU3ODQwNzQ1YzBjNGY3MjU2ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJkZW1pbmVjcmFmdGVybG9sIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzRjZjQ3OTQ1OGIxZjFmNDNlMzQxMzI3MDllNmZjNjM5Yzg5NzhiMzNmOWViMmU0YTg2M2E1MzJiNTViMTYxZjIiCiAgICB9CiAgfQp9",
            "Q2zpXmyp+i8T+4CWKAOPVRpK1mQSoYCdWxza6LClcmUwfx4+gNKdn9qGwcZ9cPnVg8EsgckcY1ii5M8qnYqYxGRT1IBIrXaTAK9tT4z2jRMnyprZBYTnVDjc4p4POKE1f7i6L+XflgWnf59gSfo4yQGqyo8OtZhliBfBxdVCogTVgbEz87QUZa18IM6iL03wBJUtoy/RbINgPayvrbxhkEwkf3q374pqCTokTsTxqRo4V5J3f3wV3Z0ttCBCbXFuJ5DsuU9dFfbsezjfucGHBBkI78XarTFaTXqLYql8Poa1MZJiFDy/wYvwWNaiSmfNr2mV8vjp/MeaHBI4YtgVMknYadxnRAYOg9sNQ6wVpEsATfukUx1D3N1eBFDXF0R1CYR2R9+itm56iKcoziqx9Uu7+phiI1C5K6Lvyobi56LWA/CtwMt0M8WG0wYO66RZraZIoG34tmGC64U1SyclSay2qkxQr9+baE85JNQZJnpPg/GgM+ez7q5qiOo1hnV2a2+VEm49AkXwJ4I1CMoTAlQP4j47d7SEovMu6MyLHnPfVlmrbjfOVmSV9aoLqHpElPissA6BWaEQh62yqE3PsQVOIYWptqniFp3L1tPBAbPG9YlGnoVN3yNHE10f5v9U3z1T2RCnnke8YWoBMsVhl1rTMDn0jkM1TnxHIxslD60=",
            "No butcher knife, no cuts. Mine disappeared off my block one morning, gone without a trace. Find it or put one together, and I'll keep you fed better than anyone else in this place",
            "Butcher Knife",
            GordanItemCraftingRecipeGUI,
            CreateItem(Material.COOKED_BEEF, "Buy food", List.of("Buy meat and food supplies"), ""),
            CreateItem(Material.BEEF, "Sell items", List.of("Sell meat in one go"), "")
        ));
        ShapedRecipe GordanItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "gordans_butcher_knife"), CreateItem(Material.ENCHANTED_BOOK, "Gordan's Butcher Knife", List.of("Hand this to Gordan"), "gordans_butcher_knife"));
        GordanItemCraftingRecipe.shape("SII", "SII", "S  ");
        GordanItemCraftingRecipe.setIngredient('S', Material.STICK);
        GordanItemCraftingRecipe.setIngredient('I', Material.IRON_INGOT);
        Bukkit.addRecipe(GordanItemCraftingRecipe);
        Inventory SeraphimItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Seraphim").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.IRON_BLOCK, "", List.of(), "");
        Item2 = CreateItem(Material.IRON_INGOT, "", List.of(), "");
        Item3 = CreateItem(Material.IRON_NUGGET, "", List.of(), "");
        Item4 = CreateItem(Material.ANVIL, "", List.of(), "");
        SeraphimItemCraftingRecipeGUI.setItem(12, Item1);
        SeraphimItemCraftingRecipeGUI.setItem(13, Item2);
        SeraphimItemCraftingRecipeGUI.setItem(14, Item1);
        SeraphimItemCraftingRecipeGUI.setItem(21, Item2);
        SeraphimItemCraftingRecipeGUI.setItem(22, Item4);
        SeraphimItemCraftingRecipeGUI.setItem(23, Item2);
        SeraphimItemCraftingRecipeGUI.setItem(30, Item1);
        SeraphimItemCraftingRecipeGUI.setItem(31, Item2);
        SeraphimItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Seraphim",
            "tools",
            "ewogICJ0aW1lc3RhbXAiIDogMTcxMjM4MDEwMTk4NCwKICAicHJvZmlsZUlkIiA6ICI4ZTZlM2E4ZjRmYjA0NjhlOGQ5MTU5YmU3ZDYxYzRmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICAiR3Vpb2xobzY3OCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lYmMyYjAwMzI2MGE1ZGE1ZDNhNzVjNDg2ZWQyYjhlYzRmOGQ5M2RiNzg4MjUyYWQ5ZGMyNWY4ZTc0NWRlY2RjIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "nbzKTsLaMoQxuoAJtgp0skU3OmrSPRc8FgJAhjFKro/4fWJIpu6UjeSWFIbhIV9jgOy/GBYStD26cJ5yFRxuieZPUfVjLJguwqxuZsiGwuOoMoBw+LjSoEXePPjRKGnj2uekptipNaam6BkHj0wb3skcNO93E/L7l0qsfsJHINzV3aiPw8Wh5TkU+f4c9kev+bGrzUBN5y7dxOosA0klBv1j73XJcxQvA4HFwWkIrnX5hCZwY2X0fw/SQJXNKHskKZQdXnjhTU3hah81ZuDA1MkC7Fg3ccs/NiAq9XD3zItaKgqllhncSF5OvQzF2TnFrFVAPY88tso0unD3vBx4dVnRyIzlb6IsANvpltjZsN7t6JWKJle3JWggnN9lRpc6hfJBCEktPs8QFWBhO19zpcqLz0d3QGEu9nGG9d4PWQVVi9SFoQDqTXx/ji7hz8ZNTH/lYu+fFw4S7kDTy2S9iM2td/Hh/hB/MiO8hFq+ALZfgZbnvB0sqW1Fu93oLxJOiZlubvhSGDYllQYSXn3JcXIj1X2b7rwOlF5nIhcHacZVU23X8eANW5NHOde1IoYlB2WGz80Z94gPU0qNQQ1CD0akuyn6cUAcwY7xxlUQLgN/Amn0nMxdnZyVyuPV0F5vt3o+D7Gt5k50ZoomCq+taJAp3RVTpjEfy9PZ5sKn0to=",
            "An anvil is not supposed to break. Mine did. Years of use and one day it just gave out on me. Get me a new one and I will make sure you never go without a good tool",
            "Anvil",
            SeraphimItemCraftingRecipeGUI,
            CreateItem(Material.IRON_PICKAXE, "Buy tools", List.of("Buy tools and equipment"), ""),
            CreateItem(Material.IRON_INGOT, "Sell items", List.of("Sell tools in one go"), "")
        ));
        ShapedRecipe SeraphimItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "seraphims_anvil"), CreateItem(Material.ENCHANTED_BOOK, "Seraphim's Anvil", List.of("Hand this to Seraphim"), "seraphims_anvil"));
        SeraphimItemCraftingRecipe.shape("BIB", "IAI", "BIB");
        SeraphimItemCraftingRecipe.setIngredient('B', Material.IRON_BLOCK);
        SeraphimItemCraftingRecipe.setIngredient('I', Material.IRON_INGOT);
        SeraphimItemCraftingRecipe.setIngredient('A', Material.ANVIL);
        Bukkit.addRecipe(SeraphimItemCraftingRecipe);
        Inventory EdwardItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Edward").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.IRON_INGOT, "", List.of(), "");
        Item2 = CreateItem(Material.IRON_BLOCK, "", List.of(), "");
        Item3 = CreateItem(Material.SMITHING_TABLE, "", List.of(), "");
        EdwardItemCraftingRecipeGUI.setItem(12, Item1);
        EdwardItemCraftingRecipeGUI.setItem(13, Item2);
        EdwardItemCraftingRecipeGUI.setItem(14, Item1);
        EdwardItemCraftingRecipeGUI.setItem(21, Item2);
        EdwardItemCraftingRecipeGUI.setItem(22, Item3);
        EdwardItemCraftingRecipeGUI.setItem(23, Item2);
        EdwardItemCraftingRecipeGUI.setItem(30, Item1);
        EdwardItemCraftingRecipeGUI.setItem(31, Item2);
        EdwardItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Edward",
            "weapons",
            "ewogICJ0aW1lc3RhbXAiIDogMTYyMzA2MTMyNTc2NywKICAicHJvZmlsZUlkIiA6ICJjNTBhZmE4YWJlYjk0ZTQ1OTRiZjFiNDI1YTk4MGYwMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUd29FQmFlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzMTUwNTNhN2MzNWMzMGY0MjhiOGNkZjk0ZGJlZjlmMmY3YzBhZDRhYzEyYzUwNWE2YTVjODZkYzNjMTlhNDEiCiAgICB9CiAgfQp9",
            "qyI3DwgBDoliq/tCdSoAKGSJSz0P6M9T2rb0VQ7IFl42jnYDOITCPlIwuSlnjg7LcFMbVUt9rYZ61tuSR1NGJg/nJPt384hEfq8s4uq1vt02vWApo0SVzFPvqAuIu7GWC9NguSDF7AIqi7v6U8s/4U3BxG1PKoy6HFkyemPx5W7pZn2Z05TCj7zg/RGTu5pyuSPSBnYFjhcVQupkk+Uwke+/+Z6IwNPyxPAbqyn9gJICyxj/2MIVjpVtplqMKHlAzOv0IEv35xhRUg5kBgFLJ1WIb9zdNYs8VYsQJ/7NnvXOocLhtY7mr6mk1BxaA2nmZJdqPcVITfYKsa7ZIARhHoWaATdB+dTnk51v/6ShtC6oUGpOyWbexStcj9kHjk+YL84wNFA7tM+UOpchidODfsgcC1/LHB+DxbSm1Sf/qzIs6cHsiR87zDML9CE+xGM36jWMzcTnPeoou2naO1izJ6NhVHSWtON3SZ3QIgNnKakilklIUWh5IJN26mrArnHe5v+PfmQu2QGRIT0Z5nYsVyRJoSWZkaB8Qp35ZfF2NZ+fbek+XsCFt2D6lqIkwkR9CsI/xQwhpbaJzw2VuZF/jKpWolR+1VjXg4y/HX0Ur79AGmo0mHGJOC8o+XlJogM846ibmnwAkrUdpM45GMn4+6yw60i+1sktBNMaPs7Y2iyY=",
            "Every weapon I have ever forged was made on that table. It went missing and I have not been able to finish a single blade since. Bring me a new one and I will have the finest weapons ready for you",
            "Smithing Table",
            EdwardItemCraftingRecipeGUI,
            CreateItem(Material.IRON_SWORD, "Buy weapons", List.of("Buy swords, axes and more"), ""),
            CreateItem(Material.IRON_AXE, "Sell items", List.of("Sell weapons in one go"), "")
        ));
        ShapedRecipe EdwardItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "edwards_smithing_table"), CreateItem(Material.ENCHANTED_BOOK, "Edward's Smithing Table", List.of("Hand this to Edward"), "edwards_smithing_table"));
        EdwardItemCraftingRecipe.shape("IBI", "BSB", "IBI");
        EdwardItemCraftingRecipe.setIngredient('I', Material.IRON_INGOT);
        EdwardItemCraftingRecipe.setIngredient('B', Material.IRON_BLOCK);
        EdwardItemCraftingRecipe.setIngredient('S', Material.SMITHING_TABLE);
        Bukkit.addRecipe(EdwardItemCraftingRecipe);
        Inventory RowenaItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Rowena").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.LEATHER, "", List.of(), "");
        Item2 = CreateItem(Material.IRON_INGOT, "", List.of(), "");
        Item3 = CreateItem(Material.BLAST_FURNACE, "", List.of(), "");
        Item4 = CreateItem(Material.IRON_CHESTPLATE, "", List.of(), "");
        RowenaItemCraftingRecipeGUI.setItem(12, Item1);
        RowenaItemCraftingRecipeGUI.setItem(13, Item2);
        RowenaItemCraftingRecipeGUI.setItem(14, Item1);
        RowenaItemCraftingRecipeGUI.setItem(21, Item2);
        RowenaItemCraftingRecipeGUI.setItem(22, Item3);
        RowenaItemCraftingRecipeGUI.setItem(23, Item2);
        RowenaItemCraftingRecipeGUI.setItem(30, Item1);
        RowenaItemCraftingRecipeGUI.setItem(31, Item2);
        RowenaItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Rowena",
            "armor",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NzEyNTMyNjE5OCwKICAicHJvZmlsZUlkIiA6ICI0OWIzODUyNDdhMWY0NTM3YjBmN2MwZTFmMTVjMTc2NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJiY2QyMDMzYzYzZWM0YmY4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2UzMDEwNjljYWMxMDcwNTYyYWZjYTFjZWE2MTFhMTVkYTc3ZjU0ZDIwOTlkZmJiZGQ1OWIzMzg0MmJiY2NmNGUiCiAgICB9CiAgfQp9",
            "Mwyfw6+doePpRp6Poo/GQY7eESwK1W116hpd+ISNqP6DP7Oo5CBeMRVnCIijNU/XoMDgkyihbXF792DnvkK0mWubhS4qRlwxM9YA07iM2kyF+HBMX4+w7NpihgB+wiIIoyqkF5QN6ppKIl18m20jKM8EBsIuuY8CVluyccsbig3l1t+5toqWoVMdxPlr8DVvzxsyBOFX/pWrmFvwLpFdyT7CYRsrkWVCliXXndLfKAQXsIRFuRqxH5L/2fzlF/2TJ6F5mi4lN63qPtjzYf5zRoD1DEPIioe85LP+h1LkW4mECEtd1+RTcOKcOdUCr9gMQqlkH4vNRlpD78LBzPGdW2cmsn1HQg7DJ3cCD3t5uwbIPnyV4D+a0Ud1RKTldDwyY/CdAex7a8Gj04bl8jbsr/kGab4m71jz5BhPTzwiS90mbTZhbXkSBaFQNtO6tMtd+ZBkEvBcRwEqOEsI/MEQubtAoJBk0XUjWa4grRtDy/IzAvPRuj2F4VYeUErZcA3lgjk9wt4e1OP2D33gEMUwJ/ESbl375w0pKxYaypcnK+PRWth5hcyeQip+oJ/YQFD/qBQ28bndtFuo3ErRMag3zx4fyvveO3qMc0vywCoTCgL6oOLCmE1AKCun96rx83m6Vsy/cdk8erRkE3YzVTvPsruShbZCfNk1RLU9wkbGzDg=",
            "Without my bellows the blast furnace is useless and without the blast furnace I cannot smelt a thing. Craft me a new one and my stock is open to you",
            "Bellows",
            RowenaItemCraftingRecipeGUI,
            CreateItem(Material.IRON_CHESTPLATE, "Buy armor", List.of("Buy armor and protection"), ""),
            CreateItem(Material.IRON_INGOT, "Sell items", List.of("Sell armor in one go"), "")
        ));
        ShapedRecipe RowenaItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "rowenas_bellows"), CreateItem(Material.ENCHANTED_BOOK, "Rowena's Bellows", List.of("Hand this to Rowena"), "rowenas_bellows"));
        RowenaItemCraftingRecipe.shape("LIL", "IBI", "LIL");
        RowenaItemCraftingRecipe.setIngredient('L', Material.LEATHER);
        RowenaItemCraftingRecipe.setIngredient('I', Material.IRON_INGOT);
        RowenaItemCraftingRecipe.setIngredient('B', Material.BLAST_FURNACE);
        Bukkit.addRecipe(RowenaItemCraftingRecipe);
        Inventory LeoItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Leo").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.REDSTONE, "", List.of(), "");
        Item2 = CreateItem(Material.REDSTONE_TORCH, "", List.of(), "");
        Item3 = CreateItem(Material.COMPASS, "", List.of(), "");
        LeoItemCraftingRecipeGUI.setItem(12, Item1);
        LeoItemCraftingRecipeGUI.setItem(13, Item2);
        LeoItemCraftingRecipeGUI.setItem(14, Item1);
        LeoItemCraftingRecipeGUI.setItem(21, Item2);
        LeoItemCraftingRecipeGUI.setItem(22, Item3);
        LeoItemCraftingRecipeGUI.setItem(23, Item2);
        LeoItemCraftingRecipeGUI.setItem(30, Item1);
        LeoItemCraftingRecipeGUI.setItem(31, Item2);
        LeoItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Leo",
            "redstone",
            "ewogICJ0aW1lc3RhbXAiIDogMTYzNjE4MzE3MDYyNywKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmYzYjcwNWM2MGI0M2QzOTM1YzRhYjVhYzgxMmRjYjhhZTBkZTVmNmYzNDRhYTViN2JjYTg2N2ZjMGJiOTIyNyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "X+aYfonY+uuG+v4Gb0LMz0scJ5x1Dt3F4Y1sDQHoUoQVtjpcD+fMnbY0+KVCuKxVb8BsAFovBFsgFWG9E0Vw4O8pkuj1Um2q7Fh55aBZ/ggmukpysWJ6gZMPh/4nolFUR/mH4jxB36VTOHghlLM1IHpnpEx2z4/WkktCqXQOukQrExk7ufEh6UXYFE1rFMcJWxSpQLR/7xCeGpFtBoCvDF26eoWNdn7hGTc9179zwFebxadK5Al/XdnyZn1p/6e9hyckY+Rtbfe9V0Ke01v5kODQZkxNFTJbjHoA3GKjJO6Sso+l8muPZC1Vz00/wiyz9WlCegL40EWh8/1TSQ7Vej7sv4KKNzZEYMEpkU5qUf1DyymJ/cVJ+U2a/4zxrcpIhzgkI52bBAO5yvyhyv+Nw/4N7ZPOezE98DUocd4ryi8CiRMdHbljQQTDajYqrNP9G73aoP5suekQ2PAEeoAd52/v8PShpZIHLPzekzBT3Zsx3/QvwJH8zREVg9dGczHJj0da4Ljij/0Dtjs3FzALrkH5M6y+0kED9p3kElyXuEBFqF0gqLue+PKp+mnaeM4q8qYOFHBLyJaYusBfimfbuw87FVbUBCrOBiP0bCTr6P80wxoJ9z6noY874MeHgcBmOlqMo4h03YxTRiVu0JVM6eutfuMwqnwle1HR+VfoAP4=",
            "I cannot design a single circuit without my measuring compass. It is around here somewhere, or maybe it is not. Build me a new one and I will have every redstone component you could ever need",
            "Measuring Compass",
            LeoItemCraftingRecipeGUI,
            CreateItem(Material.REDSTONE, "Buy redstone", List.of("Buy redstone components and supplies"), ""),
            CreateItem(Material.REDSTONE_TORCH, "Sell items", List.of("Sell redstone items in one go"), "")
        ));
        ShapedRecipe LeoItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "leos_measuring_compass"), CreateItem(Material.ENCHANTED_BOOK, "Leo's Measuring Compass", List.of("Hand this to Leo"), "leos_measuring_compass"));
        LeoItemCraftingRecipe.shape("RTR", "TCT", "RTR");
        LeoItemCraftingRecipe.setIngredient('R', Material.REDSTONE);
        LeoItemCraftingRecipe.setIngredient('T', Material.REDSTONE_TORCH);
        LeoItemCraftingRecipe.setIngredient('C', Material.COMPASS);
        Bukkit.addRecipe(LeoItemCraftingRecipe);
        Inventory SylphieItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Sylphie").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.GLOWSTONE_DUST, "", List.of(), "");
        Item2 = CreateItem(Material.BOOK, "", List.of(), "");
        SylphieItemCraftingRecipeGUI.setItem(12, Item1);
        SylphieItemCraftingRecipeGUI.setItem(13, Item2);
        SylphieItemCraftingRecipeGUI.setItem(14, Item1);
        SylphieItemCraftingRecipeGUI.setItem(21, Item2);
        SylphieItemCraftingRecipeGUI.setItem(22, Item1);
        SylphieItemCraftingRecipeGUI.setItem(23, Item2);
        SylphieItemCraftingRecipeGUI.setItem(30, Item1);
        SylphieItemCraftingRecipeGUI.setItem(31, Item2);
        SylphieItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Sylphie",
            "enchanting",
            "ewogICJ0aW1lc3RhbXAiIDogMTc3OTYwNzg4ODU1MSwKICAicHJvZmlsZUlkIiA6ICJmMTBmYjBlNTkxOTE0YmZhYjIzMDU1NGFlMTc2ZWFlMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNeHN0aWNEYXZlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FmMThjZGRlY2ZlZWY5NTRmOWQ2NWJhNmU2ZjRmZThiYTNiNDEwNTVhOTlhODE5NDg2Y2Q4NTY5ZGIzY2JhMjciCiAgICB9CiAgfQp9",
            "ow2svmIre0SIB9PEd5xJn9PIC7XxTN5gRW3x41RTw7YzEYcnuITT57H8AmohChUiVyC9TO2LqyUUr0dv7R5i/e8jiGLamN11FmZBo+T3jQnAQsofDXNMAyax5dN5QEIyUkTFfGMLZSrz0khL4V51DsAIeWtgLRpTynROk5qNgJhhthIrube9rpSRSNSy1PMMnra+m5AOeFZcJn18ookDZmJWBk4zFm0RVnuMzKE+l+NCBdiz7g4rDQgBlWo2vu/oqXX1KJ/MV7BlFuyypoTZuMu9226zTNu2HCTgCoZKo5YH6r4MffubMyfKWl0bCvs3k4R8MZGurclw6X5QWR1dkDdOQUbD13y6lOPdQqL+R2R0h1SpcVJpD2i7+9LAhiqsAvE0lV6k0ACsrnW6qaraZL/sRAdiiUNTLuOqSoyPWeju/JgpjMZpRhBoJSBUZXLefNt0uua6229oZ14iFIpBHcLyJKDPeOJCbwdavDRfP8wIzUIvfGIve+c0TzRORTPtJyKnPqGuIhXOtOwNWyDkZ4CnqWG0Mcc9zP5zXR4bsjUmJJ23gSnkRu8LLorz6rHd8BScKgEoqWmF3myJULuKfeAhTlKbKTOx0b2huG15UuvnwBsiWyeDc14iTQoXFMjI8mTcbdR09IVX3HSow+sncDOYqRTgpwB6Hy5uPgB2FOc=",
            "I am Sylphie, keeper of enchanted knowledge. Return my grimoire to me, and I shall weave enchantments for those who are worthy enough to wield them",
            "Grimoire",
            SylphieItemCraftingRecipeGUI,
            CreateItem(Material.ENCHANTED_BOOK, "Enchanted books", List.of("Buy enchanted books"), ""),
            CreateItem(Material.ENCHANTED_BOOK, "Sell items", List.of("Sell enchanting related items in one go"), "")
        ));
        ShapedRecipe SylphieItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "sylphies_grimoire"), CreateItem(Material.ENCHANTED_BOOK, "Sylphies' Grimoire", List.of("Hand this to Sylphie"), "sylphies_grimoire"));
        SylphieItemCraftingRecipe.shape("GBG", "BGB", "GBG");
        SylphieItemCraftingRecipe.setIngredient('G', Material.GLOWSTONE_DUST);
        SylphieItemCraftingRecipe.setIngredient('B', Material.BOOK);
        Bukkit.addRecipe(SylphieItemCraftingRecipe);
        Inventory LunaItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Luna").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.BLAZE_POWDER, "", List.of(), "");
        Item2 = CreateItem(Material.NETHER_WART, "", List.of(), "");
        Item3 = CreateItem(Material.BREWING_STAND, "", List.of(), "");
        LunaItemCraftingRecipeGUI.setItem(12, Item1);
        LunaItemCraftingRecipeGUI.setItem(13, Item2);
        LunaItemCraftingRecipeGUI.setItem(14, Item1);
        LunaItemCraftingRecipeGUI.setItem(21, Item2);
        LunaItemCraftingRecipeGUI.setItem(22, Item3);
        LunaItemCraftingRecipeGUI.setItem(23, Item2);
        LunaItemCraftingRecipeGUI.setItem(30, Item1);
        LunaItemCraftingRecipeGUI.setItem(31, Item2);
        LunaItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Luna",
            "potions",
            "ewogICJ0aW1lc3RhbXAiIDogMTc3MTE1Mjk0NTQ4MywKICAicHJvZmlsZUlkIiA6ICJiZmQ3MjMxMGNmYWY0Yjc5OTNlYzhiYzU3ODg3YzU5ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbHBoYVNwQW0iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMThlY2U1ZDk3OWM2YTQ5MTNjOTE2MzgxMTFiMGFjZmE3YmVjOWNkYjQ2MjE4NTIwNmY3NmU0MThlNzE2MTk3IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "SQiu3mTF/CN8DXEvNMzVlZ6bm3J/2dnoLX2U1gEMIrhdrV0WZJTjRTrJPzNoabGCa95mHazaYRCN8Na9qwJw68q5jpPMr54OKh0x9k2ibm47ot44EkpslSA+bILYCe98h0k/L45EZ6rw4DSrNT6g4G6iDmhX87KrKkvDsSgQKiu/MOoY7Ts6fo8I3/Fd/+0haD/ndC+Psw7cYszRbDfjAihBnbWQKikSH/eKI1rkqmju5xh9lkG2EDPFMOKK9V02fmCp2mX270VWnI5GtoBMBFcV5u6u+wZmXiX5zN+pVyiqP6TXSUd2p0WUhic3CeMKU8JXParXjirfTjFUS37A/TAshWwK2Q0fgApNQn+7KGLR/oNtDz238R3ATpVk6KUee4VEZhwpoytntOdPuj+47zIo5JRpFxHSnBlbvKZEDOji47jnuomUFakKuUZpWfxovHM7qpD1vgri0T4axeJwYmyt4UkHQXQVOLA6IjdubjSprQvZNRjREeBRGsXIC3nZev2cWzmGWOHLQDpG5Q/hDnmzpNUqeI24vTfmuBcw4aqK4ZtTY0lsYCe/tXw731UfA0+C/ZyjzJeAnEqPM4eDlY4AtbAFDEqjbQjhKtQDF8MbtmRcfvzD57QqQBrooaPsY2fgYCj7ISF0a84v2Hs8WKr09G0psdGMmxdS6pMxt0E=",
            "My brewing stand vanished one night and without it every potion I know is stuck in my head with nowhere to go. Bring me a new one and I will brew anything you need",
            "Brewing Stand",
            LunaItemCraftingRecipeGUI,
            CreateItem(Material.POTION, "Buy potions", List.of("Buy potions and brewing supplies"), ""),
            CreateItem(Material.GLASS_BOTTLE, "Sell items", List.of("Sell potions and ingredients in one go"), "")
        ));
        ShapedRecipe LunaItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "lunas_brewing_stand"), CreateItem(Material.ENCHANTED_BOOK, "Luna's Brewing Stand", List.of("Hand this to Luna"), "lunas_brewing_stand"));
        LunaItemCraftingRecipe.shape("BNB", "NSN", "BNB");
        LunaItemCraftingRecipe.setIngredient('B', Material.BLAZE_POWDER);
        LunaItemCraftingRecipe.setIngredient('N', Material.NETHER_WART);
        LunaItemCraftingRecipe.setIngredient('S', Material.BREWING_STAND);
        Bukkit.addRecipe(LunaItemCraftingRecipe);
        Inventory AshItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Ash").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.IRON_INGOT, "", List.of(), "");
        Item2 = CreateItem(Material.BONE, "", List.of(), "");
        Item3 = CreateItem(Material.IRON_SWORD, "", List.of(), "");
        AshItemCraftingRecipeGUI.setItem(12, Item1);
        AshItemCraftingRecipeGUI.setItem(13, Item2);
        AshItemCraftingRecipeGUI.setItem(14, Item1);
        AshItemCraftingRecipeGUI.setItem(21, Item2);
        AshItemCraftingRecipeGUI.setItem(22, Item3);
        AshItemCraftingRecipeGUI.setItem(23, Item2);
        AshItemCraftingRecipeGUI.setItem(30, Item1);
        AshItemCraftingRecipeGUI.setItem(31, Item2);
        AshItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Ash",
            "mob_drops",
            "ewogICJ0aW1lc3RhbXAiIDogMTcxNjE1ODE0NTA2MywKICAicHJvZmlsZUlkIiA6ICIyNDY1ODI2NWVjMjg0NTY4YTg3MDJkOTVlYzdlYTc4MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcmdvc1oxMiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jZTJhMjc3MWNiMmYyOGZlMDkzOGZiNTNhZjE1OGI3YmY2Nzk3YjNmYzc0MTdkNjRlZmQ3NDkyMDUzNzdmYzYzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "gWCjrRwDSwRX8YFDwjXGKplDz7ZNvhiVSzM5SC4lcjUeg/oL9RjpWDFItZ7qwwMrAwZ+dcO1dNvW8lV13YrADOD5E2gEulqAKW8CnLwrbv6ZZkv6sjIFqVCuwQ2j3cEQCPYV0zz+kIQ3dnoDU2kkaTl/0N1h0gRcYvFCHbRlm/A7fblgqFt3aXu2K45/R6TXa49/U3oqCMlVnSvtbe7oe/5g87Mul6Qh8RcjviYl6pTFxtHON9etZBf7hFcmBtpnQG9pJXctm9OUfypKjAxrqOFUGpppkkilqIV8aaLgo7gFcNgkAvsLp62UnzpHAdY9wvLDPhaIo/vr4u5HcqwgAeW2oa81Fn25d84XRS4ytb9P5BsWm/sgI+jXg3jzccpueFiTp0uPj5hlK4cu43QhnNVHxonWFDT6Kl0sAP3qWlHzLjCaBW5UqnSt8QOWi4Wik2e0KtmYtsGEk95n8DzF7GcQs7KV9SS2L7uKtpd7bQQj2dmNf5/JBVHcdDtu88o9oPBSxeEt+ymcILoS+esJE7QoDwrIv+qifPMRUANPYW8H7PX1LwqQhAyiaT1p9/usvrwjz029op0aKQiUJaChI7uFIeGZwjUZbDsdRvMut6+hF87SgSsj4gMtdnC+V4yNhwd6OVk59v2q7BWUyY1dIQsUA03N+1O2oEM03OLNxLM=",
            "I do not hunt without my sword. It is out there somewhere and until I have it back I am not going anywhere. Bring it to me and everything I have collected is yours",
            "Hunting Sword",
            AshItemCraftingRecipeGUI,
            CreateItem(Material.ROTTEN_FLESH, "Buy mob drops", List.of("Buy mob drops and loot"), ""),
            CreateItem(Material.BONE, "Sell items", List.of("Sell mob drops in one go"), "")
        ));
        ShapedRecipe AshItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "ashs_hunting_sword"), CreateItem(Material.ENCHANTED_BOOK, "Ash's Hunting Sword", List.of("Hand this to Ash"), "ashs_hunting_sword"));
        AshItemCraftingRecipe.shape("IBI", "BSB", "IBI");
        AshItemCraftingRecipe.setIngredient('I', Material.IRON_INGOT);
        AshItemCraftingRecipe.setIngredient('B', Material.BONE);
        AshItemCraftingRecipe.setIngredient('S', Material.IRON_SWORD);
        Bukkit.addRecipe(AshItemCraftingRecipe);
        Inventory NoxItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Nox").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.GHAST_TEAR, "", List.of(), "");
        Item2 = CreateItem(Material.NETHERITE_INGOT, "", List.of(), "");
        Item3 = CreateItem(Material.NETHER_STAR, "", List.of(), "");
        NoxItemCraftingRecipeGUI.setItem(12, Item1);
        NoxItemCraftingRecipeGUI.setItem(13, Item2);
        NoxItemCraftingRecipeGUI.setItem(14, Item1);
        NoxItemCraftingRecipeGUI.setItem(21, Item2);
        NoxItemCraftingRecipeGUI.setItem(22, Item3);
        NoxItemCraftingRecipeGUI.setItem(23, Item2);
        NoxItemCraftingRecipeGUI.setItem(30, Item1);
        NoxItemCraftingRecipeGUI.setItem(31, Item2);
        NoxItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Nox",
            "spawners",
            "ewogICJ0aW1lc3RhbXAiIDogMTYxNTgzMDQ1MjczMCwKICAicHJvZmlsZUlkIiA6ICI3NzI3ZDM1NjY5Zjk0MTUxODAyM2Q2MmM2ODE3NTkxOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJsaWJyYXJ5ZnJlYWsiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTRjYTQxZGFkMjFlYTIzMzJlNDg4NTAyOTlmYzg1ODk4NTM3ZDljM2Q4N2UzZDQ5NDU2MDJmODM0NDk3Njk5OSIKICAgIH0KICB9Cn0=",
            "JrouHF+BYtNqIlMtTC12iJxZePtKE2I/ghBfvF0IyL3c/7y2e8ssp35OoFa5ZGfwNM8aR+Vd1CPdOSv+LPrfqUWv7DhOfWjZlMb08BKnTtsHgnZCBVt2k4fdvt5CeaWnxXM7opvgduCdgXp384U/qCN/XXlkAvSF9GUHnkTyLWb14T1Scn+9Hklid6Wo22vcRa/Msge/CJrXNAdV0DnGBFLRI3P9JWo2W8OiTPtkRzoTi/kJXZOX5392J4BgdIW0abSI4ikNJ9norWc0mIOw5a3RmZeOTEHNe4IqWxg9TBJxtw5hbgtgwlSq0zJB7am8fFREMIcz+jXP+dRQfcPzII6JT/IEo2+s1GWpahqL30v1zTt9hph6vi9yIaCodvzKsULbkSnBOL1U88YQJkZG1F4EPDni67IZ3aUQsQ4Tc0jZX7Incv28PAbolhgFEdZ9+GtA5l77DnsCrqQFJQcYOormLbiUqanVzACmh8Oj1z1XHQ2nEVLQd9mq42q01aPYa4dTWoZeAdFywDLmLIzGJAhdvU31IzDTQtIoZxQAzJdiOP8IH+Qe8Wcok9CrKH1DAbJ0v/1vGf8UjVrdfyj1VSAuaTj2qPWDdBYpYCBVn2g/KBu5hQJERND/y7osnVnKm1wM6UgSc+a2dNvGLtVPbAEkYCDzkPA1jm/PGc53G4c=",
            "My Harvester is missing. Without it I cannot harness what lurks in the unknown. Find me a new one and we can do business",
            "Harvester",
            NoxItemCraftingRecipeGUI,
            CreateItem(Material.SPAWNER, "Buy spawners", List.of("Buy mob spawners"), ""),
            CreateItem(Material.NETHER_STAR, "Sell items", List.of("Sell spawner related items in one go"), "")
        ));
        ShapedRecipe NoxItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "noxs_harvester"), CreateItem(Material.ENCHANTED_BOOK, "Nox's Harvester", List.of("Hand this to Nox"), "noxs_harvester"));
        NoxItemCraftingRecipe.shape("GNG", "NSN", "GNG");
        NoxItemCraftingRecipe.setIngredient('G', Material.GHAST_TEAR);
        NoxItemCraftingRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        NoxItemCraftingRecipe.setIngredient('S', Material.NETHER_STAR);
        Bukkit.addRecipe(NoxItemCraftingRecipe);
        Inventory LyraItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Lyra").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.AMETHYST_SHARD, "", List.of(), "");
        Item2 = CreateItem(Material.COPPER_INGOT, "", List.of(), "");
        Item3 = CreateItem(Material.SPYGLASS, "", List.of(), "");
        LyraItemCraftingRecipeGUI.setItem(12, Item1);
        LyraItemCraftingRecipeGUI.setItem(13, Item2);
        LyraItemCraftingRecipeGUI.setItem(14, Item1);
        LyraItemCraftingRecipeGUI.setItem(21, Item2);
        LyraItemCraftingRecipeGUI.setItem(22, Item3);
        LyraItemCraftingRecipeGUI.setItem(23, Item2);
        LyraItemCraftingRecipeGUI.setItem(30, Item1);
        LyraItemCraftingRecipeGUI.setItem(31, Item2);
        LyraItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Lyra",
            "decorative",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NzIwNzU2MjI0OCwKICAicHJvZmlsZUlkIiA6ICJmMTA0NzMxZjljYTU0NmI0OTkzNjM4NTlkZWY5N2NjNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJ6aWFkODciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA5OWY1N2FmM2Y3NzY4YjY4NmQ0OTc0NDY1NWVjYTYyYzA3YTAwOGVkZjA3Y2QxZTBkYjVjZTBkMzI4NDM0ZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "LDSt1hGBILFWxBb3i7hqcAgVtQWbXLj0iFOz74SLiQrc7hY9S/uQTXqTlKN+TZ1KL7doRngL1pBKAgRK/gACF1Q5OYjlRQ+o3B8wKYBn/IP4Boa3GBvHBXtXukK8MV7m2vY4+tWeqp7MOj/12hb3I9+E9eP77qvM4HWNJbOOhMnBukH+nuGR9acl2kcMqE4jd/QSswdtjdDNmMC8D/riIsZSITUD8u1Lur9+v7Ob4Sd3jPvZy6aH0o1MlTQekglVFkffDWWKHhM4/Dvd7bGeLmfnN9MFAj/QGE8WSlwgVkjt7AkFLsziGcCatOOf+FUcshol3xxyhODPsMYEhivcfvnfeiud706pT7AwxDGv0v+bbXqPDB8SbVMsxCdRK2fmM9HsHVPwboSJCQr3Jm2o6COk/qLw02bM+p7uEEDbBWbwOaKdaIzvaLWCaZHCNAFClqxDSV6ZGPiZM3690XE82ITj9T7HRYg8YpwwhFeGF8NMVYMFWSAyZ0Ua7msbFX2Z/X8+75NK6xJpjdFDICPlgTwJIx47gmWtzwAmB9tfsq89r29yTmUVrnWBUm5rmFOWAOFvPStDbWMkjMf6ERa3rbbSkKbtCVuvKKTFSpNagToTK6LpdwJmxPpn9vG0SP/mVemRZzFn7F1wHc5g55hnSrhGDfWbIoyNtkue8Ag0XzM=",
            "I have been everywhere and brought back everything. Lost my spyglass somewhere along the way though. Piece one together and I will sell you things you would not find anywhere else",
            "Spyglass",
            LyraItemCraftingRecipeGUI,
            CreateItem(Material.OAK_SAPLING, "Buy decorative", List.of("Buy saplings, corals, banner patterns and more"), ""),
            CreateItem(Material.TUBE_CORAL, "Sell items", List.of("Sell decorative items in one go"), "")
        ));
        ShapedRecipe LyraItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "lyras_spyglass"), CreateItem(Material.ENCHANTED_BOOK, "Lyra's Spyglass", List.of("Hand this to Lyra"), "lyras_spyglass"));
        LyraItemCraftingRecipe.shape("ACA", "CSC", "ACA");
        LyraItemCraftingRecipe.setIngredient('A', Material.AMETHYST_SHARD);
        LyraItemCraftingRecipe.setIngredient('C', Material.COPPER_INGOT);
        LyraItemCraftingRecipe.setIngredient('S', Material.SPYGLASS);
        Bukkit.addRecipe(LyraItemCraftingRecipe);
        Inventory FloraItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Flora").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.FEATHER, "", List.of(), "");
        Item2 = CreateItem(Material.INK_SAC, "", List.of(), "");
        Item3 = CreateItem(Material.ALLIUM, "", List.of(), "");
        FloraItemCraftingRecipeGUI.setItem(12, Item1);
        FloraItemCraftingRecipeGUI.setItem(13, Item2);
        FloraItemCraftingRecipeGUI.setItem(14, Item1);
        FloraItemCraftingRecipeGUI.setItem(21, Item2);
        FloraItemCraftingRecipeGUI.setItem(22, Item3);
        FloraItemCraftingRecipeGUI.setItem(23, Item2);
        FloraItemCraftingRecipeGUI.setItem(30, Item1);
        FloraItemCraftingRecipeGUI.setItem(31, Item2);
        FloraItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Flora",
            "dyes",
            "ewogICJ0aW1lc3RhbXAiIDogMTYxNDEwNTY0MDYwOSwKICAicHJvZmlsZUlkIiA6ICI5ZDEzZjcyMTcxM2E0N2U0OTAwZTMyZGVkNjBjNDY3MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUYWxvZGFvIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzY2MDNmMDRmYzhjYzVjNDJjM2M3NTYxNGQ3YTFkOWNlYjMxOTFiZDU4NDk5ODNkMDg2NWM3ZTQ5ZjllMjk3MmIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "XwOJn9etUedxaw0az0H53IUHM8sQQdRuCqOsGOqmyVyf1F+K19oMVRbrXpqqWgynXdD5WUn9xQIiIgUWA1zjCmdY/Evb9syd8URDcGJLm/zBjYahbhj124AaLcAXZTjbRsmrbwbhL/P9FgMT4zNEQijz3JMp7qLDGOzehiaulLOhRfYKOJ6Hh7295bnpugFnjCi7mMNlBqC1kXwIdexh1CaJ+jCbFPgn5MMxzpvj5wgqJFNJ0ZxKjxV9wfe/6DNJY3BUe6SNSctptJ8IuGn6Jkm6Nvq+b8QrOKcy9JeWjpsAiLB86F1GFRrpvlMijsk5/v0W07zZKspWOeE3gjLRysUIyQQ7KSUw9NTYfL+jrCHxLXmwN9SotI3r+TibYAfVoBVPNN3hD2kxgEtfkGNYRTYP4r3IHsEvy+e1h5rGOtsHakYSdlFyIc7JjlhADk2xlrb40lxf8aA3kP9NY+4cLwu7Yh8CNyi906L2bkgnQ15W7cnRCd1XpXpQhXlcrF5ea4sJ77md+/K7sydHDOWbWNaFW1lj9syQjfrvr6ParzSOxRjvYcc76kG3AILlqBau5mMgha/GrVbrqv7XHdNh+qyXrLXQDi72BvtMDIM1N+1c1CThbzstRyJmoxV94VSadXPBhsOvNHQdhINLRs/2MnBaozRKfgBRCjHXdX4aGZw=",
            "My lavender went missing and without it I cannot feel at home. Find me a new one and everything I have gathered from the forest is yours",
            "Lavender",
            FloraItemCraftingRecipeGUI,
            CreateItem(Material.ALLIUM, "Buy flowers and dyes", List.of("Buy flowers, ink and dyes"), ""),
            CreateItem(Material.INK_SAC, "Sell items", List.of("Sell flowers and dyes in one go"), "")
        ));
        ShapedRecipe FloraItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "floras_lavender"), CreateItem(Material.ENCHANTED_BOOK, "Flora's Lavender", List.of("Hand this to Flora"), "floras_lavender"));
        FloraItemCraftingRecipe.shape("FIF", "IAI", "FIF");
        FloraItemCraftingRecipe.setIngredient('F', Material.FEATHER);
        FloraItemCraftingRecipe.setIngredient('I', Material.INK_SAC);
        FloraItemCraftingRecipe.setIngredient('A', Material.ALLIUM);
        Bukkit.addRecipe(FloraItemCraftingRecipe);
        Inventory JasperItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Jasper").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.OAK_PLANKS, "", List.of(), "");
        Item2 = CreateItem(Material.MUSIC_DISC_13, "", List.of(), "");
        Item3 = CreateItem(Material.JUKEBOX, "", List.of(), "");
        JasperItemCraftingRecipeGUI.setItem(12, Item1);
        JasperItemCraftingRecipeGUI.setItem(13, Item2);
        JasperItemCraftingRecipeGUI.setItem(14, Item1);
        JasperItemCraftingRecipeGUI.setItem(21, Item2);
        JasperItemCraftingRecipeGUI.setItem(22, Item3);
        JasperItemCraftingRecipeGUI.setItem(23, Item2);
        JasperItemCraftingRecipeGUI.setItem(30, Item1);
        JasperItemCraftingRecipeGUI.setItem(31, Item2);
        JasperItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Jasper",
            "music",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NzI0MDMwNjE5NywKICAicHJvZmlsZUlkIiA6ICJkYTA1Y2Q3OWZkYjc0MDJlYTdjNjMzY2NkZmYzMDI4YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJGZXJuYW5kMGFsMG5zMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMTg0YjAxMjU3M2RlNDdjNDgyZGNhN2IzNDZjYzI1MmE4Njg5NTAzNWQ4MTkyZjE1MzkzMjBjYmM5ZTFiZGYwIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "tA920HsEjeGOPJiYwvyEzLxNLljreLEfnaGqm1cHrc/V0Wai7b9PTC8HV4CxFIdMZMKVUp7t+3n/uOhU7GL7IqXyoR77gPR0wGBNtRKlelz3JkXZLKorqvV6zIjw5lswLtNnu0nWQELjfRtLoA4Y7WCfcKDDSD3AmUdC+ETqs4H58/JXraHtc1O3AiHn6fkzWzN9dwqIIw/rPDAOiv9P1UPYMFvo1jV84cakCDsfcvklTBDPxLwLNqSsG7xvwIHe1/LwlkXbu8jyKRr+HLp8tOtdxx4skllnBbJkRZWvW9MnnPhGhUpka/oGH3MTFSoM9xi3YSjCvPySqaxK6bzF8GuM/snNaFVfi8cj7r0/sQmQnm1ewvWb750J8Hj0uJnetqYtzX7H6GfOUJeOmFOLvsvGLluaYudG+vj4IDgzQdUD9RjesBUyFLu8AhZyESuvf0SgYp1XJtCprk2+8Bm5Aq8CkqlO1juu9+hefdwBDnjUMhk/uHm7zWlTa4WIAwxrPCyi0GXsgrgcRgAfQwvb5MG7iJfrFC3RTmI6a5onPy6j2u7rtgERoHmUWlRkhGV1xzjhNU/gRMoKKJmhZV0s/HPZAdUlA2UfOlFFqxaEnRzmmhG3ap1rC+fXMjvcly8VgvC4E+kvXLS8VBWMO6DggQNvdLaIB4ipjCEpaMua1AE=",
            "A disc collector without a record player is just a man holding circles. Mine broke and I have not been the same since. Build me a new one and I will let you browse the collection",
            "Record Player",
            JasperItemCraftingRecipeGUI,
            CreateItem(Material.MUSIC_DISC_13, "Buy music discs", List.of("Buy all kinds of music discs"), ""),
            CreateItem(Material.JUKEBOX, "Sell items", List.of("Sell music discs in one go"), "")
        ));
        ShapedRecipe JasperItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "jaspers_record_player"), CreateItem(Material.ENCHANTED_BOOK, "Jasper's Record Player", List.of("Hand this to Jasper"), "jaspers_record_player"));
        JasperItemCraftingRecipe.shape("WDW", "DJD", "WDW");
        JasperItemCraftingRecipe.setIngredient('W', Material.OAK_PLANKS);
        JasperItemCraftingRecipe.setIngredient('D', Material.MUSIC_DISC_13);
        JasperItemCraftingRecipe.setIngredient('J', Material.JUKEBOX);
        Bukkit.addRecipe(JasperItemCraftingRecipe);
        Inventory RufusItemCraftingRecipeGUI = Bukkit.createInventory(null, 45, Component.text("Rufus").color(NamedTextColor.BLACK));
        Item1 = CreateItem(Material.OAK_PLANKS, "", List.of(), "");
        Item2 = CreateItem(Material.OAK_SLAB, "", List.of(), "");
        Item3 = CreateItem(Material.BARREL, "", List.of(), "");
        RufusItemCraftingRecipeGUI.setItem(12, Item1);
        RufusItemCraftingRecipeGUI.setItem(13, Item2);
        RufusItemCraftingRecipeGUI.setItem(14, Item1);
        RufusItemCraftingRecipeGUI.setItem(21, Item2);
        RufusItemCraftingRecipeGUI.setItem(22, Item3);
        RufusItemCraftingRecipeGUI.setItem(23, Item2);
        RufusItemCraftingRecipeGUI.setItem(30, Item1);
        RufusItemCraftingRecipeGUI.setItem(31, Item2);
        RufusItemCraftingRecipeGUI.setItem(32, Item1);
        merchants.add(new Merchant("Rufus",
            "storage",
            "ewogICJ0aW1lc3RhbXAiIDogMTc3NzA0NTgxMjI5NCwKICAicHJvZmlsZUlkIiA6ICI2MzA1MDhmN2YyODg0N2E1OTA0ZTk2Mjc3NmFjM2ExMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJkeWxhbmNhdHMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFlMDkyZjFmYjg2MzI1MjFkZDdlZTBkNzZlM2Q2ZGE5OTY0NTUwZDVhNWMzMjhhYTA4M2Q4ZDdmMTY4NTQ2MiIKICAgIH0KICB9Cn0=",
            "yA6p5DlDk5KKzbEydnzZtuVkCygaP181wHjip46mqQr2drf56CvlqIv4RCQ8cU5iYtt1ce8atoS09oWFIlL8SGokqv7NxW7rk08h43yLBxmeaJH06jB7j7v4hqRTmXmefmcbr+ab9PxBGOa1MMWiwWD3KqQVWqhnWdSvJgOcokusQOG0SwST5Vwy0fmOJrBT0qR8l8mWJVq8JZoMMwt82T916//JQwuW9eGA7CQjco66JrUJVKr3lSZdl3Wt7hkh7amnpP2BVjaJZMRfCLTtg3KZVE27RkKcKcKgYUmrt5qyePTtM2xEOZg4EYnzzdKEy0YUTpKbHfdXUVZfQW/fxXyj2q0FXlpVnubq+jS0N59kmdn9kacxQNIvMa/kO2td8gnkM4GvN46rm8fhgpY8C2qGKaSZ96CyXhFzvKiRVCR+Y/K32238wA47Gh6Y1Y0UtDZJpb9FWqmUant/4zrkbcmXhSBrvKK1XkCFOA8GQ4Wq9vv5GR/m5tKJA4X7O3D7Q60HIS+ewuxCrlzHpIoLRZq3i7TqKnT7s8jC1IiF72UgzUf3NVFSZKzmEJt//NtJ30dYDzNZAdcBvwJTny8JQZEH6jili7aA7+rXXfQDh/ESlVvZPZ1RJQi2iqmO8MGbAdWaP2/v0yCplhmUQAkNNn0Y4miu/pER46qn08VAR6A=",
            "My barrel went missing and without it I have nowhere to put anything. Find me a new one and I will have every storage solution you could ever need",
            "Barrel",
            RufusItemCraftingRecipeGUI,
            CreateItem(Material.CHEST, "Buy storage", List.of("Buy chests, barrels, shulker boxes and more"), ""),
            CreateItem(Material.BARREL, "Sell items", List.of("Sell storage items in one go"), "")
        ));
        ShapedRecipe RufusItemCraftingRecipe = new ShapedRecipe(new NamespacedKey(this, "rufus_barrel"), CreateItem(Material.ENCHANTED_BOOK, "Rufus's Barrel", List.of("Hand this to Rufus"), "rufus_barrel"));
        RufusItemCraftingRecipe.shape("PSP", "SBS", "PSP");
        RufusItemCraftingRecipe.setIngredient('P', Material.OAK_PLANKS);
        RufusItemCraftingRecipe.setIngredient('S', Material.OAK_SLAB);
        RufusItemCraftingRecipe.setIngredient('B', Material.BARREL);
        Bukkit.addRecipe(RufusItemCraftingRecipe);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                int PlayerCount = players.size();
                if (PlayerCount != 0) {
                    Random random = new Random();
                    Player RandomPlayer = players.get(random.nextInt(PlayerCount));
                    if (RandomPlayer.getWorld().getName().equals("world")) {
                        Merchant RandomMerchant = merchants.get(random.nextInt(merchants.size()));
                        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, RandomMerchant.name);
                        MetadataStore NPCData = npc.data();
                        NPCData.setPersistent("variant", "stray");
                        NPCData.setPersistent("creation_time", System.currentTimeMillis());
                        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                        npc.getOrAddTrait(LookClose.class).toggle();
                        NPCData.setPersistent(NPC.Metadata.GLOWING, true);
                        npc.setProtected(false);
                        npc.getOrAddTrait(TargetableTrait.class).setTargetable(true);
                        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(RandomMerchant.name, RandomMerchant.SkinSignature, RandomMerchant.SkinTexture);
                        World world = Bukkit.getWorld("world");
                        int NewX = RandomPlayer.getLocation().blockX() + random.nextInt(201) - 100;
                        int NewZ = RandomPlayer.getLocation().blockZ() + random.nextInt(201) - 100;
                        npc.spawn(new Location(world, NewX + 0.5, world.getHighestBlockYAt(NewX, NewZ) + 1, NewZ + 0.5));
                        RandomPlayer.sendMessage(Component.text("Hm... something feels different. Did someone just arrive nearby?").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true));
                    }
                }
            }
        }, 72000L, 72000L);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (NPC npc : CitizensAPI.getNPCRegistry()) {
                    MetadataStore NPCData = npc.data();
                    String NPCVariant = NPCData.get("variant");
                    if (NPCVariant.equals("stray") || NPCVariant.equals("stray_moving")) {
                        if (System.currentTimeMillis() - (long) NPCData.get("creation_time") >= 600000) {
                            npc.destroy();
                        }
                    }
                }
            }
        }, 2400L, 2400L);
        this.console = Bukkit.getConsoleSender();
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Wandering Merchants has been enabled");
    }
    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        String NPCName = npc.getName();
        for (int i=0; i<merchants.size(); i++) {
            Merchant MerchantData = merchants.get(i);
            if (NPCName.equals(MerchantData.name)) {
                Inventory GUI = Bukkit.createInventory(new InventoryOwner(NPCName + " " + npc.getId()), 45, Component.text(NPCName));
                Player player = event.getClicker();
                MetadataStore NPCData = npc.data();
                String NPCVariant = NPCData.get("variant");
                if (NPCVariant.equals("stray")) {
                    if (NPCData.get("in_interaction") == null) {
                        NPCData.setPersistent("in_interaction", true);
                        npc.getOrAddTrait(Waypoints.class).setWaypointProvider("linear");
                        NPCData.setPersistent(NPC.Metadata.GLOWING, false);
                        GUI.setItem(20, CreateItem(Material.PAPER, "Talk to " + NPCName, List.of(), ""));
                        GUI.setItem(22, CreateItem(Material.ENCHANTED_BOOK, MerchantData.ItemName, List.of("Left click to give " + MerchantData.ItemName + " to " + NPCName, "Right click to view " + NPCName + "'s " + MerchantData.ItemName + " recipe"), ""));
                        GUI.setItem(24, CreateItem(Material.LIME_CONCRETE, "Follow", List.of("Make " + NPCName + " follow you"), ""));
                    }
                    else {
                        player.sendMessage(MerchantData.name + ": Hold on, I am talking to someone else right now");
                        break;
                    }
                }
                else if (NPCVariant.equals("stray_moving")) {
                    GUI.setItem(20, CreateItem(Material.PAPER, "Talk to " + NPCName, List.of(), ""));
                    GUI.setItem(22, CreateItem(Material.ENCHANTED_BOOK, MerchantData.ItemName, List.of("Left click to give " + MerchantData.ItemName + " to " + NPCName, "Right click to view " + NPCName + "'s " + MerchantData.ItemName + " recipe"), ""));
                    String following = npc.getOrAddTrait(FollowTrait.class).getFollowing().getName();
                    ItemStack Item3;
                    if (player.getName().equals(following)) {
                        Item3 = CreateItem(Material.RED_CONCRETE, "Unfollow", List.of("Make " + NPCName + " stop following you"), "");
                    }
                    else {
                        Item3 = CreateItem(Material.WHITE_CONCRETE, "Following " + following, List.of(NPCName + " is currently following " + following), "");
                    }
                    GUI.setItem(24, Item3);
                }
                else if (NPCVariant.equals("shop")) {
                    GUI.setItem(20, MerchantData.ShopIcon);
                    GUI.setItem(22, MerchantData.SellGUIIcon);
                    GUI.setItem(24, CreateItem(Material.LIME_CONCRETE, "Follow", List.of("Make " + NPCName + " follow you"), ""));
                }
                else {
                    GUI.setItem(20, MerchantData.ShopIcon);
                    GUI.setItem(22, MerchantData.SellGUIIcon);
                    String following = npc.getOrAddTrait(FollowTrait.class).getFollowing().getName();
                    ItemStack Item3;
                    if (player.getName().equals(following)) {
                        Item3 = CreateItem(Material.RED_CONCRETE, "Unfollow", List.of("Make " + NPCName + " stop following you"), "");
                    }
                    else {
                        Item3 = CreateItem(Material.WHITE_CONCRETE, "Following " + following, List.of(NPCName + " is currently following " + following), "");
                    }
                    GUI.setItem(24, Item3);
                }
                GUI.setItem(36, CreateItem(Material.REDSTONE_BLOCK, "Health: " + String.format("%.1f", ((LivingEntity) npc.getEntity()).getHealth()) + "/20.0", List.of(), ""));
                player.openInventory(GUI);
                break;
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder owner = inventory.getHolder();
        if (owner instanceof InventoryOwner) {
            event.setCancelled(true);
            if (inventory.getItem(20) != null) {
                NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(((InventoryOwner) owner).owner.split(" ")[1]));
                if (npc != null) {
                    String NPCName = npc.getName();
                    for (int i=0; i<merchants.size(); i++) {
                        Merchant MerchantData = merchants.get(i);
                        if (NPCName.equals(MerchantData.name)) {
                            Player player = (Player) event.getWhoClicked();
                            int slot = event.getSlot();
                            MetadataStore NPCData = npc.data();
                            String NPCVariant = NPCData.get("variant");
                            if (NPCVariant.equals("stray")) {
                                if (slot == 20) {
                                    player.sendMessage(NPCName + ": " + MerchantData.dialogue);
                                    player.closeInventory();
                                }
                                else if (slot == 22) {
                                    if (event.getClick() == ClickType.RIGHT) {
                                        inventory.setContents(MerchantData.ItemCraftingRecipieGUI.getContents().clone());
                                    }
                                    else {
                                        String MerchantItemName = MerchantData.ItemName.toLowerCase();
                                        ItemStack ItemInHand = player.getInventory().getItemInMainHand();
                                        List<Component> ItemInHandLore = ItemInHand.lore();
                                        if (ItemInHandLore != null && ItemInHandLore.get(0).equals(Component.text("Hand this to " + NPCName))) {
                                            ItemInHand.setAmount(ItemInHand.getAmount()-1);
                                            player.sendMessage(NPCName + ": " + "Thank you for returning my " + MerchantItemName + " to me!");
                                            NPCData.remove("in_interaction");
                                            NPCData.setPersistent("variant", "shop");
                                            NPCData.setPersistent("shop_type", MerchantData.shop);
                                        }
                                        else {
                                            player.sendMessage(NPCName + ": " + "This is not my " + MerchantItemName);
                                        }
                                        player.closeInventory();
                                    }
                                }
                                else if (slot == 24) {
                                    player.sendMessage(NPCName + ": So where are we going?");
                                    NPCData.remove("in_interaction");
                                    NPCData.setPersistent("variant", "stray_moving");
                                    npc.getOrAddTrait(FollowTrait.class).follow(player);
                                    player.closeInventory();
                                }
                            }
                            else if (NPCVariant.equals("stray_moving")) {
                                if (slot == 20) {
                                    player.sendMessage(NPCName + ": " + MerchantData.dialogue);
                                    player.closeInventory();
                                }
                                else if (slot == 22) {
                                    if (event.getClick() == ClickType.RIGHT) {
                                        inventory.setContents(MerchantData.ItemCraftingRecipieGUI.getContents().clone());
                                    }
                                    else {
                                        String MerchantItemName = MerchantData.ItemName.toLowerCase();
                                        ItemStack ItemInHand = player.getInventory().getItemInMainHand();
                                        List<Component> ItemInHandLore = ItemInHand.lore();
                                        if (ItemInHandLore != null && ItemInHandLore.get(0).equals(Component.text("Hand this to " + NPCName))) {
                                            ItemInHand.setAmount(ItemInHand.getAmount()-1);
                                            player.sendMessage(NPCName + ": " + "Thank you for returning my " + MerchantItemName + " to me!");
                                            NPCData.setPersistent("variant", "shop");
                                            NPCData.setPersistent("shop_type", MerchantData.shop);
                                            npc.getOrAddTrait(FollowTrait.class).follow(null);
                                        }
                                        else {
                                            player.sendMessage(NPCName + ": " + "This is not my " + MerchantItemName);
                                        }
                                        player.closeInventory();
                                    }
                                }
                                else if (slot == 24) {
                                    if (inventory.getItem(24).getType() == Material.RED_CONCRETE) {
                                        player.sendMessage(NPCName + ": Alright then, I will be on my way!");
                                        NPCData.setPersistent("variant", "stray");
                                        npc.getOrAddTrait(FollowTrait.class).follow(null);
                                        player.closeInventory();
                                    }
                                }
                            }
                            else if (NPCVariant.equals("shop")) {
                                if (slot == 20) {
                                    Bukkit.dispatchCommand(console, "shop " + MerchantData.shop + " " + player.getName());
                                }
                                else if (slot == 22) {
                                    String PlayerName = player.getName();
                                    Bukkit.dispatchCommand(console, "lp user " + PlayerName + " permission set economyshopgui.sellgui." + MerchantData.shop + " true");
                                    Bukkit.dispatchCommand(console, "sellgui " + PlayerName);
                                }
                                else if (slot == 24) {
                                    player.sendMessage(NPCName + ": So where are we going?");
                                    NPCData.setPersistent("variant", "shop_moving");
                                    npc.getOrAddTrait(FollowTrait.class).follow(player);
                                    player.closeInventory();
                                }
                            }
                            else {
                                if (slot == 20) {
                                    Bukkit.dispatchCommand(console, "shop " + MerchantData.shop + " " + player.getName());
                                }
                                else if (slot == 22) {
                                    String PlayerName = player.getName();
                                    Bukkit.dispatchCommand(console, "lp user " + PlayerName + " permission set economyshopgui.sellgui." + MerchantData.shop + " true");
                                    Bukkit.dispatchCommand(console, "sellgui " + PlayerName);
                                }
                                else if (slot == 24) {
                                    if (inventory.getItem(24).getType() == Material.RED_CONCRETE) {
                                        player.sendMessage(NPCName + ": Alright then, I will stay here!");
                                        NPCData.setPersistent("variant", "shop");
                                        npc.getOrAddTrait(FollowTrait.class).follow(null);
                                        player.closeInventory();
                                    }
                                }
                            }
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
                if (NPCData.get("variant").equals("stray")) {
                    NPCData.remove("in_interaction");
                    npc.getOrAddTrait(Waypoints.class).setWaypointProvider("wander");
                }
            }
        }
        else if (owner.getClass().getSimpleName().equals("SellGUI")) {
            Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    Player player = (Player) event.getPlayer();
                    for (int i = 0; i < merchants.size(); i++) {
                        String permission = "economyshopgui.sellgui." + merchants.get(i).shop;
                        if (player.hasPermission(permission)) {
                            Bukkit.dispatchCommand(console, "lp user " + player.getName() + " permission unset " + permission);
                            break;
                        }
                    }
                }
            }, 10L);
        }
    }
    @EventHandler
    public void onNPCDeath(NPCDeathEvent event) {
        event.getNPC().destroy();
    }
    @Override
    public void onDisable() {
        getLogger().info("Wandering Merchants has been disabled");
    }
}