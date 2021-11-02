package io.github.gecko10000.GeckoSpawners.objects;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.*;
import java.util.stream.Collectors;

@ConfigMappable
public class SpawnerObject {

    @ConfigValue
    private Collection<SpawnCandidate> spawnCandidates = ConfigManager.collection(SpawnCandidate.class,
            new PriorityQueue<>(Comparator.comparingInt(SpawnCandidate::getWeight).reversed()));

    public SpawnerObject() {}

    public SpawnerObject add(SpawnCandidate candidate) {
        spawnCandidates.add(candidate);
        return this;
    }

    public Collection<SpawnCandidate> getCandidates() {
        return spawnCandidates;
    }

    public ItemStack getSpawner() {
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        if (spawnCandidates.isEmpty()) {
            return spawner;
        }
        NBTItem nbtItem = new NBTItem(spawner);
        NBTCompound blockEntityTag = nbtItem.getOrCreateCompound("BlockEntityTag");
        NBTCompound spawnData = blockEntityTag.getOrCreateCompound("SpawnData");
        spawnData.mergeCompound(spawnCandidates.iterator().next().asCompound());
        NBTCompoundList spawnPotentials = blockEntityTag.getCompoundList("SpawnPotentials");
        spawnCandidates.forEach(c -> spawnPotentials.addCompound(c.asSpawnPotential()));
        nbtItem.applyNBT(spawner);
        return spawner;
    }

    public List<ItemStack> getDisplayItems() {
        return spawnCandidates.stream()
                .map(SpawnCandidate::getDisplayItem)
                .collect(Collectors.toList());
    }

}