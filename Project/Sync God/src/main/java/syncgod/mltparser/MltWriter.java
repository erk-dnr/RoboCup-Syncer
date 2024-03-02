package syncgod.mltparser;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import syncgod.config.Config;
import syncgod.config.ConfigValue;

public final class MltWriter {

    private static Document document;
    private static Element root;
    private static int tractorCount = 0;
    private static boolean writeProjectFile = false;


    public static void writeDocument(MltData data, List<String> log, boolean isProjectFile) {

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        writeProjectFile = isProjectFile;
        
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }

        document = documentBuilder.newDocument();
        document.setXmlVersion("1.0");
        root = document.createElement(Constants.MLT);
        document.appendChild(root);
        root.appendChild(addProfile());
        addProducers(data.getProducers());
        addTractors(data.getTractors());
        if ((isProjectFile) && !log.isEmpty()) {
            addLogPaths(log);
        }

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(data.getFileName()));
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private static void addLogPaths(List<String> log) {
        Element xmlLog = document.createElement(Constants.LOGS);
        log.forEach(s -> {
            Element logPath = document.createElement(Constants.LOG);
            logPath.setTextContent(s);
            xmlLog.appendChild(logPath);
        });
        root.appendChild(xmlLog);
    }


    private static void addTractors(List<MltTractor> tractors) {
        tractors.stream()
                .map((MltTractor tractor) -> addTractor(tractor, String.valueOf(tractorCount)))
                .forEach(newChild -> root.appendChild(newChild));
    }


    private static Element addProfile() {
        Element xmlProfile = document.createElement(Constants.PROFILE);
        xmlProfile.setAttribute(Constants.WIDTH, Config.getAsString(ConfigValue.VideoWidth));
        xmlProfile.setAttribute(Constants.HEIGHT, Config.getAsString(ConfigValue.VideoHeight));
        xmlProfile.setAttribute(Constants.DISPLAY_ASPECT_DEN, "9");
        xmlProfile.setAttribute(Constants.DISPLAY_ASPECT_NUM, "16"); // TODO: IN CONFIG ADDEN
        xmlProfile.setAttribute(Constants.FRAME_RATE_DEN, "1");
        xmlProfile.setAttribute(Constants.FRAME_RATE_NUM, Config.getAsString(ConfigValue.Fps));
        xmlProfile.setAttribute(Constants.PROGRESSIVE, "1");
        xmlProfile.setAttribute(Constants.SAMPLE_ASPECT_DEN, "1");
        xmlProfile.setAttribute(Constants.SAMPLE_ASPECT_NUM, "1");
        return xmlProfile;
    }

    private static void addProducers(List<MltProducer> producers) {
        for (MltProducer producer : producers) {
            addProducer(producer);
        }
    }

    private static void addProducer(MltProducer producer) {
        Element xmlProducer = document.createElement(Constants.PRODUCER);
        xmlProducer.setAttribute(Constants.ID, producer.getId());
        if (writeProjectFile && (!producer.getOffset().equals(""))) {
            xmlProducer.setAttribute(Constants.OFFSET, producer.getOffset());
        }
        Element property = document.createElement(Constants.PROPERTY);
        property.setAttribute(Constants.NAME, Constants.RESOURCE);
        String path = producer.getProperty();
        property.setTextContent(path);
        xmlProducer.appendChild(property);

        root.appendChild(xmlProducer);
    }

    private static Element addTractor(MltTractor tractor, String id) {
        Element xmlTractor = document.createElement(Constants.TRACTOR);
        xmlTractor.setAttribute(Constants.ID, Constants.TRACTOR + id);
        Element multiTrack = document.createElement((Constants.MULTITRACK));
        xmlTractor.appendChild(multiTrack);
        tractorCount++;
        root.appendChild(xmlTractor);

        List<MltPlaylist> playlists = tractor.getPlaylists();
        IntStream.range(0, tractor.getPlaylists().size())
                .mapToObj(i -> getXmlPlaylist(playlists.get(i), String.valueOf(i)))
                .forEach(multiTrack::appendChild);

        tractor.getTransitions().stream().map(MltWriter::getTransition)
                .forEach(xmlTractor::appendChild);

        return xmlTractor;
    }

    private static Element getTransition(MltTransition transition) {
        Element element = document.createElement(Constants.TRANSITION);
        element.setAttribute(Constants.MLT_SERVICE, Constants.LUMA);
        element.setAttribute(Constants.IN, transition.getIn());
        element.setAttribute(Constants.OUT, transition.getOut());
        element.setAttribute(Constants.A_TRACK, transition.getATrack());
        element.setAttribute(Constants.B_TRACK, transition.getBTrack());
        return element;
    }

    private static Element getXmlEntry(MltEntry entry) {
        Element element = document.createElement(Constants.ENTRY);
        element.setAttribute(Constants.PRODUCER, entry.getProducer());
        NodeList producer = root.getElementsByTagName(Constants.PRODUCER);

        Optional<Node> producerNode = IntStream.range(0, producer.getLength())
                .mapToObj(producer::item).filter(node -> node.getAttributes()
                .getNamedItem(Constants.ID).getTextContent().equals(entry.getProducer()))
                .findFirst();

        int offset = 0;
        if (writeProjectFile) {
            offset = producerNode.map(node -> Integer.parseInt(node.getAttributes()
                    .getNamedItem(Constants.OFFSET)
                    .getTextContent())).orElse(0);
        }
        element.setAttribute(Constants.IN,
                String.valueOf(Integer.parseInt(entry.getIn()) - offset));
        element.setAttribute(Constants.OUT,
                String.valueOf(Integer.parseInt(entry.getOut()) - offset));

        return element;
    }

    private static Element getXmlPlaylist(MltPlaylist playlist, String id) {
        Element element = document.createElement(Constants.PLAYLIST);
        element.setAttribute(Constants.ID,"p" + id);
        element.setAttribute(Constants.HIDE, playlist.getWhatIsHidden());
        playlist.getEntries().stream().map(MltWriter::getXmlEntry).forEach(element::appendChild);
        return element;
    }
}
