package syncgod.menu;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.w3c.dom.Document;
import java.util.ArrayList;


public class HelpMenuTest extends ApplicationTest{

    ArrayList<Node> nodes;
    Parent parent;

    /**
     * For running tests on the JFX application thread.
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
         parent = FXMLLoader.load(getClass()
                .getResource("/fxml/menu/HelpMenu.fxml"));
    }

    /**
     * Populating nodes list
     */
    @Before
    public void setUp(){
        nodes = getAllNodes(parent);
    }

    /**
     * ImageView should not be empty after initialize was called.
     */
    @Test
    public void testImageViewInit() {
        System.out.println("testImageViewInit");

        ImageView testImgView = (ImageView) getNodeById(nodes, "imgView");
        Image image = testImgView.getImage();
        assertThat(image, is(notNullValue()));
    }
    /**
     * WebView should not be empty after init.
     */
    @Test
    public void testWebViewInit() {
        System.out.println("testWebViewInit");

        WebView testWebView = (WebView) getNodeById(nodes, "webView");
        Document document = testWebView.getEngine().getDocument();
        assertThat(document, is(notNullValue()));
    }


    /**
     * Functions necessary to get/test JFX Nodes.
     * Using these could miss the point of testing, idk
     */


    /**
     * get all nodes.
     * @param root Root node
     * @return List of all nodes
     */
    private ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    /**
     * called in getAllNodes function (splitting into two functions improves performance)
     * searches recursively for child nodes, bc Node.lookup / .lookupAll does not provide this functionality
     * @param parent Parent node
     * @param nodes List of discovered nodes
     */
    private void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }

    /**
     * get node by id
     * @param nodes list of nodes
     * @param id node id
     * @return found node or null
     */
    private Node getNodeById(ArrayList<Node> nodes, String id){
        Node node = null;
        for(Node n : nodes){
            if(n.getId() != null && n.getId().equals(id)){
                node = n;
            }
        }
        return node;
    }

}
