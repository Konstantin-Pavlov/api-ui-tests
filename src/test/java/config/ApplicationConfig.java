package config;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:application.properties")  // Directly load from application.properties
public interface ApplicationConfig extends Config {

    @Key("url")
    String url();

    @Key("api")
    String api();
}
