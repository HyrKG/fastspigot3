package cn.hyrkg.fastspigot3.spigot.logger;

public interface Logger {
    /**
     * 用于输出开发和调试阶段的详细信息，可以被关闭。
     */
    void debug(String message,Object... args);

    /**
     * 用于记录系统业务流程、正常运行状态。
     */
    void info(String message,Object... args);

    /**
     * 用于强调系统中的某些特殊情况、状态变化或潜在影响。
     */
    void notice(String message,Object... args);

    /**
     * 用于记录可能存在问题或风险的情况，提示需要关注，但系统仍能正常运行。
     */
    void warn(String message,Object... args);

    /**
     * 用于记录系统中已经发生的错误或异常，通常意味着某个功能点失败。
     */
    void error(String message,Object... args);
}
