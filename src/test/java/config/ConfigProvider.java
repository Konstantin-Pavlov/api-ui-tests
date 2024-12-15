package config;

import org.aeonbits.owner.ConfigFactory;

public class ConfigProvider {

    private static final ApplicationConfig CONFIG = ConfigFactory.create(ApplicationConfig.class);

    private ConfigProvider() {
        // Private constructor to prevent instantiation
    }

    public static ApplicationConfig getConfig() {
        return CONFIG;
    }
}
