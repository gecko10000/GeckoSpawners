package io.github.gecko10000.GeckoSpawners.util;

import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.Arrays;
import java.util.List;

public class Lang {

    @ConfigValue("gui.main.title")
    public static String guiMainTitle = "<#059142>Edit Spawners";
    @ConfigValue("gui.main.create")
    public static String guiMainCreate = "<#059142>Create New Spawner";
    @ConfigValue("gui.main.edit-instructions")
    public static List<String> spawnerEditInstructions = Arrays.asList(
            "<red>Click to edit",
            "<red>Middle click to get",
            "<red>Shift+click to delete"
    );

    @ConfigValue("gui.spawner.edit-instructions")
    public static List<String> candidateEditInstructions = Arrays.asList(
            "<red>Click to edit",
            "<red>Shift+click to delete"
    );
    @ConfigValue("gui.spawner.title")
    public static String guiSpawnerTitle = "<#059142>%name%";
    @ConfigValue("gui.spawner.rename")
    public static String renameSpawner = "<#059142>Rename Spawner";
    @ConfigValue("gui.spawner.create")
    public static String guiSpawnerCreateCandidate = "<#059142>Create Spawn Candidate";

    @ConfigValue("gui.spawner.data-button-name")
    public static String guiSpawnerDataButton = "<#77dd77><underlined>%amount%</underlined> <black>| <#059142>%data%";
    @ConfigValue("gui.spawner.data-button-lore")
    public static List<String> guiSpawnerDataButtonLore = Arrays.asList(
            "",
            "<green>+1 <black>|<#06a94d> Left click",
            "<green>+10 <black>|<#06a94d> Shift+left click",
            "<red>-1 <black>|<#06a94d> Right click",
            "<red>-10 <black>|<#06a94d> Shift+right click"
    );

    @ConfigValue("reload-success")
    public static String reloadSuccess = "<green>Configs reloaded!";

    @ConfigValue("enter-edit-mode")
    public static List<String> enterEditMode = Arrays.asList(
            "",
            "<green>Drop an item <dark_green>to set the candidate to it.",
            "<green>Left click a mob <dark_green>to set the candidate to its base type.",
            "<green>Right click a mob <dark_green>to set the candidate to the exact mob.",
            "<green>Left click a block <dark_green>to set the candidate to it.",
            "<green>Enter a number <dark_green>to set the candidate's weight.",
            "<green>Say \"exit\" <dark_green>to leave edit mode."
    );

    @ConfigValue("enter-naming-mode")
    public static String enterNamingMode = "<green>Enter a new name for %name%:";

    @ConfigValue("error.hold-something")
    public static String holdSomething = "<red>Hold an item in your hand!";
    @ConfigValue("error.enter-valid-input")
    public static String enterValidInput = "<red>Please enter \"exit\" or a number.";
    @ConfigValue("error.spawner-name-exists")
    public static String spawnerNameExists = "<red>A spawner with that name already exists.";

    @ConfigValue("spawner-candidate-format")
    public static String spawnerCandidateFormat = "<#06a94d>%type% - %weight%";

}
