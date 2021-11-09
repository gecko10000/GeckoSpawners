package io.github.gecko10000.GeckoSpawners.util;

import org.bukkit.Material;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Config {

    @ConfigValue("entity-removed-values")
    public static List<String> entityRemovedValues = Arrays.asList("UUID", "Rotation", "Pos");

    @ConfigValue("tile-entity-removed-values")
    public static List<String> tileEntityRemovedValues = Arrays.asList("x", "y", "z");

    @ConfigValue("falling-block-time")
    public static int fallingBlockTime = 1;

    @ConfigValue("center-falling-blocks")
    public static boolean centerFallingBlocks = false;

    @ConfigValue("default-falling-blocks-dont-drop-items")
    public static boolean defaultFallingBlocksDontDropItems = false;

    @ConfigValue("default-data.Delay")
    public static ShortWrapper defaultDelay = new ShortWrapper(20);

    @ConfigValue("default-data.MinSpawnDelay")
    public static ShortWrapper defaultMinSpawnDelay = new ShortWrapper(200);

    @ConfigValue("default-data.MaxSpawnDelay")
    public static ShortWrapper defaultMaxSpawnDelay = new ShortWrapper(800);

    @ConfigValue("default-data.MaxNearbyEntities")
    public static ShortWrapper defaultMaxNearbyEntities = new ShortWrapper(6);

    @ConfigValue("default-data.SpawnRange")
    public static ShortWrapper defaultSpawnRange = new ShortWrapper(4);

    @ConfigValue("default-data.SpawnCount")
    public static ShortWrapper defaultSpawnCount = new ShortWrapper(4);

    @ConfigValue("default-data.RequiredPlayerRange")
    public static ShortWrapper defaultRequiredPlayerRange = new ShortWrapper(16);

    @ConfigValue("spawner-mining-tools")
    public static Set<Material> spawnerMiningTools = ConfigManager.set(Material.class,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE);

}
