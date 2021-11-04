package io.github.gecko10000.GeckoSpawners;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import io.github.gecko10000.GeckoSpawners.guis.MainEditor;
import io.github.gecko10000.GeckoSpawners.guis.SpawnerEditor;
import io.github.gecko10000.GeckoSpawners.objects.SpawnCandidate;
import io.github.gecko10000.GeckoSpawners.objects.SpawnerObject;
import io.github.gecko10000.GeckoSpawners.util.Config;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeckoSpawners extends JavaPlugin {

    public ConfigManager spawnerConfig;
    @ConfigValue("spawners")
    public Map<String, SpawnerObject> spawnerObjects = ConfigManager.map(String.class, SpawnerObject.class);

    public Map<Player, SpawnCandidate> editingCandidates = new HashMap<>();
    public Map<Player, SpawnerEditor> previousEditors = new HashMap<>();

    public MainEditor editor;

    public static final NamespacedKey SPAWNER_NAME_KEY = NamespacedKey.fromString("geckospawners:name");

    public void onEnable() {
        reload();
        new CommandHandler(this);
        new Listeners(this);
        editor = new MainEditor(this);
    }

    public void onDisable() {
        spawnerConfig.save();
    }

    public void reload() {
        new ConfigManager(this)
                .register(Config.class).saveDefaults().load();
        new ConfigManager(this, "lang.yml")
                .register(Lang.class).saveDefaults().load();
        spawnerConfig = new ConfigManager(this, "spawners.yml")
                .addConverter(NBTCompound.class, NBTContainer::new, NBTCompound::toString)
                .addConverter(Material.class, Material::getMaterial, Material::toString)
                .register(this).saveDefaults().load();
        editor = new MainEditor(this);
    }

    public ItemStack pageItem(boolean prev) {
        ItemStack item = new ItemStack(prev ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.translatable("createWorld.customize.custom." + (prev ? "prev" : "next"))
                .decoration(TextDecoration.ITALIC, false)
                .color(prev ? NamedTextColor.RED : NamedTextColor.GREEN));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack backItem() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.translatable("gui.back")
                .decoration(TextDecoration.ITALIC, false)
                .color(NamedTextColor.RED));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack makeItem(Material material, String name) {
        return makeItem(material, name, new ArrayList<>());
    }

    public ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(makeReadable(name).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore.stream()
                .map(GeckoSpawners::makeReadable)
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toList())
        );
        item.setItemMeta(meta);
        return item;
    }

    public static List<Component> makeReadable(List<String> input) {
        return input.stream().map(GeckoSpawners::makeReadable).collect(Collectors.toList());
    }

    public static Component makeReadable(String input) {
        return MiniMessage.get().parse(input);
    }

}
