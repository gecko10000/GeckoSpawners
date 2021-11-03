package io.github.gecko10000.GeckoSpawners.objects;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTTileEntity;
import io.github.gecko10000.GeckoSpawners.util.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import redempt.redlib.configmanager.annotations.ConfigMappable;
import redempt.redlib.configmanager.annotations.ConfigValue;
import redempt.redlib.misc.FormatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (Config.defaultFallingBlocksDontDropItems) {
            entityNBT.setByte("DropItem", (byte) 0);
        }
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

    public ItemStack getDisplayItem() {
        ItemStack displayCopy = displayItem.clone();
        ItemMeta displayMeta = displayCopy.getItemMeta();
        Component displayName;
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Weight: " + weight)
                .color(TextColor.fromHexString("#059142"))
                .decoration(TextDecoration.ITALIC, false));
        if (entityNBT == null) {
            displayName = Component.text("None")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(TextColor.fromHexString("#08f26e"));
        } else {
            String name = getKey(entityNBT.getString("id"));
            name = FormatUtils.toTitleCase(name.replace('_', ' '));
            displayName = Component.text(name)
                    .decoration(TextDecoration.ITALIC, false)
                    .color(TextColor.fromHexString("#08f26e"));
            switch (name) {
                case "Item" -> displayName = displayName.append(Component.text(": "))
                        .append(Optional.ofNullable(displayMeta.displayName())
                                .orElse(Component.translatable(displayCopy.translationKey()))
                        );
                case "Falling Block" -> {
                    String blockName = getKey(entityNBT.getCompound("BlockState")
                            .getString("Name"));
                    blockName = FormatUtils.toTitleCase(blockName.replace('_', ' '));
                    displayName = displayName.append(Component.text(": "))
                            .append(Component.text(blockName));
                }
            }
            if (Config.showNbtInCandidateLore) {
                lore.addAll(
                        Arrays.stream(entityNBT.toString().split("(?<=\\G.{20})"))
                                .map(Component::text)
                                .map(c -> c.color(TextColor.fromHexString("#059142")))
                                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                                .collect(Collectors.toList()));
            }
        }
        displayMeta.displayName(displayName);
        displayMeta.lore(lore);
        displayCopy.setItemMeta(displayMeta);
        return displayCopy;
    }

    private String getKey(String namespaced) {
        String[] split = namespaced.split(":");
        return split[split.length - 1];
    }

    public NBTCompound asCompound() {
        return entityNBT;
    }

    public NBTCompound asSpawnPotential() {
        if (entityNBT == null) {
            return null;
        }
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
