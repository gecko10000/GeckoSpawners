package io.github.gecko10000.GeckoSpawners;

import io.github.gecko10000.GeckoSpawners.guis.SpawnerEditor;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        new EventListener<>(PlayerQuitEvent.class, evt -> {
            Player player = evt.getPlayer();
            plugin.editingCandidates.remove(player);
            plugin.previousEditors.remove(player);
        });

        new EventListener<>(PlayerDropItemEvent.class, evt -> {
            Player player = evt.getPlayer();
            SpawnerEditor editor = plugin.previousEditors.remove(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            plugin.editingCandidates.remove(player).set(evt.getItemDrop().getItemStack());
            plugin.spawnerConfig.save();
            editor.open(player);
        });

        new EventListener<>(EntityDamageByEntityEvent.class, evt -> {
            if (!(evt.getDamager() instanceof Player player)) {
                return;
            }
            SpawnerEditor editor = plugin.previousEditors.remove(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            plugin.editingCandidates.remove(player).set(evt.getEntityType());
            plugin.spawnerConfig.save();
            editor.open(player);
        });

        new EventListener<>(PlayerInteractAtEntityEvent.class, evt -> {
            Player player = evt.getPlayer();
            SpawnerEditor editor = plugin.previousEditors.remove(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            plugin.editingCandidates.remove(player).set(evt.getRightClicked());
            plugin.spawnerConfig.save();
            editor.open(player);
        });

        new EventListener<>(PlayerInteractEvent.class, evt -> {
            Action action = evt.getAction();
            Block block = evt.getClickedBlock();
            if (!action.isLeftClick() || block == null) {
                return;
            }
            Player player = evt.getPlayer();
            SpawnerEditor editor = plugin.previousEditors.remove(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            plugin.editingCandidates.remove(player).set(block);
            plugin.spawnerConfig.save();
            editor.open(player);
        });

        new EventListener<>(AsyncChatEvent.class, evt -> {
            Player player = evt.getPlayer();
            SpawnerEditor editor = plugin.previousEditors.get(player);
            if (editor == null) {
                return;
            }
            evt.setCancelled(true);
            if (PlainTextComponentSerializer.plainText().serialize(evt.message()).equals("exit")) {
                plugin.previousEditors.remove(player);
                plugin.editingCandidates.remove(player);
                Task.syncDelayed(() -> editor.open(player));
            }
        });
    }

}
