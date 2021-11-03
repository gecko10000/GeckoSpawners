package io.github.gecko10000.GeckoSpawners.guis;

import io.github.gecko10000.GeckoSpawners.GeckoSpawners;
import io.github.gecko10000.GeckoSpawners.objects.SpawnerObject;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.inventorygui.PaginationPanel;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MainEditor {

    private final GeckoSpawners plugin;

    private final InventoryGUI gui;
    private final PaginationPanel panel;
    private static final int SIZE = 54;

    public MainEditor(GeckoSpawners plugin) {
        this.plugin = plugin;
        this.gui = new InventoryGUI(Bukkit.createInventory(null, SIZE, plugin.makeReadable(Lang.guiMainTitle)));
        this.panel = new PaginationPanel(gui);
        addBottomBar();
        panel.addSlots(0, SIZE - 9);
        update();
        gui.setDestroyOnClose(false);
        gui.setReturnsItems(false);
    }

    public void open(Player player) {
        update();
        gui.open(player);
    }

    private void addBottomBar() {
        gui.addButton(SIZE - 3, ItemButton.create(plugin.makeItem(Material.LIME_STAINED_GLASS_PANE, Lang.nextPage), evt -> {
            panel.nextPage();
            update();
        }));
        gui.addButton(SIZE - 5, ItemButton.create(new ItemBuilder(plugin.makeItem(Material.SPAWNER, Lang.guiMainCreate))
                .addEnchant(Enchantment.DURABILITY, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS), evt -> {
            SpawnerObject spawner = new SpawnerObject();
            panel.setPage(panel.getMaxPage());
            plugin.spawnerObjects.put(spawner.id, spawner);
            plugin.spawnerConfig.save();
            new SpawnerEditor(plugin, (Player) evt.getWhoClicked(), spawner);
            update();
        }));
        gui.addButton(SIZE - 7, ItemButton.create(plugin.makeItem(Material.RED_STAINED_GLASS_PANE, Lang.prevPage), evt -> {
            panel.prevPage();
            update();
        }));
    }

    private List<ItemButton> spawnerButtons() {
        return plugin.spawnerObjects.values().stream()
                .map(this::buttonForSpawner)
                .collect(Collectors.toList());
    }

    private ItemButton buttonForSpawner(SpawnerObject spawner) {
        return ItemButton.create(spawner.getDisplayItem(), evt -> {
            Player player = (Player) evt.getWhoClicked();
            switch (evt.getClick()) {
                case SHIFT_RIGHT:
                case SHIFT_LEFT:
                    plugin.spawnerObjects.remove(spawner.id);
                    plugin.spawnerConfig.save();
                    update();
                    break;
                case MIDDLE:
                    ItemUtils.give(player, spawner.getSpawner());
                    break;
                default:
                    new SpawnerEditor(plugin, player, spawner);
            }
        });
    }

    public void update() {
        panel.clear();
        panel.addPagedButtons(spawnerButtons());
        gui.getInventory().getItem(SIZE - 3).setAmount(Math.min(panel.getMaxPage(), panel.getPage() + 1));
        gui.getInventory().getItem(SIZE - 7).setAmount(Math.max(1, panel.getPage() - 1));
    }

}
