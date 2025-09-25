package cn.hyrkg.fastspigot3.processor.test;

import cn.hyrkg.fastspigot3.beans.factory.DefaultBeanFactory;
import cn.hyrkg.fastspigot3.context.support.BeanDependencyResolver;

import java.util.Arrays;
import java.util.List;

/**
 * Processor功能测试 - 使用main方法进行测试
 */
public class ProcessorTest {

    public static void main(String[] args) {
        ProcessorTest test = new ProcessorTest();
        
        System.out.println("=== 开始Processor功能测试 ===\n");
        
        try {
            test.testProcessorPriorityRegistration();
            test.testProcessorHandlesCustomAnnotations();
            test.testProcessorRegistrationTiming();
            
            System.out.println("\n=== 所有测试通过! ===");
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试Processor类优先注册功能
     */
    public void testProcessorPriorityRegistration() {
        System.out.println("1. 测试Processor类优先注册功能");
        
        BeanDependencyResolver dependencyResolver = new BeanDependencyResolver();
        
        // 准备测试类列表，故意将Processor类放在后面
        List<Class<?>> candidates = Arrays.asList(
            TestComponentA.class,
            TestComponentB.class,
            RegularComponent.class,
            TestProcessorAnnotation.class  // Processor类放在最后
        );

        // 使用依赖解析器进行排序
        List<Class<?>> orderedClasses = dependencyResolver.resolveOrder(candidates);

        // 验证Processor类被排在最前面
        if (!TestProcessorAnnotation.class.equals(orderedClasses.get(0))) {
            throw new AssertionError("带有@Processor注解的类应该被优先排序，但实际第一个是: " 
                                   + orderedClasses.get(0).getSimpleName());
        }
        
        System.out.println("   排序结果:");
        for (int i = 0; i < orderedClasses.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + orderedClasses.get(i).getSimpleName());
        }
        System.out.println("   ✓ Processor优先排序测试通过\n");
    }

    /**
     * 测试Processor处理自定义注解功能
     */
    public void testProcessorHandlesCustomAnnotations() {
        System.out.println("2. 测试Processor处理自定义注解功能");
        
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        BeanDependencyResolver dependencyResolver = new BeanDependencyResolver();
        TestProcessorAnnotation.clearProcessedBeans(); // 清空之前的处理记录

        // 按照依赖解析的顺序注册beans
        List<Class<?>> candidates = Arrays.asList(
            TestProcessorAnnotation.class,    // Processor先注册
            TestComponentA.class,
            TestComponentB.class,
            RegularComponent.class
        );

        List<Class<?>> orderedClasses = dependencyResolver.resolveOrder(candidates);

        // 注册并创建所有beans
        for (Class<?> clazz : orderedClasses) {
            beanFactory.registerBean(clazz);
        }

        // 获取beans以触发创建和处理
        Object processor = beanFactory.getBean("cn.hyrkg.fastspigot3.processor.test.TestProcessor");
        Object componentA = beanFactory.getBean("cn.hyrkg.fastspigot3.processor.test.TestComponentA");
        Object componentB = beanFactory.getBean("cn.hyrkg.fastspigot3.processor.test.TestComponentB");
        Object regularComponent = beanFactory.getBean("cn.hyrkg.fastspigot3.processor.test.RegularComponent");

        // 验证processor存在
        if (processor == null) {
            throw new AssertionError("TestProcessor应该被成功创建");
        }
        if (!(processor instanceof TestProcessorAnnotation)) {
            throw new AssertionError("Bean应该是TestProcessor的实例");
        }

        // 验证组件被创建
        if (componentA == null) {
            throw new AssertionError("TestComponentA应该被成功创建");
        }
        if (componentB == null) {
            throw new AssertionError("TestComponentB应该被成功创建");
        }
        if (regularComponent == null) {
            throw new AssertionError("RegularComponent应该被成功创建");
        }

        // 验证processor处理了带有@TestAnnotation的组件
        List<TestProcessorAnnotation.ProcessedBeanInfo> processedBeans = TestProcessorAnnotation.getProcessedBeans();
        
        System.out.println("   处理过的beans:");
        for (TestProcessorAnnotation.ProcessedBeanInfo info : processedBeans) {
            System.out.println("   - " + info.toString());
        }

        // 应该只处理了2个带有@TestAnnotation的组件
        if (processedBeans.size() != 2) {
            throw new AssertionError("应该处理了2个带有@TestAnnotation注解的组件，但实际处理了: " 
                                   + processedBeans.size() + "个");
        }

        // 验证处理的组件正确
        boolean foundComponentA = false;
        boolean foundComponentB = false;
        boolean foundRegularComponent = false;
        
        for (TestProcessorAnnotation.ProcessedBeanInfo info : processedBeans) {
            if (info.beanClass == TestComponentA.class 
                && "componentA".equals(info.annotationValue) 
                && info.priority == 1) {
                foundComponentA = true;
            }
            if (info.beanClass == TestComponentB.class 
                && "componentB".equals(info.annotationValue) 
                && info.priority == 5) {
                foundComponentB = true;
            }
            if (info.beanClass == RegularComponent.class) {
                foundRegularComponent = true;
            }
        }

        if (!foundComponentA) {
            throw new AssertionError("应该处理了TestComponentA");
        }
        if (!foundComponentB) {
            throw new AssertionError("应该处理了TestComponentB");
        }
        if (foundRegularComponent) {
            throw new AssertionError("不应该处理RegularComponent（没有@TestAnnotation注解）");
        }

        System.out.println("   ✓ Processor处理自定义注解测试通过\n");
    }

    /**
     * 测试Processor注册时机
     */
    public void testProcessorRegistrationTiming() {
        System.out.println("3. 测试Processor注册时机");
        
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        BeanDependencyResolver dependencyResolver = new BeanDependencyResolver();

        // 创建混合的候选类列表
        List<Class<?>> candidates = Arrays.asList(
            TestComponentA.class,
            TestProcessorAnnotation.class,
            TestComponentB.class
        );

        // 解析依赖顺序
        List<Class<?>> orderedClasses = dependencyResolver.resolveOrder(candidates);

        // 验证TestProcessor是第一个
        if (!TestProcessorAnnotation.class.equals(orderedClasses.get(0))) {
            throw new AssertionError("TestProcessor应该是第一个被注册的类，但实际是: " 
                                   + orderedClasses.get(0).getSimpleName());
        }

        // 按顺序注册
        long processorRegisterTime = 0;
        long componentARegisterTime = 0;
        long componentBRegisterTime = 0;

        for (Class<?> clazz : orderedClasses) {
            long currentTime = System.nanoTime();
            beanFactory.registerBean(clazz);
            
            if (clazz == TestProcessorAnnotation.class) {
                processorRegisterTime = currentTime;
            } else if (clazz == TestComponentA.class) {
                componentARegisterTime = currentTime;
            } else if (clazz == TestComponentB.class) {
                componentBRegisterTime = currentTime;
            }
        }

        // 验证注册时间顺序
        if (processorRegisterTime >= componentARegisterTime) {
            throw new AssertionError("Processor应该比ComponentA更早注册");
        }
        if (processorRegisterTime >= componentBRegisterTime) {
            throw new AssertionError("Processor应该比ComponentB更早注册");
        }

        System.out.println("   注册顺序: " + 
                         orderedClasses.get(0).getSimpleName() + " -> " +
                         orderedClasses.get(1).getSimpleName() + " -> " +
                         orderedClasses.get(2).getSimpleName());
        System.out.println("   ✓ Processor注册时机测试通过\n");
    }
}
