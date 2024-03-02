package syncgod.mltparser;

import java.io.IOException;
import java.nio.file.Paths;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import syncgod.pubsub.EventBus;
import syncgod.pubsub.events.LoadLogEvent;


public final class MltReader {

    private static MltData data;

    public static MltData read(String path) {
        data = new MltData(Paths.get(path).toFile().getName());
        Document doc = null;
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fac.newDocumentBuilder();
            doc = builder.parse(path);
        }  catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        if (doc == null) {
            return data;
        }
        
        Element root = doc.getDocumentElement();

        Node sibling = root.getFirstChild().getNextSibling();
        boolean hasNext = true;
        while (hasNext) {
            parseSibling(sibling);
            sibling = sibling.getNextSibling();
            hasNext = sibling != null;
        }

        return data;

    }

    private static void parseSibling(Node sibling) {
        String nodeName = sibling.getNodeName();
        if (Constants.PRODUCER.equals(nodeName)) {
            parseProducer(sibling);
        } else if (Constants.TRACTOR.equals(nodeName)) {
            data.addTractor(parseTractor(sibling));
        } else if (Constants.LOGS.equals(nodeName) && sibling.hasChildNodes()) {
            EventBus.getInstance().post(new LoadLogEvent(sibling.getFirstChild().getTextContent()));
        } else {
            if (!((Constants.PROFILE.equals(nodeName)) || (!nodeName.equals("\t\n")))) {
                System.out.printf("Invalid node type: %s !%n", sibling.getNodeName());
            }
        }
    }

    private static MltTractor parseTractor(Node sibling) {

        MltTractor tractor = new MltTractor(sibling.getAttributes().getNamedItem(Constants.ID)
                .getTextContent());
        Node playlistNode  = sibling.getFirstChild().getFirstChild();

        boolean hasNext = playlistNode != null;
        while (hasNext) {
            if (playlistNode.getNodeName().equals(Constants.PLAYLIST)) {
                tractor.addPlaylist(parsePlaylist(playlistNode));
            }
            playlistNode = playlistNode.getNextSibling();
            hasNext = playlistNode != null;
        }


        Node transitionNode = sibling.getFirstChild().getNextSibling();

        if (transitionNode == null) {
            return tractor;
        }
        
        hasNext = true;
        while (hasNext) {
            if (transitionNode.getNodeName().equals(Constants.TRANSITION)) {
                tractor.addTransition(parseTransition(transitionNode));
            }
            transitionNode = transitionNode.getNextSibling();
            hasNext = transitionNode != null;
        }

        return tractor;
    }

    private static MltTransition parseTransition(Node transitionNode) {
        String in = transitionNode.getAttributes()
                .getNamedItem(Constants.IN).getTextContent();
        String out = transitionNode.getAttributes()
                .getNamedItem(Constants.OUT).getTextContent();
        String trackA = transitionNode.getAttributes()
                .getNamedItem(Constants.A_TRACK).getTextContent();
        String trackB = transitionNode.getAttributes()
                .getNamedItem(Constants.B_TRACK).getTextContent();

        return new MltTransition(in, out, trackA, trackB);
    }

    private static MltPlaylist parsePlaylist(Node playlistNode) {
        String playlistID = playlistNode.getAttributes().getNamedItem(Constants.ID)
                .getTextContent();
        String hide = "";
        if (playlistNode.getAttributes().getNamedItem(Constants.HIDE) != null) {
            hide = playlistNode.getAttributes().getNamedItem(Constants.HIDE).getTextContent();
        }
        MltPlaylist playlist = new MltPlaylist(playlistID, hide);

        Node entryNode = playlistNode.getFirstChild();

        boolean hasNext = true;
        while (hasNext) {
            if (entryNode.getNodeName().equals(Constants.ENTRY)) {
                playlist.addEntry(parseEntry(entryNode));
            }
            entryNode = entryNode.getNextSibling();
            hasNext = entryNode != null;
        }

        return playlist;
    }

    private static MltEntry parseEntry(Node entryNode) {
        String entryId = entryNode.getAttributes()
                .getNamedItem(Constants.PRODUCER).getTextContent();
        int offset = 0;
        for (int i = 0; i < data.getProducers().size(); i++) {
            if (data.getProducers().get(i).getId().equals(entryId)) {
                offset = Integer.parseInt(data.getProducers().get(i).getOffset());
                break;
            }
        }
        String in = "0";
        if (entryNode.getAttributes().getNamedItem(Constants.IN) != null) {
            in = String.valueOf(Integer.parseInt(entryNode.getAttributes().getNamedItem(Constants.IN).getTextContent()) + offset);
        }
        String out = "0";
        if (entryNode.getAttributes().getNamedItem(Constants.OUT) != null) {
            out = String.valueOf(Integer.parseInt(entryNode.getAttributes().getNamedItem(Constants.OUT).getTextContent()) + offset);
        }
        return new MltEntry(entryId, in, out);
    }

    private static void parseProducer(Node sibling) {
        String producerId = sibling.getAttributes().getNamedItem(Constants.ID).getTextContent();
        String producerPath  = sibling.getTextContent();
        String offset = "0";
        if (sibling.getAttributes().getNamedItem(Constants.OFFSET) != null) {
            offset = sibling.getAttributes().getNamedItem(Constants.OFFSET).getTextContent();
        }
        MltProducer producer = new MltProducer(producerId, producerPath,  offset);
        data.addProducer(producer);
    }

}
