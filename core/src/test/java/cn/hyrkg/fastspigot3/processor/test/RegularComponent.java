package cn.hyrkg.fastspigot3.processor.test;

import cn.hyrkg.fastspigot3.stereotype.Component;

/**
 * 普通组件 - 没有TestAnnotation注解，用于验证processor不会处理它
 */
@Component
public class RegularComponent {
    
    private String data = "Regular component without TestAnnotation";
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "RegularComponent{data='" + data + "'}";
    }
}
