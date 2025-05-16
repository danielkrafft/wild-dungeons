package com.danielkkrafft.wilddungeons.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class InstantLoadout extends Item {
    private final Type type;
    public InstantLoadout(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            if (player.getInventory().contains(itemStack)) {
                player.getInventory().removeItem(itemStack);
            }
            switch (type){
                case Leather -> {
                    player.addItem(Items.STONE_SWORD.getDefaultInstance());
                    player.addItem(Items.STONE_PICKAXE.getDefaultInstance());
                    player.addItem(Items.STONE_AXE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.HEAD, Items.LEATHER_HELMET.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.CHEST, Items.LEATHER_CHESTPLATE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.LEGS, Items.LEATHER_LEGGINGS.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.FEET, Items.LEATHER_BOOTS.getDefaultInstance());
                }
                case Iron -> {
                    player.addItem(Items.IRON_SWORD.getDefaultInstance());
                    player.addItem(Items.IRON_PICKAXE.getDefaultInstance());
                    player.addItem(Items.IRON_AXE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.HEAD, Items.IRON_HELMET.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.CHEST, Items.IRON_CHESTPLATE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.LEGS, Items.IRON_LEGGINGS.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.FEET, Items.IRON_BOOTS.getDefaultInstance());
                }
                case Diamond -> {
                    player.addItem(Items.DIAMOND_SWORD.getDefaultInstance());
                    player.addItem(Items.DIAMOND_PICKAXE.getDefaultInstance());
                    player.addItem(Items.DIAMOND_AXE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.HEAD, Items.DIAMOND_HELMET.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.CHEST, Items.DIAMOND_CHESTPLATE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.LEGS, Items.DIAMOND_LEGGINGS.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.FEET, Items.DIAMOND_BOOTS.getDefaultInstance());
                }
                case Netherite -> {
                    player.addItem(Items.NETHERITE_SWORD.getDefaultInstance());
                    player.addItem(Items.NETHERITE_PICKAXE.getDefaultInstance());
                    player.addItem(Items.NETHERITE_AXE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.HEAD, Items.NETHERITE_HELMET.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.CHEST, Items.NETHERITE_CHESTPLATE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.LEGS, Items.NETHERITE_LEGGINGS.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.FEET, Items.NETHERITE_BOOTS.getDefaultInstance());
                }
                case Gold -> {
                    player.addItem(Items.GOLDEN_SWORD.getDefaultInstance());
                    player.addItem(Items.GOLDEN_PICKAXE.getDefaultInstance());
                    player.addItem(Items.GOLDEN_AXE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.HEAD, Items.GOLDEN_HELMET.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.CHEST, Items.GOLDEN_CHESTPLATE.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.LEGS, Items.GOLDEN_LEGGINGS.getDefaultInstance());
                    player.setItemSlot(EquipmentSlot.FEET, Items.GOLDEN_BOOTS.getDefaultInstance());
                }
            }
            player.addItem(new ItemStack(Items.TORCH, 32));
            player.addItem(new ItemStack(Items.COOKED_BEEF, 16));
        }
        return InteractionResultHolder.consume(itemStack);
    }

    public enum Type {
        Leather,
        Iron,
        Diamond,
        Netherite,
        Gold,
    }
}
