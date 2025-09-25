package cn.hyrkg.fastspigot3.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.hyrkg.fastspigot3.injector.Injector;
 
 

/**
 * Bean 管理器：
 * <p>
 * 负责维护 Class 到实例的映射，提供 Bean 的注册、查询与卸载能力；
 * 并与 Injector 协作，在注册或显式调用时完成依赖注入与连接。
 */
public class BeanManager {

    private HashMap<Class<?>, Object> registeredBeanMap = new HashMap<>();
    private final ThreadLocal<Set<Class<?>>> constructing = ThreadLocal.withInitial(HashSet::new);
    private final Injector injector = new Injector(this);
    private final LifecycleInvoker lifecycle = new LifecycleInvoker();


    /**
     * 注册指定类型的 Bean：
     * <ul>
     * 	<li>如已存在相同类型，则先卸载旧实例</li>
     * 	<li>使用无参构造创建实例，缓存到容器</li>
     * 	<li>创建完成后立即执行依赖注入</li>
     * </ul>
     */
    public <T> void registerBean(Class<T> clazz) {
        if (registeredBeanMap.containsKey(clazz)) {
            unregisterBean(clazz, registeredBeanMap.get(clazz));
        }
        try {
            T instance = createGuarded(clazz);
            registeredBeanMap.put(clazz, instance);
            // 对新注册的 Bean 立即执行注入
            injector.injectInto(instance);
            // 注入完成后触发 @OnReady
            lifecycle.invokeReady(instance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate bean: " + clazz.getName(), e);
        }
    }

    /**
     * 以给定实例注册 Bean（跳过构造创建流程），适用于外部已构建好的对象。
     */
    public void registerBeanInstance(Class<?> clazz, Object instance) {
        if (registeredBeanMap.containsKey(clazz)) {
            unregisterBean(clazz, registeredBeanMap.get(clazz));
        }
        registeredBeanMap.put(clazz, instance);
    }

    /**
     * 卸载指定类型的 Bean（忽略传入实例，仅根据类型移除）。
     */
    public void unregisterBean(Class<?> clazz, Object bean) {
        registeredBeanMap.remove(clazz);
    }

    /**
     * 卸载指定类型的 Bean。
     */
    public void unregisterBean(Class<?> clazz) {
        registeredBeanMap.remove(clazz);
    }

    @SuppressWarnings("unchecked")
    /**
     * 获取指定类型的 Bean，不存在则返回 null。
     */
    public <T> T getBean(Class<T> clazz) {
        Object instance = registeredBeanMap.get(clazz);
        if (instance == null) {
            return null;
        }
        return (T) instance;
    }

    /**
     * 通过无参构造创建实例。
     */
    private <T> T createInstance(Class<T> clazz) throws ReflectiveOperationException {
        return clazz.getDeclaredConstructor().newInstance();
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
            T instance = createInstance(clazz);
            // 构造完成后触发 @OnCreate
            lifecycle.invokeCreate(instance);
            return instance;
        } finally {
            stack.remove(clazz);
        }
    }

    /**
     * 反射调用 @OnCreate 标注的无参方法。
     */
    

    /**
     * 供 @Inject 使用：
     * - 始终创建新实例，注册到容器，并执行依赖注入
     */
    public <T> T resolveForInject(Class<T> depType) {
        try {
            T created = createGuarded(depType);
            registerBeanInstance(depType, created);
        injector.injectInto(created);
        lifecycle.invokeReady(created);
            return created;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to resolve dependency for inject: " + depType.getName(), e);
        }
    }

    /**
     * 将目标对象执行一次注入（不注册为 Bean）。
     */
    /**
     * 对外提供一次性注入能力：不注册目标对象，仅执行依赖注入/连接。
     */
    public void wireInto(Object target) {
        // 对外部传入但未由容器构造的对象，也触发一次 @OnCreate
        lifecycle.invokeCreate(target);
        injector.injectInto(target);
        lifecycle.invokeReady(target);
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
            if (!scan.isRegistrable(clazz)) {
                continue;
            }
            if (registeredBeanMap.containsKey(clazz)) {
                continue;
            }
            registerBean(clazz);
        }
    }

    /**
     * 判定一个类是否可被容器自动注册。
     */
    // isRegistrable 已迁移至 ScanService
}
