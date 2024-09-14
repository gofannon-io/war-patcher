package xyz.gofannon.warpatcher.sayhello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SayHelloService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SayHelloService.class);
    private final Properties properties = new Properties();

    public SayHelloService() {
//        properties.setProperty("helloMessage", "No message");

        try(InputStream in = SayHelloService.class.getResourceAsStream("/hello.properties")) {
            properties.load(in);
        } catch (IOException ex) {
            LOGGER.error("Fail to load configuration file /hello.properties", ex);
        }
    }

    public String getDefaultHello() {
        return properties.getProperty("helloMessage");
    }

}
