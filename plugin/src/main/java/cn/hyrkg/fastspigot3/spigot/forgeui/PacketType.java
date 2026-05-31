package cn.hyrkg.fastspigot3.spigot.forgeui;

/**
 * 数据包类型枚举，标识Forge GUI通信协议中的包类型。
 */
public enum PacketType {
    message("msg"),     // 普通消息
    display("property"), // 界面展示
    update("update"),   // 属性更新
    close;              // 界面关闭

    /** 对应JSON协议中的字段路径，close类型无路径 */
    public final String propertyPath;

    PacketType() {
        this.propertyPath = null;
    }

    PacketType(String propertyPath) {
        this.propertyPath = propertyPath;
    }
}
