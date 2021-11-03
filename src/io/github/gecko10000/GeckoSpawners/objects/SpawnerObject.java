package io.github.gecko10000.GeckoSpawners.objects;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigPath;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.*;
import java.util.stream.Collectors;

@ConfigMappable
public class SpawnerObject {

    @ConfigPath
    public String id = UUID.randomUUID().toString();

    @ConfigValue
    private Collection<SpawnCandidate> spawnCandidates = ConfigManager.collection(SpawnCandidate.class,
            new PriorityQueue<>(Comparator.comparingInt(SpawnCandidate::getWeight).reversed()));

    public SpawnerObject() {}

    public SpawnerObject add(SpawnCandidate candidate) {
        spawnCandidates.add(candidate);
        return this;
    }

    public SpawnerObject remove(SpawnCandidate candidate) {
        spawnCandidates.remove(candidate);
        return this;
    }

    public Collection<SpawnCandidate> getCandidates() {
        return spawnCandidates;
    }

    public ItemStack getSpawner() {
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        Collection<SpawnCandidate> nonNullSpawnCandidates = spawnCandidates.stream()
                .filter(sc -> sc.asCompound() != null).collect(Collectors.toList());
        if (nonNullSpawnCandidates.isEmpty()) {
            return spawner;
        }
        NBTItem nbtItem = new NBTItem(spawner);
        NBTCompound blockEntityTag = nbtItem.getOrCreateCompound("BlockEntityTag");
        NBTCompound spawnData = blockEntityTag.getOrCreateCompound("SpawnData");
        spawnData.mergeCompound(nonNullSpawnCandidates.iterator().next().asCompound());
        NBTCompoundList spawnPotentials = blockEntityTag.getCompoundList("SpawnPotentials");
        nonNullSpawnCandidates.forEach(c -> spawnPotentials.addCompound(c.asSpawnPotential()));
        nbtItem.applyNBT(spawner);
        return spawner;
    }

    public ItemStack getDisplayItem() {
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawner.getItemMeta();
        meta.displayName(Component.text(this.id)
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromHexString("#08f26e")));
        meta.lore(spawnCandidates.stream()
                .map(sc -> sc.getDisplayItem().getItemMeta().displayName()
                        .append(Component.text(" - " + sc.getWeight()))
                        .color(TextColor.fromHexString("#06a94d")))
                .collect(Collectors.toList()));
        spawner.setItemMeta(meta);
        return spawner;
    }

    public List<ItemStack> getDisplayItems() {
        return spawnCandidates.stream()
                .map(SpawnCandidate::getDisplayItem)
                .collect(Collectors.toList());
    }

}