package cn.hyrkg.fastspigot3.spigot.forgeui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 共享属性容器，管理JSON数据的增删改查，支持变化追踪、子属性嵌套和PropertyShader装饰。
 * 是Forge GUI属性同步的核心数据结构。
 */
public class SharedProperty {
    private static SharedProperty EMPTY_PROPERTY = new SharedProperty(); // 空属性单例，用于属性不存在时的哨兵返回

    public SharedProperty(JsonObject jsonObject) {
        this.completeJson = jsonObject;
    }

    public SharedProperty() {
        this.completeJson = new JsonObject();
    }

    /**
     * ####################################################################
     * Change Detect Feature
     * 变化感知特性
     * ####################################################################
     */
    private HashMap<String, SharedProperty> propertyHashMap = new HashMap<>(); // 子属性映射
    private JsonObject changedJson = new JsonObject(); // 待同步的变更数据
    private ArrayList<String> removes = new ArrayList<>(); // 待同步的删除列表

    /** 检测自身或子属性是否有未同步的变更 */
    public boolean detectChange() {
        for (Map.Entry<String, SharedProperty> stringSharedPropertyEntry : propertyHashMap.entrySet()) {
            if (stringSharedPropertyEntry.getValue().detectChange()) return true;
        }

        return changedJson.size() != 0 || !removes.isEmpty();
    }

    /** 生成增量更新JSON并清除变更记录 */
    public JsonObject generateAndClearUpdate() {

        JsonObject updated = changedJson;

        propertyHashMap.entrySet().forEach(j -> {
            if (j.getValue().detectChange()) {
                updated.add(j.getKey(), j.getValue().generateAndClearUpdate());
            }
        });

        if (!removes.isEmpty()) {
            JsonArray removeArray = new JsonArray();
            removes.forEach(j -> removeArray.add(j));

            updated.add("$remove", removeArray);
        }
        changedJson = new JsonObject();
        removes.clear();
        return updated;
    }

    /** 清除变更记录并返回完整JSON快照 */
    public JsonObject generateCompleteJsonAndClearUpdate() {
        //我们不使用生成的结果，只需执行来清理更新
        generateAndClearUpdate();
        return getCompleteJson();
    }

    /** 从客户端同步属性数据，支持子属性递归同步和$remove标签批量删除 */
    public void synProperty(JsonObject jsonObject) {

        // syn json to now properties.
        jsonObject.entrySet().forEach(j -> {
            if (propertyHashMap.containsKey(j.getKey()))
                propertyHashMap.get(j.getKey()).synProperty(j.getValue().getAsJsonObject());
            else if (j.getValue().isJsonObject()) {
                SharedProperty property = new SharedProperty();
                property.completeJson = j.getValue().getAsJsonObject();
                propertyHashMap.put(j.getKey(), property);
            } else {
                completeJson.remove(j.getKey());
                JsonElement value = j.getValue();
                completeJson.add(j.getKey(), value);
            }
        });

        // remove all properties if has remove tag
        if (jsonObject.has("$remove")) {
            JsonArray array = jsonObject.getAsJsonArray("$remove");
            array.forEach(j -> {
                if (propertyHashMap.containsKey(j.getAsString())) propertyHashMap.remove(j.getAsString());
                if (completeJson.has(j.getAsString())) completeJson.remove(j.getAsString());
            });
        }
        clearShaderCache();
    }

    /**
     * ####################################################################
     * Basic CRUD
     * 基础增删改查
     * ####################################################################
     */
    protected JsonObject completeJson; // 完整数据JSON

    /** 获取完整数据JSON */
    public JsonObject getCompleteJson() {
        return completeJson;
    }

    /** 检查属性是否存在（子属性或completeJson中） */
    public boolean hasProperty(String key) {
        return propertyHashMap.containsKey(key) || completeJson.has(key);
    }

    /** 移除属性 */
    public void removeProperty(String key) {
        this.setProperty(key, null);
    }

    /** 设置属性值，支持SharedProperty、PropertyShader、JsonObject、JsonElement及基础类型，null值表示删除 */
    public void setProperty(String key, Object value) {
        if (value == null) {
            if (changedJson.has(key)) changedJson.remove(key);
            if (completeJson.has(key)) completeJson.remove(key);
            if (propertyHashMap.containsKey(key)) propertyHashMap.remove(key);
            if (!removes.contains(key)) removes.add(key);
        } else {
            if (value instanceof SharedProperty) {
                propertyHashMap.put(key, (SharedProperty) value);
                ((SharedProperty) value).changedJson = ((SharedProperty) value).completeJson;
                completeJson.add(key, ((SharedProperty) value).getCompleteJson());
            } else if (value instanceof PropertyShader) {
                //remap
                setProperty(key, ((PropertyShader) value).getProperty());
            } else if (value instanceof JsonObject) {
                SharedProperty sharedProperty = new SharedProperty();
                sharedProperty.completeJson = (JsonObject) value;
                sharedProperty.changedJson = sharedProperty.completeJson;
                propertyHashMap.put(key, sharedProperty);
                completeJson.add(key, sharedProperty.getCompleteJson());
            } else if (value instanceof JsonElement) {
                completeJson.add(key, (JsonElement) value);
                changedJson.add(key, (JsonElement) value);
            } else if (value instanceof Number) {
                completeJson.addProperty(key, (Number) value);
                changedJson.addProperty(key, (Number) value);
            } else if (value instanceof String) {
                completeJson.addProperty(key, (String) value);
                changedJson.addProperty(key, (String) value);
            } else if (value instanceof Boolean) {
                completeJson.addProperty(key, (Boolean) value);
                changedJson.addProperty(key, (Boolean) value);
            } else if (value instanceof Character) {
                completeJson.addProperty(key, (Character) value);
                changedJson.addProperty(key, (Character) value);
            }
        }
    }

    /** 获取原始JsonElement */
    public JsonElement get(String key) {
        return completeJson.get(key);
    }


    /** 获取字符串值，不存在时返回"$empty" */
    public String getAsString(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsString();
        return "$empty";
    }

    /** 获取整型值，不存在时返回-1 */
    public int getAsInt(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsInt();
        return -1;
    }

    /** 获取长整型值，不存在时返回-1 */
    public long getAsLong(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsLong();
        return -1l;
    }

    /** 获取双精度浮点值，不存在时返回-1 */
    public double getAsDouble(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsDouble();
        return -1d;
    }

    /** 获取单精度浮点值，不存在时返回-1 */
    public float getAsFloat(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsFloat();
        return -1f;
    }

    /** 获取布尔值，不存在时返回false */
    public boolean getAsBool(String key) {
        if (completeJson.has(key)) return completeJson.get(key).getAsBoolean();
        return false;
    }

    /** 获取JsonArray，不存在时返回null */
    public JsonArray getAsJsonArray(String key) {
        if (completeJson.has(key)) {
            return completeJson.getAsJsonArray(key);
        }
        return null;
    }

    /**
     * ####################################################################
     * Convenient Method Addition
     * 便捷功能
     * ####################################################################
     */

    /** 获取子属性，不存在时返回空属性单例 */
    public SharedProperty getAsProperty(String key) {
        if (propertyHashMap.containsKey(key)) {
            return propertyHashMap.get(key);
        } else if (hasProperty(key) && get(key).isJsonObject()) {
            SharedProperty sharedProperty = new SharedProperty(get(key).getAsJsonObject());
            this.setProperty(key, sharedProperty);
            return sharedProperty;
        }
        return EMPTY_PROPERTY;
    }

    /** 获取或创建子属性 */
    public SharedProperty getOrCreateProperty(String key) {
        if (hasProperty(key)) return getAsProperty(key);
        SharedProperty property = new SharedProperty();
        setProperty(key, property);
        return property;
    }


    @Deprecated
    /**
     * 废弃方法，请勿使用。
     */ public SharedProperty getOrCreateSharedProperty(String key) {
        return getOrCreateProperty(key);
    }

    /** 将列表转为JsonArray，支持SharedProperty和PropertyShader元素 */
    public JsonArray getArrayFromList(List<?> value) {
        JsonArray array = new JsonArray();

        for (Object obj : value) {
            if (obj instanceof SharedProperty) {
                array.add(((SharedProperty) obj).completeJson);
            } else if (obj instanceof PropertyShader) {
                array.add(((PropertyShader) obj).property.completeJson);
            } else {
                array.add(String.valueOf(obj));
            }
        }

        return array;
    }

    /** 从JSON数组反序列化为PropertyShader子类列表 */
    @SneakyThrows
    public <T extends PropertyShader> List<T> getListFromArray(String key, Class<T> clazz) {

        Constructor<T> constructor = clazz.getConstructor(SharedProperty.class);

        List<T> list = new ArrayList<>();

        JsonElement element = getCompleteJson().get(key);
        if (element != null && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            array.forEach(j -> {
                JsonObject jsonObject = j.getAsJsonObject();

                try {
                    T t = constructor.newInstance(new SharedProperty(jsonObject));
                    list.add(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return list;
    }


    /** 将PropertyShader列表设置为JSON数组属性 */
    public void setArray(String key, List<? extends PropertyShader> list) {
        this.setProperty(key, getArrayFromList(list));
    }

    /** 获取PropertyShader子类列表（getListFromArray的别名） */
    public <T extends PropertyShader> List<T> getArray(String key, Class<T> clazz) {
        return getListFromArray(key, clazz);
    }

    /**
     * ####################################################################
     * Decorate Feature
     * 装饰能力
     * ####################################################################
     */
    protected PropertyShader shader = null; // 缓存的shader实例

    /** 将自身装饰为指定PropertyShader子类，类型不变时复用缓存 */
    public <T extends PropertyShader> T getAsShader(Class<T> shaderClazz) {
        try {
            if (this.shader == null || !this.shader.getClass().equals(shaderClazz)) {
                return (T) (this.shader = shaderClazz.getConstructor(SharedProperty.class).newInstance(this));
            }
            return (T) this.shader;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 获取子属性并装饰为指定PropertyShader子类 */
    public <T extends PropertyShader> T getAsShader(String key, Class<T> shaderClazz) {
        if (hasProperty(key)) {
            return getAsProperty(key).getAsShader(shaderClazz);
        }
        return null;
    }

    /** 清除shader缓存 */
    public void clearShaderCache() {
        shader = null;
    }

}
