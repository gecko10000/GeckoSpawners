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
        gui.setDestroyOnClose(false);
        gui.setReturnsItems(false);
        open(player);
    }

    public void open(Player player) {
        update();
        gui.open(player);
        plugin.spawnerConfig.save();
    }

    private void addBottomBar() {
        gui.addButton(SIZE - 9, ItemButton.create(plugin.makeItem(Material.RED_STAINED_GLASS_PANE, Lang.back), evt -> {
            plugin.editor.update();
            plugin.editor.open((Player) evt.getWhoClicked());
        }));
        gui.addButton(SIZE - 7, ItemButton.create(plugin.makeItem(Material.RED_STAINED_GLASS_PANE, Lang.prevPage), evt -> {
            panel.prevPage();
            update();
        }));
        gui.addButton(SIZE - 5, ItemButton.create(new ItemStack(Material.GREEN_STAINED_GLASS), evt -> {
            SpawnCandidate candidate = new SpawnCandidate();
            spawner.add(candidate);
            enterEditMode((Player) evt.getWhoClicked(), candidate);
        }));
        gui.addButton(SIZE - 3, ItemButton.create(plugin.makeItem(Material.LIME_STAINED_GLASS_PANE, Lang.nextPage), evt -> {
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
            switch (evt.getClick()) {
                case SHIFT_LEFT:
                case SHIFT_RIGHT:
                    spawner.getCandidates().remove(candidate);
                    plugin.spawnerConfig.save();
                    update();
                    break;
                case RIGHT:
                    //TODO: weight setting
                default:
                    enterEditMode((Player) evt.getWhoClicked(), candidate);
            }
        });
    }

    private void enterEditMode(Player player, SpawnCandidate candidate) {
        plugin.previousEditors.put(player, this);
        plugin.editingCandidates.put(player, candidate);
        update();
        player.closeInventory();
        plugin.makeReadable(Lang.enterEditMode).forEach(player::sendMessage);
        plugin.spawnerConfig.save();
    }

    public void update() {
        panel.clear();
        panel.addPagedButtons(candidateButtons());
        gui.getInventory().getItem(SIZE - 3).setAmount(Math.min(panel.getMaxPage(), panel.getPage() + 1));
        gui.getInventory().getItem(SIZE - 7).setAmount(Math.max(1, panel.getPage() - 1));
    }

}
