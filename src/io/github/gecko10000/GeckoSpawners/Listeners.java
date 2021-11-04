package io.github.gecko10000.GeckoSpawners;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import io.github.gecko10000.GeckoSpawners.guis.SpawnerEditor;
import io.github.gecko10000.GeckoSpawners.objects.SpawnCandidate;
import io.github.gecko10000.GeckoSpawners.objects.SpawnerObject;
import io.github.gecko10000.GeckoSpawners.util.Config;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import redempt.redlib.misc.EventListener;
import redempt.redlib.misc.Task;

public class Listeners implements Listener {

    private final GeckoSpawners plugin;

    public Listeners(GeckoSpawners plugin) {
        this.plugin = plugin;
        new EventListener<>(SpawnerSpawnEvent.class, evt -> {
            if (!Config.centerFallingBlocks) {
                return;
            }
            if (evt.getEntityType() != EntityType.FALLING_BLOCK) {
                return;
            }
            evt.getEntity().teleport(evt.getLocation().toCenterLocation());
        });

        new EventListener<>(PlayerQuitEvent.class, evt -> {
            Player player = evt.getPlayer();
            plugin.editingCandidates.remove(player);
            plugin.previousEditors.remove(player);
        });

        new EventListener<>(PlayerDropItemEvent.class, evt -> {
            Player player = evt.getPlayer();
            if (plugin.editingCandidates.get(player) == null) {
                return;
            }
            SpawnerEditor editor = plugin.previousEditors.remove(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            finishEditing(player, editor).set(evt.getItemDrop().getItemStack());
        });

        new EventListener<>(EntityDamageByEntityEvent.class, evt -> {
            if (!(evt.getDamager() instanceof Player player)) {
                return;
            }
            if (plugin.editingCandidates.get(player) == null) {
                return;
            }
            SpawnerEditor editor = plugin.previousEditors.remove(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            finishEditing(player, editor).set(evt.getEntityType());
        });

        new EventListener<>(PlayerInteractAtEntityEvent.class, evt -> {
            Player player = evt.getPlayer();
            if (plugin.editingCandidates.get(player) == null) {
                return;
            }
            SpawnerEditor editor = plugin.previousEditors.remove(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            finishEditing(player, editor).set(evt.getRightClicked());
        });

        new EventListener<>(PlayerInteractEvent.class, evt -> {
            Action action = evt.getAction();
            Block block = evt.getClickedBlock();
            if (!action.isLeftClick() || block == null) {
                return;
            }
            Player player = evt.getPlayer();
            if (plugin.editingCandidates.get(player) == null) {
                return;
            }
            SpawnerEditor editor = plugin.previousEditors.remove(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            finishEditing(player, editor).set(block);
        });

        new EventListener<>(AsyncChatEvent.class, evt -> {
            Player player = evt.getPlayer();
            SpawnerEditor editor = plugin.previousEditors.get(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            String message = PlainTextComponentSerializer.plainText().serialize(evt.message());
            if (editor.settingName != null && !editor.settingName.isDone()) {
                if (plugin.spawnerObjects.containsKey(message) && !message.equals(editor.getSpawner().name)) {
                    player.sendMessage(GeckoSpawners.makeReadable(Lang.spawnerNameExists));
                    return;
                }
                Task.syncDelayed(() -> editor.settingName.complete(message), 2);
                plugin.previousEditors.remove(player);
                finishEditing(player, editor);
                return;
            }
            if (message.equals("exit")) {
                plugin.previousEditors.remove(player);
                finishEditing(player, editor);
                return;
            }
            Integer numInput = parseInt(message);
            if (numInput == null || numInput <= 0) {
                player.sendMessage(GeckoSpawners.makeReadable(Lang.enterValidInput));
                return;
            }
            plugin.previousEditors.remove(player);
            SpawnCandidate candidate = finishEditing(player, editor).setWeight(numInput);
            editor.getSpawner().remove(candidate).add(candidate);
        });

        new EventListener<>(BlockBreakEvent.class, EventPriority.HIGHEST, evt -> {
            if (evt.isCancelled()) {
                return;
            }
            Block block = evt.getBlock();
            if (block.getType() != Material.SPAWNER) {
                return;
            }
            Player player = evt.getPlayer();
            ItemStack tool = player.getInventory().getItemInMainHand();
            if (!Config.spawnerMiningTools.contains(tool.getType())) {
                return;
            }
            if (!tool.containsEnchantment(Enchantment.SILK_TOUCH) && !player.hasPermission("geckospawners.nosilk")) {
                return;
            }
            evt.setExpToDrop(0);
            ItemStack spawner = new SpawnerObject((CreatureSpawner) block.getState()).getSpawner();
            block.getWorld().dropItemNaturally(block.getLocation(), spawner);
        });

        new EventListener<>(BlockPlaceEvent.class, EventPriority.HIGHEST, evt -> {
            if (evt.isCancelled()) {
                return;
            }
            ItemStack item = evt.getItemInHand();
            if (item.getType() != Material.SPAWNER) {
                return;
            }
            CreatureSpawner spawner = (CreatureSpawner) evt.getBlock().getState();
            NBTTileEntity spawnerNbt = new NBTTileEntity(spawner);
            spawnerNbt.mergeCompound(new NBTItem(item).getOrCreateCompound("BlockEntityTag"));
            Component displayName = item.getItemMeta().displayName();
            spawner = (CreatureSpawner) evt.getBlock().getState();
            if (displayName != null) {
                String serializedName = MiniMessage.get().serialize(displayName);
                spawner = (CreatureSpawner) evt.getBlock().getState();
                spawner.getPersistentDataContainer().set(GeckoSpawners.SPAWNER_NAME_KEY, PersistentDataType.STRING, serializedName);
            }
            if (Config.fixShortDelayOnSpawnerPlace) {
                spawner.setDelay((spawner.getMaxSpawnDelay() + spawner.getMinSpawnDelay())/2);
            }
            spawner.update();
        });
    }

    private SpawnCandidate finishEditing(Player player, SpawnerEditor editor) {
        Task.syncDelayed(editor::open);
        return plugin.editingCandidates.remove(player);
    }

    private Integer parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
