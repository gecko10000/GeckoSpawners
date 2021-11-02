package io.github.gecko10000.GeckoSpawners.util;

import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.Arrays;
import java.util.List;

public class Config {

    @ConfigValue("entity-removed-values")
    public static List<String> entityRemovedValues = Arrays.asList("UUID", "Rotation", "Pos");

    @ConfigValue("tile-entity-removed-values")
    public static List<String> tileEntityRemovedValues = Arrays.asList(/*"x", "y", "z"*/);

    @ConfigValue("falling-block-time")
    public static int fallingBlockTime = 1;

    @ConfigValue("center-falling-blocks")
    public static boolean centerFallingBlocks = false;

}
