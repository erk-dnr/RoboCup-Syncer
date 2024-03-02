package syncgod.menu;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.testfx.framework.junit.ApplicationTest;


/**
 *Tests for Menu Class.
 * @author sirkpetzold
 */
public class MenuTest extends ApplicationTest {

    private Parent root;
    private MenuBar bar;


    @Override
    public void start(Stage stage) throws IOException {
        root = FXMLLoader.load(Menu.class.getResource("/fxml/menu/Menu.fxml"));
        bar = from(root).lookup("#menuBar").query();
        Scene scene = new Scene(root, 720, 1280);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass(){
    }
    
    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     *
     */
    @Before
    public void setUp() {
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    @Test
    public void testComponents() {
        //Checking that all components are there
        for (javafx.scene.control.Menu menu : bar.getMenus()) {
            assertThat(menu, is(notNullValue()));
        }
    }

    //TODO WIP, find better way to test MenuItem and click on it.
    @Test
    public void testSubMenus() {
        javafx.scene.control.Menu project = findMenu("Project", bar);
        //javafx.scene.control.Menu mlt = findMenu("MLT", bar);
        javafx.scene.control.Menu ask = findMenu("?", bar);


        verifyThat(project.getItems(), is(notNullValue()));
        verifyThat(ask.getItems(), is(notNullValue()));


    }

    private static javafx.scene.control.Menu findMenu(String menuName, MenuBar bar) {
        for (javafx.scene.control.Menu menu : bar.getMenus()) {
            if (menuName.equals(menu.getText())) {
                return menu;
            }
        }
        return null;
    }
}
