package cn.hyrkg.fastspigot3.context.support;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 在依赖解析与拓扑排序阶段检测到循环依赖时抛出。
 */
public class DependencyCycleException extends RuntimeException {
    public DependencyCycleException(List<Class<?>> cycleCandidates) {
        super("Detected cyclic or unresolved dependencies among: " +
                cycleCandidates.stream().map(Class::getName).collect(Collectors.joining(", ")));
    }
}


