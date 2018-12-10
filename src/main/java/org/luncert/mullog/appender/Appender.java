package org.luncert.mullog.appender;

public interface Appender {

    /**
     * @param logLevel 日志等级 {@link org.luncert.mullog.LogLevel}
     * @param fields 消息,对应Mullog配置文件中format属性中的%S
     * @throws Exception
     */
    public void log(int logLevel, String... fields) throws Exception;

    /**
     * @param logLevel 日志等级 {@link org.luncert.mullog.LogLevel}
     */
    public void setLogLevel(int logLevel);

    /**
     * @return logLevel
     */
    public int getLogLevel();

}