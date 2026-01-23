package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.dungeon.components.*;
import com.danielkkrafft.wilddungeons.dungeon.components.helpers.LimitedRoomTracker;
import com.danielkkrafft.wilddungeons.dungeon.components.perk.DungeonPerk;
import com.danielkkrafft.wilddungeons.dungeon.components.perk.ExtraLifePerk;
import com.danielkkrafft.wilddungeons.dungeon.components.room.*;
import com.danielkkrafft.wilddungeons.dungeon.components.template.TemplateOrientation;
import com.danielkkrafft.wilddungeons.dungeon.session.DungeonSession;
import com.danielkkrafft.wilddungeons.player.SavedTransform;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import com.danielkkrafft.wilddungeons.render.DecalRenderer;
import com.danielkkrafft.wilddungeons.registry.WDProtectedRegion;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2i;
import sun.reflect.ReflectionFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class Serializer
{
    public static final HashMap<String, Class<?>> ACCEPTABLE_CLASS_REFERENCES = new HashMap<>();
    private static final HashMap<String, List<Field>> CLASS_FIELDS = new HashMap<>();
    private static final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();
    private static final Map<Class<?>, Constructor<?>> CLASS_CONSTRUCTORS = new HashMap<>();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface IgnoreSerialization {
    }

    public static void setup() {
        addCustom(WDPlayer.class);
        addCustom(SavedTransform.class);
        addCustom(ResourceKey.class);
        addCustom(ResourceLocation.class);
        addCustom(Vec3.class);
        addCustom(Vec3i.class);
        addCustom(ConnectionPoint.class);
        addCustom(BlockPos.class);
        addCustom(BoundingBox.class);
        addCustom(DungeonSession.class);
        addCustom(DungeonFloor.class);
        addCustom(DungeonBranch.class);
        addCustom(DungeonRoom.class);
        addCustom(Vector2i.class);
        addCustom(ChunkPos.class);
        addCustom(LootRoom.class);
        addCustom(CombatRoom.class);
        addCustom(SecretRoom.class);
        addCustom(SaveSystem.SaveFile.class);
        addCustom(SaveSystem.DungeonSessionFile.class);
        addCustom(SaveSystem.DungeonFloorFile.class);
        addCustom(SaveSystem.DungeonBranchFile.class);
        addCustom(SaveSystem.DungeonRoomFile.class);
        addCustom(DungeonSession.DungeonStats.class);
        addCustom(DungeonPerk.class);
        addCustom(DungeonTarget.class);
        addCustom(Pair.class);
        addCustom(DungeonSession.DungeonStatsHolder.class);
        addCustom(DungeonSession.DungeonSkinDataHolder.class);
        addCustom(KeyRequiredRoom.class);
        addCustom(BossRoom.class);
        addCustom(DecalRenderer.Decal.class);
        addCustom(DecalRenderer.Decal.Vertex.class);
        addCustom(TemplateOrientation.class);
        addCustom(Property.class);
        addCustom(Mirror.class);
        addCustom(Rotation.class);
        addCustom(LootChoiceRoom.class);
        addCustom(ExtraLifePerk.class);
        addCustom(WeaponGauntletKeyRoom.class);
        addCustom(LimitedRoomTracker.class);
        addCustom(LimitedRoomTracker.RoomContainer.class);
        addCustom(WDProtectedRegion.class);
        addCustom(WDProtectedRegion.RegionPermission.class);
    }

    private static void addCustom(Class<?> clazz) {
        try {
            CLASS_CONSTRUCTORS.put(clazz, REFLECTION_FACTORY.newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor()));
            while (clazz != null) {
                List<Field> fields = new ArrayList<>();
                ACCEPTABLE_CLASS_REFERENCES.put(clazz.getName(), clazz);
                for (Field field : clazz.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    if (field.isAnnotationPresent(IgnoreSerialization.class)) continue;
                    field.setAccessible(true);
                    fields.add(field);
                }
                CLASS_FIELDS.put(clazz.getName(), fields);
                if (clazz.isEnum()) break;
                clazz = clazz.getSuperclass();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static CompoundTag toCompoundTag(Object obj)
    {
        CompoundTag result = new CompoundTag();

        try
        {
            serializeAndAdd("INTERNAL_ROOT", obj, result);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> T fromCompoundTag(CompoundTag tag)
    {
        try
        {
            //noinspection unchecked
            return (T) deserialize("INTERNAL_ROOT", tag);
        }
        catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static int serializations = 0;
    private static void serializeAndAdd(String key, Object value, CompoundTag tag) throws IllegalAccessException {
        serializations++;

        CompoundTag entry = new CompoundTag();
        if (value instanceof Integer intValue)
        {
            entry.putString("type", "int");
            entry.putInt("value", intValue);
        }

        else if (value instanceof String stringValue)
        {
            entry.putString("type", "string");
            entry.putString("value", stringValue);
        }

        else if (value instanceof Double doubleValue)
        {
            entry.putString("type", "double");
            entry.putDouble("value", doubleValue);
        }

        else if (value instanceof Float floatValue)
        {
            entry.putString("type", "float");
            entry.putFloat("value", floatValue);
        }

        else if (value instanceof Long longValue)
        {
            entry.putString("type", "long");
            entry.putLong("value", longValue);
        }

        else if (value instanceof Boolean booleanValue)
        {
            entry.putString("type", "boolean");
            entry.putBoolean("value", booleanValue);
        }

        else if (value instanceof HashMap<?, ?> hashMapValue)
        {
            entry.putString("type", "hashmap");
            CompoundTag nestedTag = new CompoundTag();
            int index = 0;
            for (Map.Entry<?, ?> hashEntry : hashMapValue.entrySet()) {
                CompoundTag keyValueTag = new CompoundTag();
                serializeAndAdd("k", hashEntry.getKey(), keyValueTag);
                serializeAndAdd("v", hashEntry.getValue(), keyValueTag);
                nestedTag.put(""+index++, keyValueTag);
            }
            entry.put("value", nestedTag);
        }

        else if (value instanceof ArrayList<?> arrayListValue)
        {
            entry.putString("type", "arrayList");
            CompoundTag nestedTag = new CompoundTag();
            int index = 0;
            for (Object object : arrayListValue.toArray()) {
                serializeAndAdd(""+index++, object, nestedTag);
            }
            entry.put("value", nestedTag);
        }

        else if (value instanceof List<?> listValue)
        {
            entry.putString("type", "list");
            CompoundTag nestedTag = new CompoundTag();
            int index = 0;
            for (Object object : listValue.toArray()) {
                serializeAndAdd(""+index++, object, nestedTag);
            }
            entry.put("value", nestedTag);
        }

        else if (value instanceof HashSet<?> hashSetValue)
        {
            entry.putString("type", "hashSet");
            CompoundTag nestedTag = new CompoundTag();
            int index = 0;
            for (Object object : hashSetValue.toArray()) {
                serializeAndAdd(""+index++, object, nestedTag);
            }
            entry.put("value", nestedTag);
        }

        else if (value instanceof EnumSet<?> enumSetValue) {
            entry.putString("type", "enumSet");
            CompoundTag nestedTag = new CompoundTag();
            int index = 0;
            Class<?> elementType = null;
            if (!enumSetValue.isEmpty()) {
                elementType = enumSetValue.iterator().next().getClass();
            } else {
                try {
                    Field elementTypeField = EnumSet.class.getDeclaredField("elementType");
                    elementTypeField.setAccessible(true);
                    elementType = (Class<?>) elementTypeField.get(enumSetValue);
                } catch (Exception e) {
                    WildDungeons.getLogger().error("EnumSet element type is cooked ", e);
                }
            }

            if (elementType != null) entry.putString("enumClass", elementType.getName());

            for (Object object : enumSetValue.toArray()) {
                serializeAndAdd(""+index++, object, nestedTag);
            }
            entry.put("value", nestedTag);
        }

        else if (value instanceof Enum<?> enumValue)
        {
            entry.putString("type", "enum");
            entry.putString("value", enumValue.name());
            entry.putString("class", enumValue.getClass().getName());
        }

        else if (value instanceof ResourceKey<?> resourceKeyValue)
        {
            entry.putString("type", "resourceKey");
            CompoundTag nestedTag = new CompoundTag();
            nestedTag.putString("registry", resourceKeyValue.registry().toString());
            nestedTag.putString("location", resourceKeyValue.location().toString());
            entry.put("value", nestedTag);
        }

        else if (value instanceof Object objectValue)
        {
            entry.putString("type", "custom");
            CompoundTag nestedTag = new CompoundTag();

            Class<?> clazz = ACCEPTABLE_CLASS_REFERENCES.get(objectValue.getClass().getName());
            if (clazz == null) {
                WildDungeons.getLogger().info("THE CLASS: {} WAS NOT SETUP FOR SERIALIZATION", objectValue.getClass().getName());
                return;
            }
            String className = clazz.getName();

            while (clazz != null && CLASS_FIELDS.containsKey(clazz.getName())) {
                for (Field field : CLASS_FIELDS.get(clazz.getName())) {
                    Object fieldValue = field.get(objectValue);
                    serializeAndAdd(field.getName(), fieldValue, nestedTag);
                }
                clazz = clazz.getSuperclass();
            }

            entry.put("value", nestedTag);
            entry.putString("class", className);
        }



        tag.put(key, entry);
    }

    private static Object deserialize(String key, CompoundTag tag) throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        CompoundTag entry = tag.getCompound(key);
        String type = entry.getString("type");

        switch (type) {
            case "int" -> {
                return entry.getInt("value");
            }
            case "string" -> {
                return entry.getString("value");
            }
            case "double" -> {
                return entry.getDouble("value");
            }
            case "float" -> {
                return entry.getFloat("value");
            }
            case "long" -> {
                return entry.getLong("value");
            }
            case "boolean" -> {
                return entry.getBoolean("value");
            }
            case "hashmap" -> {
                CompoundTag nestedTag = entry.getCompound("value");

                HashMap<Object, Object> hashMapValue = new HashMap<>();
                for (String hashMapEntryIndex : nestedTag.getAllKeys()) {
                    CompoundTag keyValueTag = nestedTag.getCompound(hashMapEntryIndex);
                    hashMapValue.put(deserialize("k", keyValueTag), deserialize("v", keyValueTag));
                }

                return hashMapValue;
            }
            case "hashSet" -> {
                CompoundTag nestedTag = entry.getCompound("value");

                HashSet<Object> hashSetValue = new HashSet<>();
                for (String hashSetEntryValue : nestedTag.getAllKeys()) {
                    hashSetValue.add(deserialize(hashSetEntryValue, nestedTag));
                }

                return hashSetValue;
            }

            case "enumSet" -> {
                CompoundTag nestedTag = entry.getCompound("value");
                String enumClassName = entry.getString("enumClass");

                Class<?> enumClass = ACCEPTABLE_CLASS_REFERENCES.get(enumClassName);
                if (enumClass == null) return null;

                Class<? extends Enum> enumType = (Class<? extends Enum>) enumClass;
                EnumSet enumSetValue = EnumSet.noneOf(enumType);

                for (int i = 0; i < nestedTag.getAllKeys().size(); i++) {
                    Enum enumVal = (Enum) deserialize(String.valueOf(i), nestedTag);
                    if (enumVal != null) {
                        boolean added = enumSetValue.add(enumVal);
                    }
                }

                return enumSetValue;
            }

            case "arrayList" -> {
                CompoundTag nestedTag = entry.getCompound("value");

                ArrayList<Object> arrayListValue = new ArrayList<>();
                for (int i = 0; i < nestedTag.getAllKeys().size(); i++) {
                    arrayListValue.add(deserialize(String.valueOf(i), nestedTag));
                }

                return arrayListValue;
            }
            case "list" -> {
                CompoundTag nestedTag = entry.getCompound("value");

                List<Object> listValue = new ArrayList<>();
                for (int i = 0; i < nestedTag.getAllKeys().size(); i++) {
                    listValue.add(deserialize(String.valueOf(i), nestedTag));
                }

                return listValue;
            }
            case "enum" -> {
                Class<?> enumClass = ACCEPTABLE_CLASS_REFERENCES.get(entry.getString("class"));
                if (enumClass == null) {
                    WildDungeons.getLogger().info("THE CLASS: {} WAS NOT SETUP FOR SERIALIZATION", entry.getString("class"));
                    return null;
                }

                return Enum.valueOf((Class<Enum>) enumClass, entry.getString("value"));
            }
            case "resourceKey" -> {
                CompoundTag nestedTag = entry.getCompound("value");
                ResourceLocation registry = ResourceLocation.parse(nestedTag.getString("registry"));
                ResourceLocation location = ResourceLocation.parse(nestedTag.getString("location"));

                ResourceKey<?> resourceKey = ResourceKey.create(registry, location);
                return resourceKey;
            }
            case "custom" -> {
                CompoundTag nestedTag = entry.getCompound("value");

                Class<?> clazz = ACCEPTABLE_CLASS_REFERENCES.get(entry.getString("class"));
                if (clazz == null) {
                    WildDungeons.getLogger().info("THE CLASS: {} WAS NOT SETUP FOR SERIALIZATION", entry.getString("class"));
                    return null;
                }

                Object instance = clazz.cast(CLASS_CONSTRUCTORS.get(clazz).newInstance());
                while (clazz != null) {
                    for (Field field : CLASS_FIELDS.get(clazz.getName())) {
                        field.set(instance, deserialize(field.getName(), nestedTag));
                    }
                    clazz = clazz.getSuperclass();
                }

                return instance;
            }
        }

        return null;
    }
}
