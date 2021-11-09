package io.github.gecko10000.GeckoSpawners.objects;

import de.tr7zw.nbtapi.*;
import io.github.gecko10000.GeckoSpawners.GeckoSpawners;
import io.github.gecko10000.GeckoSpawners.util.Config;
import io.github.gecko10000.GeckoSpawners.util.Lang;
import io.github.gecko10000.GeckoSpawners.util.ShortWrapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import redempt.redlib.configmanager.ConfigManager;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigPath;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ConfigMappable
public class SpawnerObject {

    @ConfigPath
    public String name = "<color:#08f26e>" + UUID.randomUUID();

    @ConfigValue
    private List<SpawnCandidate> spawnCandidates = ConfigManager.list(SpawnCandidate.class);

    // default of 20 will be used, or average of min and max if set in config
    @ConfigValue
    public ShortWrapper delay = Config.defaultDelay;

    @ConfigValue
    public ShortWrapper minSpawnDelay = Config.defaultMinSpawnDelay;

    @ConfigValue
    public ShortWrapper maxSpawnDelay = Config.defaultMaxSpawnDelay;

    @ConfigValue
    public ShortWrapper spawnRange = Config.defaultSpawnRange;

    @ConfigValue
    public ShortWrapper spawnCount = Config.defaultSpawnCount;

    @ConfigValue
    public ShortWrapper maxNearbyEntities = Config.defaultMaxNearbyEntities;

    @ConfigValue
    public ShortWrapper requiredPlayerRange = Config.defaultRequiredPlayerRange;

    public SpawnerObject() {}

    // for when a spawner is mined, to get the drop
    public SpawnerObject(CreatureSpawner spawner) {
        NBTTileEntity tileNbt = new NBTTileEntity(spawner);
        name = spawner.getPersistentDataContainer().get(GeckoSpawners.SPAWNER_NAME_KEY, PersistentDataType.STRING);
        NBTCompoundList potentials = tileNbt.getCompoundList("SpawnPotentials");
        potentials.stream()
                .map(SpawnCandidate::new)
                .forEach(this::add);
    }

    public SpawnerObject add(SpawnCandidate candidate) {
        spawnCandidates.add(candidate);
        spawnCandidates.sort(Comparator.comparingInt(SpawnCandidate::getWeight).reversed());
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
        blockEntityTag.mergeCompound(shortCompound("Delay", delay));
        blockEntityTag.mergeCompound(shortCompound("MinSpawnDelay", minSpawnDelay));
        blockEntityTag.mergeCompound(shortCompound("MaxSpawnDelay", maxSpawnDelay));
        blockEntityTag.mergeCompound(shortCompound("MaxNearbyEntities", maxNearbyEntities));
        blockEntityTag.mergeCompound(shortCompound("RequiredPlayerRange", requiredPlayerRange));
        blockEntityTag.mergeCompound(shortCompound("SpawnCount", spawnCount));
        blockEntityTag.mergeCompound(shortCompound("SpawnRange", spawnRange));
        NBTCompound spawnData = blockEntityTag.getOrCreateCompound("SpawnData");
        spawnData.mergeCompound(nonNullSpawnCandidates.iterator().next().asCompound());
        NBTCompoundList spawnPotentials = blockEntityTag.getCompoundList("SpawnPotentials");
        nonNullSpawnCandidates.forEach(c -> spawnPotentials.addCompound(c.asSpawnPotential()));
        nbtItem.applyNBT(spawner);
        ItemMeta meta = spawner.getItemMeta();
        meta.displayName(nameComponent());
        meta.lore(loreComponents());
        spawner.setItemMeta(meta);
        return spawner;
    }

    public NBTCompound shortCompound(String key, ShortWrapper value) {
        NBTContainer container = new NBTContainer();
        container.setShort(key, value.value);
        return container;
    }

    public ItemStack getDisplayItem() {
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawner.getItemMeta();
        meta.displayName(nameComponent());
        List<Component> lore = loreComponents();
        lore.add(Component.empty());
        lore.addAll(Lang.spawnerEditInstructions.stream()
                .map(GeckoSpawners::makeReadable)
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toList()));
        meta.lore(lore);
        spawner.setItemMeta(meta);
        return spawner;
    }

    private Component nameComponent() {
        return this.name == null ? null : GeckoSpawners.makeReadable(this.name)
                .decoration(TextDecoration.ITALIC, false);
    }

    private List<Component> loreComponents() {
        return spawnCandidates.stream()
                .map(sc -> GeckoSpawners.makeReadable(Lang.spawnerCandidateFormat)
                        .replaceText(builder -> builder.matchLiteral("%type%")
                                .replacement(sc.getDisplayItem().getItemMeta().displayName().color(null)))
                        .replaceText(builder -> builder.matchLiteral("%weight%")
                                .replacement(Component.text(sc.getWeight() + "").decoration(TextDecoration.ITALIC, false))))
                .collect(Collectors.toList());
    }

}