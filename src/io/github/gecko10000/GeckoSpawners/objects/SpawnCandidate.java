package io.github.gecko10000.GeckoSpawners.objects;

import de.tr7zw.nbtapi.*;
import io.github.gecko10000.GeckoSpawners.util.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ConfigMappable
public class SpawnCandidate {

    @ConfigValue
    private int weight = 1;

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
        NBTContainer container = new NBTContainer();
        container.setString("id", "minecraft:item");
        container.setItemStack("Item", item);
        entityNBT = container;
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
        entityNBT = new NBTContainer();
        entityNBT.setString("id", entity.getType().getKey().toString());
        entityNBT.mergeCompound(new NBTEntity(entity));
        Config.entityRemovedValues.forEach(entityNBT::removeKey);
        displayItem = spawnEgg(entity.getType());
        return this;
    }

    public SpawnCandidate set(Block block) {
        Material type = block.getType();
        entityNBT = new NBTContainer();
        entityNBT.setString("id", "minecraft:falling_block");
        entityNBT.setInteger("Time", Config.fallingBlockTime);
        NBTCompound blockState = entityNBT.getOrCreateCompound("BlockState");
        blockState.setString("Name", type.getKey().toString());
        String blockData = block.getBlockData().getAsString();
        int low = blockData.indexOf('[') + 1;
        int high = blockData.lastIndexOf(']');
        // only set blockdata if it exists
        if (low < high) {
            NBTCompound properties = blockState.getOrCreateCompound("Properties");
            blockData = blockData.substring(low, high);
            Arrays.stream(blockData.split(","))
                    .map(s -> s.split("="))
                    .forEach(s -> properties.setString(s[0], s[1]));
        }

        // only set tile entity properties for actual tile entities
        if (!block.getState().getClass().getSimpleName().equals("CraftBlockState")) {
            NBTCompound tileEntityData = entityNBT.getOrCreateCompound("TileEntityData");
            tileEntityData.mergeCompound(new NBTTileEntity(block.getState()));
            Config.tileEntityRemovedValues.forEach(tileEntityData::removeKey);
        }
        displayItem = new ItemStack(type.isItem() ? type : Material.WHITE_STAINED_GLASS);
        return this;
    }

    /*
{
	BlockState:{
		Name:"minecraft:shulker_box",
		Properties:{facing:east}
	},
	Time:1,
	TileEntityData:{
		Items:[
		{Count:1b,Slot:0b,id:grass_block}
		]
	}
}
     */

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public NBTCompound asCompound() {
        return entityNBT;
    }

    public NBTCompound asSpawnPotential() {
        NBTCompound potential = new NBTContainer();
        potential.setInteger("Weight", weight);
        potential.getOrCreateCompound("Entity").mergeCompound(entityNBT);
        return potential;
    }

    private ItemStack spawnEgg(EntityType type) {
        return Optional.ofNullable(Bukkit.getItemFactory().getSpawnEgg(type))
                .orElse(new ItemStack(Material.EGG));
    }

}
