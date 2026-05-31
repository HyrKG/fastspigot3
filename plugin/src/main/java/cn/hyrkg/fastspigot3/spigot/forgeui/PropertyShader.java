package cn.hyrkg.fastspigot3.spigot.forgeui;

import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 属性着色器，封装SharedProperty并提供类型化的便捷属性访问方法。
 * 子类可声明具名字段，实现对JSON结构的面向对象访问。
 */
public class PropertyShader {
    public final SharedProperty property; // 底层属性容器

    /** 数组类型属性的缓存，避免重复反序列化 */
    protected HashMap<String, List<? extends PropertyShader>> arrayCacheMap = new HashMap<>();

    public PropertyShader(SharedProperty property) {
        this.property = property;
    }

    /** 获取底层属性容器 */
    public SharedProperty getProperty() {
        return property;
    }

    /** 清除所有数组缓存 */
    public void clearCaches() {
        arrayCacheMap.clear();
    }

    /** 获取带缓存的PropertyShader数组属性 */
    public <T extends PropertyShader> List<T> getCachedPropertyArray(String key, Class<T> type) {
        if (!arrayCacheMap.containsKey(key)) {
            arrayCacheMap.put(key, getProperty().getArray(key, type));
        }
        return (List<T>) arrayCacheMap.get(key);
    }

    /** 创建字符串属性访问器 */
    public JsonContent<String> cStr(String key) {
        return new JsonContent<String>(property, key, String.class);
    }

    /** 创建Double属性访问器 */
    public JsonContent<Double> cDouble(String key) {
        return new JsonContent<Double>(property, key, Double.class);
    }

    /** 创建Long属性访问器 */
    public JsonContent<Long> cLong(String key) {
        return new JsonContent<Long>(property, key, Long.class);
    }

    /** 创建Float属性访问器 */
    public JsonContent<Float> cFloat(String key) {
        return new JsonContent<Float>(property, key, Float.class);
    }

    /** 创建JsonArray属性访问器 */
    public JsonContent<JsonArray> cJsonArray(String key) {
        return new JsonContent<JsonArray>(property, key, JsonArray.class);
    }

    /** 创建UUID属性访问器 */
    public JsonContent<UUID> cUUID(String key) {
        return new JsonContent<UUID>(property, key, UUID.class);
    }

    /** 创建Integer属性访问器 */
    public JsonContent<Integer> cInt(String key) {
        return new JsonContent<Integer>(property, key, Integer.class);
    }

    /** 创建Boolean属性访问器 */
    public JsonContent<Boolean> cBool(String key) {
        return new JsonContent<Boolean>(property, key, Boolean.class);
    }


}
