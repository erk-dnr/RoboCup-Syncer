package syncgod.config;

import static syncgod.config.ConfigValue.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configurable values and values for testing.
 *
 * @author tj18b
 */
public final class Config {
    private static final double fps = 24;
    private static final int skipFrames = 10;
    private static final double mouseTranslation = 1;
    private static final int videoUpdateRate = 10;
    private static final int clockUpdateRate = 10;
    private static final int videoWidth = 480;
    private static final int videoHeight = 360;
    private static final Logger LOG = Logger.getLogger(Config.class.getName());
    private static final String USER_SETTINGS_PATH = "settings/config.xml";
    private static final String UTIL_SETTINGS_PATH = "settings/util.xml";

    private static Properties userConfig = setDefaultConfigSettings();
    private static Properties utilConfig = new Properties();

    /**
     * Loads the default data into properties object.
     */
    private static Properties setDefaultConfigSettings() {
        Properties defaultProperties = new Properties();

        initConfigValue(defaultProperties, SkipFrameAmount, skipFrames);
        initConfigValue(defaultProperties, MouseResizeSens, mouseTranslation);
        initConfigValue(defaultProperties, WorkerRate, videoUpdateRate);
        initConfigValue(defaultProperties, ClockRate, clockUpdateRate);
        initConfigValue(defaultProperties, VideoWidth, videoWidth);
        initConfigValue(defaultProperties, VideoHeight, videoHeight);
        initConfigValue(defaultProperties, Fps, fps);

        return defaultProperties;
    }

    /**
     * Sets the value for given key.
     *
     * @param key Key for the value
     * @param value Value
     */
    public static void setUtilProperty(UtilValue key, String value) {
        utilConfig.setProperty(key.name(), value);
    }

    /**
     * Sets the value for given key.
     * @param key Key for the value
     * @return value as String
     */
    public static String getUtilProperty(UtilValue key) {
        return utilConfig.getProperty(key.name());
    }

    public static void setToDefault() {
        userConfig = setDefaultConfigSettings();
    }

    private static void initConfigValue(Properties props, ConfigValue key, double value) {
        props.setProperty(key.name(), Double.toString(value));
    }

    /**
     * Sets the double value for given key.
     *
     * @param key   Key for the value
     * @param value New double value for the key.
     */
    public static void set(final ConfigValue key, final double value) {
        userConfig.setProperty(key.name(), Double.toString(value));
    }

    /**
     * Get value from key {@link ConfigValue}.
     * @param key as ConfigValue
     * @return value as double
     */
    public static double get(final ConfigValue key) {
        return Double.valueOf(userConfig.getProperty(key.name()));
    }

    public static String getAsString(final ConfigValue key) {
        return String.valueOf(get(key));
    }

    public static void loadAllProperties() {
        loadProperties(userConfig, USER_SETTINGS_PATH);
        loadProperties(utilConfig, UTIL_SETTINGS_PATH);
    }

    public static void saveConfig() {
        saveProperties(userConfig, USER_SETTINGS_PATH);
    }

    public static void saveUtilProperty() {
        saveProperties(utilConfig, UTIL_SETTINGS_PATH);
    }

    /**
     * Loads properties from xml to property.
     */
    private static void loadProperties(Properties props, String path) {
        try (InputStream in = new FileInputStream(path)) {
            props.loadFromXML(in);
        } catch (IOException ex) {
            LOG.log(Level.WARNING,
                    "Could not find config file, creating new Config at: " + path);
            saveProperties(props, path);
        }
    }

    /**
     * Saves the current properties to relating file.
     */
    private static void saveProperties(Properties props, String path) {
        File dir = new File(path.split("/")[0]);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try (OutputStream out = new FileOutputStream(path)) {
            props.storeToXML(out, props.toString());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Could not write in the XML file while saving");
        }
    }
}
