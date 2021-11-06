package io.github.gecko10000.GeckoSpawners.guis;

import io.github.gecko10000.GeckoSpawners.GeckoSpawners;
import io.github.gecko10000.GeckoSpawners.objects.SpawnCandidate;
import io.github.gecko10000.GeckoSpawners.objects.SpawnerObject;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import io.github.gecko10000.GeckoSpawners.util.ShortWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.inventorygui.PaginationPanel;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SpawnerEditor {

    private final GeckoSpawners plugin;
    private final Player player;
    private final SpawnerObject spawner;

    private final InventoryGUI gui;
    private final PaginationPanel panel;
    private final static int SIZE = 54;

    public SpawnerEditor(GeckoSpawners plugin, Player player, SpawnerObject spawner) {
        this.plugin = plugin;
        this.player = player;
        this.spawner = spawner;

        this.gui = new InventoryGUI(Bukkit.createInventory(null, SIZE, GeckoSpawners.makeReadable(Lang.guiSpawnerTitle
                .replace("%name%", spawner.name))));
        this.panel = new PaginationPanel(gui);
        addBottomBar();
        panel.addSlots(0, SIZE - 9);
        gui.setDestroyOnClose(false);
        gui.setReturnsItems(false);
        open();
    }

    public void open() {
        update();
        gui.open(player);
        plugin.spawnerConfig.save();
    }
    //TODO: add controls for spawner attributes (there are 7!)
    private void addBottomBar() {
        gui.addButton(SIZE - 9, ItemButton.create(plugin.backItem(), evt -> {
            plugin.editor.update();
            plugin.editor.open((Player) evt.getWhoClicked());
        }));
        gui.addButton(SIZE - 7, ItemButton.create(plugin.pageItem(true), evt -> {
            panel.prevPage();
            update();
        }));
        gui.addButton(SIZE - 5, ItemButton.create(new ItemBuilder(plugin.makeItem(Material.GREEN_STAINED_GLASS, Lang.guiSpawnerCreateCandidate))
                .addEnchant(Enchantment.DURABILITY, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS), evt -> {
            SpawnCandidate candidate = new SpawnCandidate();
            spawner.add(candidate);
            enterEditMode((Player) evt.getWhoClicked(), candidate);
        }));
        gui.addButton(SIZE - 3, ItemButton.create(plugin.pageItem(false), evt -> {
            panel.nextPage();
            update();
        }));
        gui.addButton(SIZE - 1, ItemButton.create(plugin.makeItem(Material.NAME_TAG, Lang.renameSpawner), evt -> {
            settingName = new CompletableFuture<>();
            enterEditMode((Player) evt.getWhoClicked(), null);
            settingName.thenAccept(name -> {
                SpawnerObject temp = plugin.spawnerObjects.remove(this.spawner.name);
                this.spawner.name = name;
                plugin.spawnerObjects.put(name, temp);
                plugin.spawnerConfig.save();
                new SpawnerEditor(plugin, player, spawner);
            });
        }));
    }

    public CompletableFuture<String> settingName;

    private List<ItemButton> candidateButtons() {
        return spawner.getCandidates().stream()
                .map(this::buttonForCandidate)
                .collect(Collectors.toList());
    }

    private ItemButton buttonForCandidate(SpawnCandidate candidate) {
        return ItemButton.create(candidate.getDisplayItem(), evt -> {
            switch (evt.getClick()) {
                case SHIFT_LEFT, SHIFT_RIGHT -> {
                    spawner.remove(candidate);
                    plugin.spawnerConfig.save();
                    update();
                }
                default -> enterEditMode((Player) evt.getWhoClicked(), candidate);
            }
        });
    }

    private ItemButton buttonForData(ShortWrapper spawnerData, Material material, String name) {
        ItemStack item = new ItemBuilder(material).setCount(spawnerData.value);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(GeckoSpawners.makeReadable(Lang.guiSpawnerDataButton)
                .replaceText(builder -> builder.matchLiteral("%data%")
                        .replacement(name)));
        meta.lore(GeckoSpawners.makeReadable(Lang.guiSpawnerDataButtonLore));
        item.setItemMeta(meta);
        return ItemButton.create(item, evt -> {
            if (evt.isLeftClick()) {
                spawnerData.value += evt.isShiftClick() ? 10 : 1;
            } else if (evt.isRightClick()) {
                spawnerData.value -= evt.isShiftClick() ? 10 : 1;
            }
            if (spawnerData.value < 0) {
                spawnerData.value = 0;
            }
            update();
        });
    }

    private void enterEditMode(Player player, SpawnCandidate candidate) {
        plugin.previousEditors.put(player, this);
        plugin.editingCandidates.put(player, candidate);
        update();
        player.closeInventory();
        if (candidate != null) {
            GeckoSpawners.makeReadable(Lang.enterEditMode).forEach(player::sendMessage);
        } else {
            player.sendMessage(GeckoSpawners.makeReadable(Lang.enterNamingMode.replace("%name%", this.spawner.name)));
        }
        plugin.spawnerConfig.save();
    }

    public SpawnerObject getSpawner() {
        return spawner;
    }

    public void update() {
        panel.clear();
        panel.addPagedButtons(candidateButtons());
        gui.getInventory().getItem(SIZE - 3).setAmount(Math.min(panel.getMaxPage(), panel.getPage() + 1));
        gui.getInventory().getItem(SIZE - 7).setAmount(Math.max(1, panel.getPage() - 1));
    }

}
