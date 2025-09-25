package cn.hyrkg.fastspigot3.framework.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.hyrkg.fastspigot3.framework.context.LifecycleInvoker;
import cn.hyrkg.fastspigot3.framework.context.ScanService;
import cn.hyrkg.fastspigot3.framework.inject.Injector;


/**
 * Bean 管理器：
 * <p>
 * 负责维护 Class 到实例的映射，提供 Bean 的注册、查询与卸载能力；
 * 并与 Injector 协作，在注册或显式调用时完成依赖注入与连接。
 */
public class BeanManager {

    private final BeanFactory factory = new BeanFactory(this); // Bean 工厂
    private final Injector injector = new Injector(this); // 依赖注入器
    private final LifecycleInvoker lifecycle = new LifecycleInvoker(); // 生命周期回调器

    private HashMap<Class<?>, Object> registeredBeanMap = new HashMap<>(); // 注册的Bean映射
    private final ThreadLocal<Set<Class<?>>> constructing = ThreadLocal.withInitial(HashSet::new); // 构造栈，用于检测循环依赖，避免无限递归

    /**
     * 注册指定类型的 Bean
     */
    public <T> T registerBean(Class<T> clazz) {
        try {
            T instance = createGuarded(clazz);
            lifecycle.invokeCreate(instance);
            registerBeanInstance(clazz, instance);
            injector.injectInto(instance);
            lifecycle.invokeReady(instance);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate bean: " + clazz.getName(), e);
        }
    }

    /**
     * 以给定实例注册 Bean（跳过构造创建流程），适用于外部已构建好的对象。
     */
    public void registerBeanInstance(Class<?> clazz, Object instance) {
        unregisterBean(clazz);
        registeredBeanMap.put(clazz, instance);
    }

    /**
     * 卸载指定类型的 Bean（忽略传入实例，仅根据类型移除）。
     */
    public void unregisterBean(Class<?> clazz) {
        registeredBeanMap.remove(clazz);
    }

    public <T> T getOrRegisterBean(Class<T> clazz) {
        T bean = getBean(clazz);
        if (bean == null) {
            bean = registerBean(clazz);
        }
        return bean;
    }

    public <T> T getBean(Class<T> clazz) {
        Object instance = registeredBeanMap.get(clazz);
        if (instance == null) {
            return null;
        }
        if (!clazz.isInstance(instance)) {
            throw new RuntimeException("Registered bean is not of the requested type: " + clazz.getName());
        }
        return (T) instance;
    }


    /**
     * 带循环依赖检测的创建：检测当前构造栈是否已包含该类型。
     */
    private <T> T createGuarded(Class<T> clazz) throws ReflectiveOperationException {
        Set<Class<?>> stack = constructing.get();
        if (stack.contains(clazz)) {
            throw new RuntimeException("Circular dependency detected while constructing: " + clazz.getName());
        }
        stack.add(clazz);
        try {
            T instance = factory.createBean(clazz);
            return instance;
        } finally {
            stack.remove(clazz);
        }
    }

    /**
     * 使用 ClassScanner 扫描包并注册需要注入的类：
     * 仅当类包含 @Inject 或 @Wire 字段，且可通过无参构造创建时才注册。
     */
    /**
     * 扫描并注册可注入类：仅对具备可注入字段且可无参构造的类进行注册。
     */
    public void scanAndRegister(String basePackage) {
        ScanService scan = new ScanService();
        for (Class<?> clazz : scan.scan(basePackage)) {
            if (registeredBeanMap.containsKey(clazz)) {
                continue;
            }
            if (!scan.isRegistrable(clazz)) {
                continue;
            }
            registerBean(clazz);
        }
    }
}
