package io.github.gecko10000.GeckoSpawners.guis;

import io.github.gecko10000.GeckoSpawners.GeckoSpawners;
import io.github.gecko10000.GeckoSpawners.objects.SpawnCandidate;
import io.github.gecko10000.GeckoSpawners.objects.SpawnerObject;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.inventorygui.PaginationPanel;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnerEditor {

    private final GeckoSpawners plugin;
    private final SpawnerObject spawner;

    private final InventoryGUI gui;
    private final PaginationPanel panel;
    private final static int SIZE = 54;

    public SpawnerEditor(GeckoSpawners plugin, Player player, SpawnerObject spawner) {
        this.plugin = plugin;
        this.spawner = spawner;

        this.gui = new InventoryGUI(Bukkit.createInventory(null, SIZE, plugin.makeReadable(Lang.guiSpawnerTitle)));
        this.panel = new PaginationPanel(gui);
        addBottomBar();
        panel.addSlots(0, SIZE - 9);
        update();
        gui.setReturnsItems(false);
        gui.open(player);
    }

    private void addBottomBar() {
        gui.addButton(SIZE - 9, ItemButton.create(new ItemStack(Material.NETHER_STAR), evt -> {
            plugin.editor.update();
            plugin.editor.open((Player) evt.getWhoClicked());
        }));
        gui.addButton(SIZE - 7, ItemButton.create(new ItemStack(Material.RED_STAINED_GLASS_PANE), evt -> {
            panel.prevPage();
            update();
        }));
        gui.addButton(SIZE - 5, ItemButton.create(new ItemStack(Material.GREEN_STAINED_GLASS), evt -> {
            //TODO: spawn selection
            update();
            plugin.spawnerConfig.save();
        }));
        gui.addButton(SIZE - 3, ItemButton.create(new ItemStack(Material.LIME_STAINED_GLASS_PANE), evt -> {
            panel.nextPage();
            update();
        }));
    }

    private List<ItemButton> candidateButtons() {
        return spawner.getCandidates().stream()
                .map(this::buttonForCandidate)
                .collect(Collectors.toList());
    }

    private ItemButton buttonForCandidate(SpawnCandidate candidate) {
        return ItemButton.create(candidate.getDisplayItem(), evt -> {
            //TODO: spawn selection
        });
    }

    public void update() {
        panel.clear();
        panel.addPagedButtons(candidateButtons());
        gui.getInventory().getItem(SIZE - 3).setAmount(Math.min(panel.getMaxPage(), panel.getPage() + 1));
        gui.getInventory().getItem(SIZE - 7).setAmount(Math.max(1, panel.getPage() - 1));
    }

}
