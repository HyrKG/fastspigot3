package cn.hyrkg.fastspigot3.processor.test;

import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试用的处理器 - 用于处理 @TestAnnotation 注解
 */
@Component
@ProcessBeanForAnnotation(TestAnnotation.class)
public class TestProcessorAnnotation implements BeanAnnotationProcessor<TestAnnotation> {

    // 记录处理过的bean信息，用于测试验证
    private static final List<ProcessedBeanInfo> processedBeans = new ArrayList<>();

    @Override
    public void postProcess(TestAnnotation annotation, Object bean) {
        ProcessedBeanInfo info = new ProcessedBeanInfo();
        info.beanClass = bean.getClass();
        info.annotationValue = annotation.value();
        info.priority = annotation.priority();
        info.processTime = System.currentTimeMillis();
        
        processedBeans.add(info);
        
        System.out.println("TestProcessor处理了bean: " + bean.getClass().getSimpleName() 
                         + ", 注解值: " + annotation.value() 
                         + ", 优先级: " + annotation.priority());
    }

    /**
     * 获取已处理的bean信息（用于测试验证）
     */
    public static List<ProcessedBeanInfo> getProcessedBeans() {
        return new ArrayList<>(processedBeans);
    }

    /**
     * 清空处理记录（用于测试清理）
     */
    public static void clearProcessedBeans() {
        processedBeans.clear();
    }

    /**
     * 已处理bean的信息
     */
    public static class ProcessedBeanInfo {
        public Class<?> beanClass;
        public String annotationValue;
        public int priority;
        public long processTime;

        @Override
        public String toString() {
            return "ProcessedBeanInfo{" +
                    "beanClass=" + beanClass.getSimpleName() +
                    ", annotationValue='" + annotationValue + '\'' +
                    ", priority=" + priority +
                    ", processTime=" + processTime +
                    '}';
        }
    }
}
