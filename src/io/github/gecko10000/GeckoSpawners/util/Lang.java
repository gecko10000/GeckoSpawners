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
            "<green>Drop <dark_green>an item to add it.",
            "<green>Left click <dark_green>a mob to add its base type.",
            "<green>Right click <dark_green>a mob to add the exact mob.",
            "<green>Left click <dark_green>a block to add it.",
            "<green>Say <dark_green>\"exit\" to leave edit mode."
    );
    @ConfigValue("error.hold-something")
    public static String holdSomething = "<red>Hold an item in your hand!";

}
