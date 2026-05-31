package cn.hyrkg.fastspigot3.spigot.forgeui;

import com.google.gson.JsonArray;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.util.UUID;


/**
 * JSON属性的类型安全包装器，封装SharedProperty的单个字段读写，支持泛型、默认值和PropertyShader自动构造。
 */
public class JsonContent<T> {
    private final Class tClass; // 属性值类型
    private Constructor shaderConstructor = null; // PropertyShader子类的构造器缓存

    public final SharedProperty property; // 所属属性容器
    public final String key; // 属性键名

    private T defValue = null; // 默认值

    protected boolean flagEmptyStringReturn = false; // 字符串缺失时返回空串而非"$empty"

    public JsonContent(SharedProperty property, String key, Class returnClazz) {
        this.property = property;
        this.key = key;

        tClass = returnClazz;

        // 如果目标类型是PropertyShader子类，缓存其构造器用于后续反射创建
        try {
            if (PropertyShader.class.isAssignableFrom(tClass)) {
                shaderConstructor = tClass.getConstructor(SharedProperty.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /** 设置默认值，属性不存在时返回此值 */
    public JsonContent<T> def(T defValue) {
        this.defValue = defValue;
        return this;
    }

    /** 属性是否存在于容器中 */
    public boolean has() {
        return property.hasProperty(key);
    }

    /** 设置属性值，UUID类型自动转为字符串存储 */
    public void set(T value) {
        if (value != null && value instanceof UUID)
            property.setProperty(key, ((UUID) value).toString());
        else
            property.setProperty(key, value);
    }

    /** 按泛型类型获取属性值，支持基础类型、UUID、JsonArray和PropertyShader */
    public T get() {
        if (tClass.equals(String.class))
            return (T) getString();
        else if (tClass.equals(Integer.class))
            return (T) getInt();
        else if (tClass.equals(Double.class))
            return (T) getDouble();
        else if (tClass.equals(Float.class))
            return (T) getFloat();
        else if (tClass.equals(Long.class))
            return (T) getLong();
        else if (tClass.equals(Boolean.class))
            return (T) getBoolean();
        if (tClass.equals(JsonArray.class))
            return (T) getJsonArray();
        else if (tClass.equals(UUID.class)) {
            return ((T) UUID.fromString(getString()));
        } else if (PropertyShader.class.isAssignableFrom(tClass)) {
            // 通过子属性构造PropertyShader实例
            SharedProperty theProperty = property.getAsProperty(key);
            if (theProperty != null) {
                try {
                    return (T) shaderConstructor.newInstance(theProperty);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

//		return null;
        return (T) getString();
    }

    /** 获取字符串值，flagEmptyStringReturn控制缺失时返回空串还是"$empty" */
    public String getString() {
        if (!flagEmptyStringReturn) {
            if (!has() && defValue != null) {
                return defValue.toString();
            }
            return property.getAsString(key);
        } else {
            if (property.hasProperty(key))
                return property.getAsString(key);
            else
                return "";
        }
    }

    /** 获取整型值，不存在时返回默认值或-1 */
    public Integer getInt() {
        if (!has() && defValue != null) {
            return (Integer) defValue;
        }
        return property.getAsInt(key);
    }

    /** 获取双精度浮点值 */
    public Double getDouble() {
        if (!has() && defValue != null) {
            return (Double) defValue;
        }
        return property.getAsDouble(key);
    }

    /** 获取单精度浮点值 */
    public Float getFloat() {
        if (!has() && defValue != null) {
            return (Float) defValue;
        }
        return property.getAsFloat(key);
    }

    /** 获取长整型值 */
    public Long getLong() {
        if (!has() && defValue != null) {
            return (Long) defValue;
        }
        return property.getAsLong(key);
    }

    /** 获取布尔值 */
    public Boolean getBoolean() {
        if (!has() && defValue != null) {
            return (Boolean) defValue;
        }
        return property.getAsBool(key);
    }

    /** 获取JsonArray，不存在时返回默认值或空数组 */
    public JsonArray getJsonArray() {
        if (has())
            return property.getAsJsonArray(key);
        else if (defValue != null) {
            return (JsonArray) defValue;
        } else {
            return new JsonArray();
        }

    }

    /** 设置字符串缺失时是否返回空串（而非默认的"$empty"） */
    public JsonContent setFlagEmptyStringReturn(boolean flagEmptyStringReturn) {
        this.flagEmptyStringReturn = flagEmptyStringReturn;
        return this;
    }
}
