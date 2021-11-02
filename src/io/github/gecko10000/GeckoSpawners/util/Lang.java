package io.github.gecko10000.GeckoSpawners.util;

import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.Arrays;
import java.util.List;

public class Lang {

    @ConfigValue("gui.main.title")
    public static String guiMainTitle = "<dark_purple>Edit Spawners";
    @ConfigValue("gui.spawner.title")
    public static String guiSpawnerTitle = "<dark_purple>Configure spawn candidates";
    @ConfigValue("gui.spawner.back")
    public static String back = "<red>Back";
    @ConfigValue("gui.page.prev")
    public static String prevPage = "<red>Previous Page";
    @ConfigValue("gui.page.next")
    public static String nextPage = "<green>Next Page";

    @ConfigValue("reload-success")
    public static String reloadSuccess = "<green>Configs reloaded!";

    @ConfigValue("enter-edit-mode")
    public static List<String> enterEditMode = Arrays.asList(
            "<green>Drop an item <dark_green>to set the option to it.",
            "<green>Left click a mob <dark_green>to set the option to its base type.",
            "<green>Right click a mob <dark_green>to set the option to the exact mob.",
            "<green>Left click a block <dark_green>to set the option to it.",
            "<green>Enter a number <dark_green>to set the option's weight.",
            "<green>Say \"exit\" <dark_green>to leave edit mode."
    );

    @ConfigValue("error.hold-something")
    public static String holdSomething = "<red>Hold an item in your hand!";
    @ConfigValue("error.enter-valid-input")
    public static String enterValidInput = "<red>Please enter \"exit\" or a number.";

}
