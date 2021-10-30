package io.github.gecko10000.GeckoSpawners;

import org.bukkit.command.CommandSender;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;

public class CommandHandler {

    private final GeckoSpawners plugin;

    public CommandHandler(GeckoSpawners plugin) {
        this.plugin = plugin;
        new CommandParser(plugin.getResource("command.rdcml"))
                .parse().register("geckospawners", this);
    }

    @CommandHook("reload")
    public void reload(CommandSender sender) {
        plugin.reload();
        response(sender, Lang.reloadSuccess);
    }

    private void response(CommandSender sender, String message) {
        sender.sendMessage(plugin.makeReadable(message));
    }

}
