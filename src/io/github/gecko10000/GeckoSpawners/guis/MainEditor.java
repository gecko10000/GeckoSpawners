package io.github.gecko10000.GeckoSpawners.guis;

import io.github.gecko10000.GeckoSpawners.GeckoSpawners;
import io.github.gecko10000.GeckoSpawners.objects.SpawnCandidate;
import io.github.gecko10000.GeckoSpawners.objects.SpawnerObject;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.inventorygui.PaginationPanel;
import redempt.redlib.itemutils.ItemBuilder;

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
    }

    public void open(Player player) {
        gui.open(player);
    }

    private void addBottomBar() {
        gui.addButton(SIZE - 3, ItemButton.create(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE), evt -> {
            panel.nextPage();
            update();
        }));
        gui.addButton(SIZE - 5, ItemButton.create(new ItemBuilder(Material.SPAWNER), evt -> {
            SpawnerObject spawner = new SpawnerObject();
            panel.setPage(panel.getMaxPage());
            plugin.spawnerObjects.add(spawner);
            plugin.spawnerConfig.save();
            new SpawnerEditor(plugin, (Player) evt.getWhoClicked(), spawner);
            update();
        }));
        gui.addButton(SIZE - 7, ItemButton.create(new ItemBuilder(Material.RED_STAINED_GLASS_PANE), evt -> {
            panel.prevPage();
            update();
        }));
    }

    private List<ItemButton> spawnerButtons() {
        return plugin.spawnerObjects.stream()
                .map(this::buttonForSpawner)
                .collect(Collectors.toList());
    }

    private ItemButton buttonForSpawner(SpawnerObject spawner) {
        List<ItemStack> displayItems = spawner.getDisplayItems();
        return ItemButton.create(displayItems.size() == 0 ? new ItemStack(Material.GLASS) : displayItems.get(0),
                evt -> new SpawnerEditor(plugin, (Player) evt.getWhoClicked(), spawner));
    }

    public void update() {
        panel.clear();
        panel.addPagedButtons(spawnerButtons());
        gui.getInventory().getItem(SIZE - 3).setAmount(Math.min(panel.getMaxPage(), panel.getPage() + 1));
        gui.getInventory().getItem(SIZE - 7).setAmount(Math.max(1, panel.getPage() - 1));
    }

}
