package com.danielkkrafft.wilddungeons.util;

import com.danielkkrafft.wilddungeons.WildDungeons;
import com.danielkkrafft.wilddungeons.player.SavedTransform;
import com.danielkkrafft.wilddungeons.player.WDPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import sun.reflect.ReflectionFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Serializer
{
    public static final HashMap<String, Class<?>> ACCEPTABLE_CLASS_REFERENCES = new HashMap<>();

    public static void setup() {
        addCustom(WDPlayer.class);
        addCustom(SavedTransform.class);
        addCustom(ResourceKey.class);
        addCustom(ResourceLocation.class);
        addCustom(Vec3.class);
    }

    private static void addCustom(Class<?> clazz) {
        ACCEPTABLE_CLASS_REFERENCES.put(clazz.getName(), clazz);
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
            return (T) deserialize("INTERNAL_ROOT", tag);
        }
        catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static void serializeAndAdd(String key, Object value, CompoundTag tag) throws IllegalAccessException {
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

        else if (value instanceof Long longValue)
        {
            entry.putString("type", "long");
            entry.putLong("value", longValue);
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
        else if (value instanceof Object objectValue)
        {
            entry.putString("type", "custom");

            Class<?> clazz = objectValue.getClass();
            CompoundTag nestedTag = new CompoundTag();
            for (Field field : clazz.getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                Object fieldValue = field.get(objectValue);
                serializeAndAdd(field.getName(), fieldValue, nestedTag);
            }
            entry.put("value", nestedTag);
            entry.putString("class", clazz.getName());
        }

        tag.put(key, entry);
    }

    private static Object deserialize(String key, CompoundTag tag) throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        CompoundTag entry = tag.getCompound(key);
        String type = entry.getString("type");

        if (type.equals("int"))
        {
            return entry.getInt("value");
        }

        else if (type.equals("string"))
        {
            return entry.getString("value");
        }

        else if (type.equals("double"))
        {
            return entry.getDouble("value");
        }

        else if (type.equals("long"))
        {
            return entry.getLong("value");
        }

        else if (type.equals("hashmap"))
        {
            CompoundTag nestedTag = entry.getCompound("value");

            HashMap<Object, Object> hashMapValue = new HashMap<>();
            for (String hashMapEntryIndex : nestedTag.getAllKeys()) {
                CompoundTag keyValueTag = nestedTag.getCompound(hashMapEntryIndex);
                hashMapValue.put(deserialize("k", keyValueTag), deserialize("v", keyValueTag));
            }

            return hashMapValue;
        }

        else if (type.equals("custom"))
        {
            CompoundTag nestedTag = entry.getCompound("value");

            Class<?> clazz = ACCEPTABLE_CLASS_REFERENCES.get(entry.getString("class"));
            if (clazz == null) {
                WildDungeons.getLogger().info("THE CLASS: {} WAS NOT SETUP FOR SERIALIZATION", entry.getString("class"));
                return null;
            }
            ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
            Object instance = clazz.cast(rf.newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor()).newInstance());
            for (Field field : clazz.getDeclaredFields())
            {
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                field.set(instance, deserialize(field.getName(), nestedTag));
            }

            return instance;
        }

        return null;
    }
}
