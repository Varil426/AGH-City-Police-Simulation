package OSMToGraph;

import de.westnordost.osmapi.map.MapDataParser;
import de.westnordost.osmapi.map.OsmMapDataFactory;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.OsmLatLon;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import utils.Haversine;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class ImportGraphFromRawData {

    static String rawDataFile = "exportKrkRaw.osm";
    static String graphExportFile = "KrkGraph.gv";
    static String rawDataPath = "src/main/java/OSMToGraph/rawData/";
    static String graphExportPath = "src/main/java/OSMToGraph/exportedGraphs/";
    // area["admin_level"=6][name="Kraków"]->.a;(way(area.a)["highway"~"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|motorway_link|trunk_link|primary_link|secondary_link|tertiary_link|living_street|service|pedestrian|track|road)$"]["crossing"!~"."]["name"];);out meta;>;out meta qt;
    static String query = "area[\"admin_level\"=6][name=\"Kraków\"]->.a;(way(area.a)[\"highway\"~\"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|motorway_link|trunk_link|primary_link|secondary_link|tertiary_link|living_street|service|pedestrian|track|road)$\"][\"crossing\"!~\".\"][\"name\"];);out meta;>;out meta qt;";
    static String apiURL = "https://overpass-api.de/api/interpreter";

    public static void main(String[] args) throws IOException {

        // example of file-based data handling: (It takes about 2 seconds)
//        ParsingMapDataHandler dataHandler = handleRawDataFromFile();

        // example of request-based data handling: (It takes about 16 seconds)
        ParsingMapDataHandler dataHandler = handleRawDataFromRequest();

        Graph<Node, ImportedEdge> graph = dataHandler.getGraph();

        // exporting the graph to a DOT file:
        ImportedGraphToDOT.exportGraphToFile(graph, graphExportPath + graphExportFile, dataHandler);

        // example of calculating the route between two points (the result is a GraphPath)
        // and the distance between these points
        //START
        HashMap<Long, Node> myNodes = dataHandler.getNodesMap();
        AStarShortestPath<Node, ImportedEdge> path = new AStarShortestPath<>(graph, new Haversine.ownHeuristics());
        GraphPath<Node, ImportedEdge> path1 = path.getPath(myNodes.get(3195641657L), myNodes.get(244399516L));
        System.out.println(path1.getEdgeList());
        System.out.println(path1.getWeight());
        //END

        // an example of searching for the nearest node for a selected point
        //START
        LatLon latlon = new OsmLatLon(80.001769, 100.8174569);
        Long nearestNodeId = findNearestNode(latlon, myNodes);
        System.out.println(nearestNodeId);
        //END
    }

    public static ParsingMapDataHandler handleRawDataFromFile() throws IOException {
        return handleRawDataFromFile(rawDataPath + rawDataFile);
    }

    public static ParsingMapDataHandler handleRawDataFromFile(String rawDataFilePath) throws IOException {
        InputStream fin = new FileInputStream(rawDataFilePath);
        ParsingMapDataHandler dataHandler = handleRawData(fin);
        // close the file:
        fin.close();
        return dataHandler;
    }

    public static ParsingMapDataHandler handleRawDataFromRequest() throws IOException {
        return handleRawDataFromRequest(apiURL, query);
    }

    public static ParsingMapDataHandler handleRawDataFromRequest(String apiURL, String query) throws IOException {
        HttpURLConnection urlConn = makeRequest(apiURL, query);
        InputStream inputStream = urlConn.getInputStream();
        return handleRawData(inputStream);
    }

    public static ParsingMapDataHandler handleRawData(InputStream inputStream) throws IOException {

        // TODO dodać jakiś konwerter do pliku wejściowego z surowymi danymi
        //  obecnie ręcznie konwertuję w Notepad++ z UTF-8 BOM na UTF-8

        BufferedInputStream bin = new BufferedInputStream(inputStream);
        ParsingMapDataHandler dataHandler = new ParsingMapDataHandler();
        OsmMapDataFactory factory = new OsmMapDataFactory();
        MapDataParser mapDataParser = new MapDataParser(dataHandler, factory);
        mapDataParser.parse(bin);

        return dataHandler;
    }

    public static HttpURLConnection makeRequest(String apiURL, String query) throws IOException {
        URL url = new URL(apiURL);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestMethod("POST");
        urlConn.setDoOutput(true);
        urlConn.setRequestProperty("data", query);
        urlConn.setRequestProperty("Content-Length", Integer.toString(query.length()));
        urlConn.getOutputStream().write(query.getBytes(StandardCharsets.UTF_8));
        return urlConn;
    }

    public static Long findNearestNode(LatLon point, HashMap<Long, Node> myNodes) {
        double distance = Double.MAX_VALUE;
        Long id = null;
        for (Map.Entry<Long, Node> me : myNodes.entrySet()) {
            LatLon nodePosition = me.getValue().getPosition();
            double tmpDistance = Haversine.distance(point.getLatitude(), point.getLongitude(), nodePosition.getLatitude(), nodePosition.getLongitude());
            if (tmpDistance < distance) {
                distance = tmpDistance;
                id = me.getValue().getId();
            }
        }
        System.out.println(distance);
        return id;
    }
}
