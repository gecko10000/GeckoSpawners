package io.github.gecko10000.GeckoSpawners;

import io.github.gecko10000.GeckoSpawners.guis.SpawnerEditor;
import io.github.gecko10000.GeckoSpawners.objects.SpawnCandidate;
import io.github.gecko10000.GeckoSpawners.util.Config;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
                if (plugin.spawnerObjects.containsKey(message) && !message.equals(editor.getSpawner().id)) {
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
