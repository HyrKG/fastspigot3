package cn.hyrkg.fastspigot3.processor.test;

import cn.hyrkg.fastspigot3.stereotype.Component;

/**
 * 测试组件A - 带有TestAnnotation注解
 */
@Component
@TestAnnotation(value = "componentA", priority = 1)
public class TestComponentA {
    
    private String name = "ComponentA";
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "TestComponentA{name='" + name + "'}";
    }
}
