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

    @ConfigValue("fix-short-delay-on-spawner-place")
    public static boolean fixShortDelayOnSpawnerPlace = false;

    @ConfigValue("spawner-mining-tools")
    public static Set<Material> spawnerMiningTools = ConfigManager.set(Material.class,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE);

}
