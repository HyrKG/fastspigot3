package cn.hyrkg.fastspigot3.processor.test;

import cn.hyrkg.fastspigot3.stereotype.Component;

/**
 * 测试组件B - 带有TestAnnotation注解，优先级更高
 */
@Component
@TestAnnotation(value = "componentB", priority = 5)
public class TestComponentB {
    
    private String description = "ComponentB with higher priority";
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "TestComponentB{description='" + description + "'}";
    }
}
