package io.github.gecko10000.GeckoSpawners.util;

import redempt.redlib.configmanager.annotations.ConfigValue;

public class Lang {

    @ConfigValue("gui.main.title")
    public static String guiMainTitle = "<dark_purple>Edit Spawners";
    @ConfigValue("gui.spawner.title")
    public static String guiSpawnerTitle = "<dark_purple>Configure spawn candidates";

    @ConfigValue("reload-success")
    public static String reloadSuccess = "<green>Configs reloaded!";
    @ConfigValue("error.hold-something")
    public static String holdSomething = "<red>Hold an item in your hand!";

}
