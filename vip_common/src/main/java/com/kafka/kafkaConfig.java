package com.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class kafkaConfig {
    public static Properties getProperties(String name) {
        InputStream resourceAsStream = kafkaConfig.class.getClassLoader().getResourceAsStream(name+".properties");
        Properties props = new Properties();
        try {
            props.load(resourceAsStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally {
            try {
                resourceAsStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return props;

    }
}
