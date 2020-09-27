package com.hyd.i18n;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * code from
 * http://www.java2s.com/Code/Java/JDK-6/XMLresourcebundle.htm
 *
 * @author yidin
 */
public class XMLResourceBundleControl extends ResourceBundle.Control {

    private static String XML = "xml";

    public List<String> getFormats(String baseName) {
        return Collections.singletonList(XML);
    }

    public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                    ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException,
            IOException {

        if ((baseName == null) || (locale == null) || (format == null) || (loader == null)) {
            throw new NullPointerException();
        }
        if (!format.equals(XML)) {
            return null;
        }

        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, format);
        URL url = loader.getResource(resourceName);
        if (url == null) {
            return null;
        }
        URLConnection connection = url.openConnection();
        if (connection == null) {
            return null;
        }
        if (reload) {
            connection.setUseCaches(false);
        }
        InputStream stream = connection.getInputStream();
        if (stream == null) {
            return null;
        }
        BufferedInputStream bis = new BufferedInputStream(stream);
        ResourceBundle bundle = new XMLResourceBundle(bis);
        bis.close();

        return bundle;
    }
}

class XMLResourceBundle extends ResourceBundle {

    private Properties props;

    XMLResourceBundle(InputStream stream) throws IOException {
        props = new Properties();
        props.loadFromXML(stream);
    }

    protected Object handleGetObject(String key) {
        return props.getProperty(key);
    }

    public Enumeration<String> getKeys() {
        Set<String> handleKeys = props.stringPropertyNames();
        return Collections.enumeration(handleKeys);
    }
}
