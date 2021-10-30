package io.github.gecko10000.GeckoSpawners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

public class GeckoSpawners extends JavaPlugin {

    public void onEnable() {
        reload();
    }

    public void reload() {
        saveDefaultConfig();
        reloadConfig();
    }

    public Component makeReadable(String input) {
        return MiniMessage.get().parse(input);
    }

}
