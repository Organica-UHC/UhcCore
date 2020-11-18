package com.gmail.val59000mc.inventory.kit;

import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.inventory.UhcInventoryContent;
import com.gmail.val59000mc.inventory.UhcInventoryItem;
import com.gmail.val59000mc.inventory.UhcInventoryItemStack;
import com.gmail.val59000mc.inventory.util.ScrollableRow;
import com.gmail.val59000mc.kit.Kit;
import com.gmail.val59000mc.kit.KitGroup;
import com.gmail.val59000mc.kit.KitsManager;
import com.gmail.val59000mc.kit.upgrade.PlayerKitUpgrades;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KitSelectionInventory extends UhcInventoryContent {

    private static final String TITLE = "Select kit";

    private final @NotNull KitsManager kitsManager;
    private final @NotNull UhcPlayer player;

    private final Map<Integer, ScrollableRow> rowByIndex = new HashMap<>();

    public KitSelectionInventory(@NotNull KitsManager kitsManager, @NotNull UhcPlayer player) {
        super(countLines(kitsManager), TITLE);

        this.kitsManager = kitsManager;
        this.player = player;

        initialize();
    }

    private void initialize() {
        int index = 0;
        int y = 0;
        for (KitGroup group : kitsManager.getGroups()) {
            List<Kit> kits = kitsManager.getKits(group);
            if (kits.isEmpty()) continue;

            ScrollableRow row = new ScrollableRow(
                    kits.stream()
                            .map(kit -> new KitInventoryItem(this, player, kit))
                            .collect(Collectors.toList()),
                    0, y, 9, group.getLines()
            );

            row.setNextButton(new NextPageInventoryItem(this, row));
            row.setPrevButton(new PrevPageInventoryItem(this, row));

            this.rowByIndex.put(index++, row);
            y += group.getLines();
        }

        rowByIndex.values().forEach(row -> row.render(this));
    }

    private void update() {
        rowByIndex.values().forEach(row -> row.render(this));
    }

    private static int countLines(@NotNull KitsManager kitsManager) {
        int lines = 0;
        for (KitGroup group : kitsManager.getGroups()) {
            List<Kit> kits = kitsManager.getKits(group);
            if (kits.isEmpty()) continue;
            lines += group.getLines();
        }
        return Math.max(Math.min(lines, 6), 1);
    }

    private static class KitInventoryItem implements UhcInventoryItem {

        private final @NotNull KitSelectionInventory inventory;
        private final @NotNull UhcPlayer player;
        private final @NotNull Kit kit;

        private final @NotNull KitDisplayItems displayItems;

        public KitInventoryItem(@NotNull KitSelectionInventory inventory, @NotNull UhcPlayer player, @NotNull Kit kit) {
            this.inventory = inventory;
            this.player = player;
            this.kit = kit;

            displayItems = kit.getDisplayItems(inventory.kitsManager, player, player.getKitUpgrades());
        }

        @Override
        public @Nullable ItemStack getDisplay() {
            ItemStack result = kit.getDisplayItems().clone();
            ItemMeta meta = result.getItemMeta();
            if (meta == null) return result;

            for (Enchantment enchantment : meta.getEnchants().keySet()) meta.removeEnchant(enchantment);
            if (kit == player.getKit()) meta.addEnchant(Enchantment.LUCK, 1, true);

            List<String> lore = new ArrayList<>();
            lore.add("[\"\"]");
            lore.add("[{\"text\":\"\",\"color\":\"white\",\"italic\":\"false\"},{\"text\":\"ЛКМ\",\"color\":\"green\"},{\"text\":\" - чтобы выбрать набор\",\"color\":\"light_gray\"}]");
            lore.add("[{\"text\":\"\",\"color\":\"white\",\"italic\":\"false\"},{\"text\":\"ПКМ\",\"color\":\"green\"},{\"text\":\" - чтобы прокачать или посмотреть набор\",\"color\":\"light_gray\"}]");

            displayItems.setLore(meta, lore);

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);

            result.setItemMeta(meta);
            return result;
        }

        @Override
        public void on(@NotNull InventoryClickEvent event) {
            if (event.getAction() == InventoryAction.PICKUP_HALF) {
                KitUpgradeInventory inventory = new KitUpgradeInventory(this.inventory.kitsManager, player, kit);
                inventory.openFor(event.getWhoClicked());
            }
            else if (event.getAction() == InventoryAction.PICKUP_ALL) {
                player.setKit(kit);
                try {
                    Player player = this.player.getPlayer();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 2);
                } catch (UhcPlayerNotOnlineException ignore) {
                }
                inventory.update();
            }
        }

    }

    private static class NextPageInventoryItem extends UhcInventoryItemStack {

        private final @NotNull KitSelectionInventory inventory;
        private final @NotNull ScrollableRow row;

        public NextPageInventoryItem(@NotNull KitSelectionInventory inventory, @NotNull ScrollableRow row) {
            super(new ItemStack(Material.ARROW));
            this.inventory = inventory;
            this.row = row;
        }

        @Override
        public void on(@NotNull InventoryClickEvent event) {
            row.next();
            inventory.update();
        }

    }

    private static class PrevPageInventoryItem extends UhcInventoryItemStack {

        private final @NotNull KitSelectionInventory inventory;
        private final @NotNull ScrollableRow row;

        public PrevPageInventoryItem(@NotNull KitSelectionInventory inventory, @NotNull ScrollableRow row) {
            super(new ItemStack(Material.SPECTRAL_ARROW));
            this.inventory = inventory;
            this.row = row;
        }

        @Override
        public void on(@NotNull InventoryClickEvent event) {
            row.prev();
            inventory.update();
        }

    }

}
