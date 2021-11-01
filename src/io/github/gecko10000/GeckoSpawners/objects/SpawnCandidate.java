package io.github.gecko10000.GeckoSpawners.objects;

import de.tr7zw.nbtapi.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.Optional;

@ConfigMappable
public class SpawnCandidate {

    @ConfigValue
    private int weight = 0;

    @ConfigValue
    private ItemStack displayItem = new ItemStack(Material.GLASS);

    @ConfigValue
    private NBTCompound entityNBT;

    public SpawnCandidate() {}

    public SpawnCandidate setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public int getWeight() {
        return this.weight;
    }

    public SpawnCandidate set(ItemStack item) {
        entityNBT = new NBTItem(item);
        displayItem = item;
        return this;
    }

    public SpawnCandidate set(EntityType type) {
        entityNBT = new NBTContainer();
        entityNBT.setString("id", type.getKey().toString());
        displayItem = spawnEgg(type);
        return this;
    }

    public SpawnCandidate set(Entity entity) {
        entityNBT = new NBTEntity(entity);
        displayItem = spawnEgg(entity.getType());
        return this;
    }

    public SpawnCandidate set(BlockState state) {
        entityNBT = new NBTTileEntity(state);
        displayItem = new ItemStack(state.getType());
        return this;
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public NBTCompound asCompound() {
        return entityNBT;
    }

    public NBTCompound asSpawnPotential() {
        NBTCompound potential = new NBTContainer();
        potential.setInteger("Weight", weight);
        Bukkit.broadcast(Component.text(entityNBT.toString()));
        potential.getOrCreateCompound("Entity").mergeCompound(entityNBT);
        Bukkit.broadcast(Component.text(potential.toString()));
        return potential;
    }

    private ItemStack spawnEgg(EntityType type) {
        return Optional.ofNullable(Bukkit.getItemFactory().getSpawnEgg(type))
                .orElse(new ItemStack(Material.EGG));
    }

}
