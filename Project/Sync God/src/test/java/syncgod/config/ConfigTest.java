package syncgod.config;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static syncgod.config.ConfigValue.ClockRate;
import static syncgod.config.ConfigValue.MouseResizeSens;
import static syncgod.config.ConfigValue.SkipFrameAmount;
import static syncgod.config.ConfigValue.VideoHeight;
import static syncgod.config.ConfigValue.WorkerRate;
import static syncgod.config.ConfigValue.VideoWidth;

public class ConfigTest {


    //Defaults values from Config Class
    private static final double defaultSkipFrames = 10;
    private static final double defaultMouseTranslation = 1.0;
    private static final double defaultClockUpdaterate = 10;
    private static final double defaultVideoUpdaterate = 10;
    private static final double defaultVideoWidth = 480;
    private static final double defaultVideoHeight = 360;
    private static Properties userProp = new Properties();    //For storing the user properties
    private static Properties utilProps = new Properties();   //Storing Util Props


    private static void loadFromXML(Properties props, String path) {
        try (FileInputStream in = new FileInputStream(path)) {
            props.loadFromXML(in);
        } catch (IOException e) {
            System.out.println("Error while loading");
        }
    }

    private static void saveToXML(Properties props, String path) {
        try (FileOutputStream out = new FileOutputStream(path)) {
            props.storeToXML(out, props.toString());
        } catch (IOException e) {
            System.out.println("Error while saving");
        }
    }
    /**
     * Saves the actual user settings.
     */
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Testing Config");
        //Save old user properties. Dont want to change the user settings.
       loadFromXML(userProp, "settings/config.xml");
        loadFromXML(utilProps, "settings/util.xml");
        Config.setToDefault();
    }

    /**
     * Stores the actual user setting again in the xml file.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("Done Testing Config");
        Config.setToDefault();
        saveToXML(userProp ,"settings/config.xml");
        saveToXML(utilProps,"settings/util.xml");
        Config.loadAllProperties();
    }

    /**
     * Everything to default and empty xml file.
     */
    @Before
    public void setUp() {
        Config.setToDefault();
        Config.saveConfig();
        Config.saveUtilProperty();
    }

    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Tests, if default are the default of class.
     */
    @Test
    public void testDefault() {
        //Should already be default

        System.out.println("Testing defaults");
        assertThat(Config.get(SkipFrameAmount), is(equalTo(defaultSkipFrames)));
        assertThat(Config.get(MouseResizeSens), is(equalTo(defaultMouseTranslation)));
        assertThat(Config.get(ClockRate), is(equalTo(defaultClockUpdaterate)));
        assertThat(Config.get(WorkerRate), is(equalTo(defaultVideoUpdaterate)));
        assertThat(Config.get(VideoHeight), is(equalTo(defaultVideoHeight)));
        assertThat(Config.get(VideoWidth), is(equalTo(defaultVideoWidth)));


    }

    /**
     * Tests, that the toDefault() method restores the user property to the defaults.
     */
    @Test
    public void restoreDefault() {
        System.out.println("Testing default restoration");

        //When skipFrames changed
        double newSkipFrames = 15;
        Config.set(SkipFrameAmount, newSkipFrames);

        //userProperties should be updated
        assertThat(Config.get(SkipFrameAmount), is(equalTo(newSkipFrames)));

        //When everything to default
        Config.setToDefault();

        //Should have default value
        assertThat(Config.get(SkipFrameAmount), is(equalTo(defaultSkipFrames)));
    }

    /**
     * Tests, that after a save the data is saved in the xml file.
     */
    @Test
    public void saveData() {
        System.out.println("Testing saving data");
        //Checking its the defaults
        assertThat(Config.get(ConfigValue.VideoHeight), is(equalTo(defaultVideoHeight)));

        double newVideoHeight = 500;
        Config.set(ConfigValue.VideoHeight, newVideoHeight);
        Config.saveConfig();

        Properties checkingFile = new Properties();
        loadFromXML(checkingFile, "settings/config.xml");


        double savedVideoHeight = Double.valueOf((checkingFile.getProperty(VideoHeight.name())));
        assertThat(savedVideoHeight, is(equalTo(newVideoHeight)));
    }
}




