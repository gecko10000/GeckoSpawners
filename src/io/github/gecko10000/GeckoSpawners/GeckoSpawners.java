package io.github.gecko10000.GeckoSpawners;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import io.github.gecko10000.GeckoSpawners.guis.MainEditor;
import io.github.gecko10000.GeckoSpawners.objects.SpawnerObject;
import io.github.gecko10000.GeckoSpawners.util.Config;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.List;

public class GeckoSpawners extends JavaPlugin {

    public ConfigManager spawnerConfig;
    @ConfigValue("spawners")
    public List<SpawnerObject> spawnerObjects = ConfigManager.list(SpawnerObject.class);

    public MainEditor editor;

    public void onEnable() {
        reload();
        new CommandHandler(this);
        new Listeners(this);
        editor = new MainEditor(this);
    }

    public void reload() {
        new ConfigManager(this)
                .register(Config.class).saveDefaults().load();
        new ConfigManager(this, "lang.yml")
                .register(Lang.class).saveDefaults().load();
        spawnerConfig = new ConfigManager(this, "spawners.yml")
                .addConverter(NBTCompound.class, NBTContainer::new, NBTCompound::toString)
                .register(this).saveDefaults().load();
        editor = new MainEditor(this);
    }

    public Component makeReadable(String input) {
        return MiniMessage.get().parse(input);
    }

}
