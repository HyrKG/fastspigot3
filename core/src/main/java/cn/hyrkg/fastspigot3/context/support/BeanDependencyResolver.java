package cn.hyrkg.fastspigot3.context.support;

import cn.hyrkg.fastspigot3.context.annotation.Autowired;
import cn.hyrkg.fastspigot3.context.annotation.Inject;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于候选类的依赖收集与拓扑排序。仅使用字段上的 @Inject/@Autowired 建图，
 * 仅保留候选集合内的依赖；候选集外的依赖视为已满足（不纳入排序边）。
 * 带有 @Processor 注释的类将被优先排序，确保最早注册。
 */
public final class BeanDependencyResolver {

    public List<Class<?>> resolveOrder(List<Class<?>> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }

        // 使用 LinkedHash* 保持确定性顺序（与 candidates 顺序一致）
        Set<Class<?>> candidateSet = new LinkedHashSet<>(candidates);
        
        // 分离 @Processor 类和普通类
        List<Class<?>> processorClasses = new ArrayList<>();
        List<Class<?>> regularClasses = new ArrayList<>();
        
        for (Class<?> clazz : candidates) {
            if (clazz.isAnnotationPresent(ProcessBeanForAnnotation.class)) {
                processorClasses.add(clazz);
            } else {
                regularClasses.add(clazz);
            }
        }

        // 1) 收集依赖：deps[c] = { d in candidates | c 依赖 d }
        Map<Class<?>, Set<Class<?>>> deps = new LinkedHashMap<>();
        for (Class<?> clazz : candidates) {
            // 收集字段上的强/弱依赖（当前都计为依赖边），仅保留候选集合内的类型
            Set<Class<?>> requires = new LinkedHashSet<>();
            ReflectionUtils.findAnnotatedFields(clazz, Inject.class).forEach(f -> requires.add(f.getType()));
            ReflectionUtils.findAnnotatedFields(clazz, Autowired.class).forEach(f -> requires.add(f.getType()));
            Set<Class<?>> filtered = requires.stream()
                    .filter(candidateSet::contains)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            deps.put(clazz, filtered);
        }

        // 2) 构建邻接表：graph[d] 包含所有依赖 d 的节点（边 d -> c）
        Map<Class<?>, List<Class<?>>> graph = new LinkedHashMap<>();
        Map<Class<?>, Integer> indegree = new LinkedHashMap<>();
        for (Class<?> c : candidates) {
            graph.put(c, new ArrayList<>());
            indegree.put(c, 0);
        }
        for (Map.Entry<Class<?>, Set<Class<?>>> e : deps.entrySet()) {
            Class<?> clazz = e.getKey();
            for (Class<?> dep : e.getValue()) {
                graph.get(dep).add(clazz);
                indegree.put(clazz, indegree.get(clazz) + 1);
            }
        }

        // 3) Kahn 拓扑排序：优先处理 @Processor 类，然后处理普通类
        Queue<Class<?>> queue = new ArrayDeque<>();
        
        // 首先将入度为0的 @Processor 类入队
        for (Class<?> c : processorClasses) {
            if (indegree.get(c) == 0) {
                queue.add(c);
            }
        }
        
        // 然后将入度为0的普通类入队
        for (Class<?> c : regularClasses) {
            if (indegree.get(c) == 0) {
                queue.add(c);
            }
        }

        List<Class<?>> ordered = new ArrayList<>(candidates.size());
        while (!queue.isEmpty()) {
            Class<?> cur = queue.remove();
            ordered.add(cur);
            for (Class<?> next : graph.get(cur)) {
                int deg = indegree.get(next) - 1;
                indegree.put(next, deg);
                if (deg == 0) {
                    // 优先加入 @Processor 类
                    if (next.isAnnotationPresent(ProcessBeanForAnnotation.class)) {
                        // 插入到队列前面（优先处理）
                        ((ArrayDeque<Class<?>>) queue).addFirst(next);
                    } else {
                        queue.add(next);
                    }
                }
            }
        }

        // 4) 检测循环依赖：若未全部出队，说明存在环
        if (ordered.size() != candidates.size()) {
            Set<Class<?>> picked = new LinkedHashSet<>(ordered);
            List<Class<?>> unresolved = candidates.stream()
                    .filter(c -> !picked.contains(c))
                    .collect(Collectors.toList());
            throw new DependencyCycleException(unresolved);
        }

        return ordered;
    }
}


