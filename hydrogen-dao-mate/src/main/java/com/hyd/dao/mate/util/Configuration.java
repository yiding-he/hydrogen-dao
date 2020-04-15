package com.hyd.dao.mate.util;

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Configuration {

    public static final String CONFIG_FILE_PATH = "hydrogen-mate-config.xml";

    public static void saveConfiguration(Object config, String fileName) {
        try {
            if (config == null || fileName == null) {
                return;
            }

            XStream xStream = new XStream();
            xStream.alias("config", config.getClass());

            Files.write(Paths.get(fileName), xStream.toXML(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T readConfiguration(String filePath, Class<T> type) {
        try {
            if (filePath == null || type == null) {
                throw new IllegalArgumentException(
                    "Arguments contains null value; filePath=" + filePath + ", type=" + type);
            }

            XStream xStream = new XStream();
            xStream.alias("config", type);

            String xml = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            return (T) xStream.fromXML(xml);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
