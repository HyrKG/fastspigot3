package cn.hyrkg.fastspigot3.context.annotation.processor;

/**
 * 表示注解处理器处理操作的结果
 * 可用于控制后续处理流程
 */
public enum ProcessorAction {
    /**
     * 继续处理后续步骤
     */
    CONTINUE,

    /**
     * 中断后续处理
     */
    STOP_PROCESSING,
}
