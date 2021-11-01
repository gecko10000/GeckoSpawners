package io.github.gecko10000.GeckoSpawners;

import io.github.gecko10000.GeckoSpawners.guis.MainEditor;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;

import java.util.stream.Stream;

public class CommandHandler {

    private final GeckoSpawners plugin;

    public CommandHandler(GeckoSpawners plugin) {
        this.plugin = plugin;
        ArgType<EntityType> entityTypeArg = new ArgType<>("entityType", s -> {
            try {
                return EntityType.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).tabStream(c -> Stream.of(EntityType.values())
                .map(EntityType::toString)
                .map(String::toLowerCase));
        new CommandParser(plugin.getResource("command.rdcml"))
                .setArgTypes(entityTypeArg)
                .parse().register("geckospawners", this);
    }

    @CommandHook("reload")
    public void reload(CommandSender sender) {
        plugin.reload();
        response(sender, Lang.reloadSuccess);
    }

    @CommandHook("edit")
    public void edit(Player player) {
        plugin.editor.open(player);
    }

    private void response(CommandSender sender, String message) {
        sender.sendMessage(plugin.makeReadable(message));
    }

}