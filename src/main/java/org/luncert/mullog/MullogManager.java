package org.luncert.mullog;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.luncert.mullog.appender.Appender;
import org.luncert.mullog.exception.MullogException;

public class MullogManager implements Serializable {

    private static final long serialVersionUID = 7606646448285009177L;

    private static Map<String, Appender> appenders = new HashMap<>();

    private MullogManager() {}

    static Path configPath;

    private static void loadConfig() {
        // 优先从jar文件中获取配置文件,而不是使用Mullog的用户的工作目录
        File configFile = Paths.get(MullogManager.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "mullog.properties").toFile();
        if (!configFile.exists()) {
            configFile = new File(MullogManager.class.getClassLoader().getResource("mullog.properties").getPath());
        }
        if (!configFile.exists()) {
            configFile = Paths.get(System.getProperty("user.dir"), "mullog.properties").toFile();
        }
        if (!configFile.exists()) {
            System.out.println("[INFO] Mullog configuration file not found");
            return;
        }
        MullogManager.configPath = configFile.toPath();

        Map<String, Properties> confs = new HashMap<>();
        Properties props = new Properties();
        try {
            InputStream in = new FileInputStream(configFile);
            props.load(in);
            // cast
            for (Object key : props.keySet()) {
                String tmp = (String)key;
                int i = tmp.lastIndexOf(".");

                String namespace = tmp.substring(0, i);
                String name = tmp.substring(i + 1);
                Object value = props.get(key);

                if (confs.containsKey(namespace)) confs.get(namespace).put(name, value);
                else {
                    Properties subProps = new Properties();
                    subProps.put(name, value);
                    confs.put(namespace, subProps);
                }
            }
            // resolve
            for (String namespace: confs.keySet()) {
                Properties subProps = confs.get(namespace);
                try {
                    if (subProps.get("level") == null)
                        throw new MullogException("field level must be specified in mullog.properties");
                    if (subProps.get("type") == null)
                        throw new MullogException("field type must be specified in mullog.properties");
                    if (subProps.get("format") == null)
                        throw new MullogException("field format must be specified in mullog.properties");
                    Class<?> clazz = Class.forName(subProps.getProperty("type"));
                    if (Appender.class.isAssignableFrom(clazz)) {
                        Constructor<?> constructor = clazz.getConstructor(Properties.class);
                        appenders.put(namespace, (Appender)constructor.newInstance(subProps));
                    }
                } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    static {
        loadConfig();
    }

    /**
     * 添加Appender并指定name
     * @param name
     * @param appender
     */
    static void addAppender(String name, Appender appender) { appenders.put(name, appender); }

    /**
     * @param name
     * @return Nullable Appender
     */
    static Appender getAppender(String name) { return appenders.get(name); }

    static Collection<Appender> getAppenders() {
        if (appenders.size() > 0) return appenders.values();
        else return null;
    }

}
