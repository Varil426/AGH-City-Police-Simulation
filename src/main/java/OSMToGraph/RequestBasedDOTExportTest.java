package OSMToGraph;

import de.westnordost.osmapi.map.MapDataParser;
import de.westnordost.osmapi.map.OsmMapDataFactory;
import de.westnordost.osmapi.map.data.Node;
import org.jgrapht.Graph;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestBasedDOTExportTest {
    public static void main(String[] args) throws IOException {

        // example of request to overpass-api (and data export to DOT file):

        final String graphExportFile = "KrkGraph2.gv";
//        area["admin_level"=6][name="Kraków"]->.a;(way(area.a)["highway"~"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|motorway_link|trunk_link|primary_link|secondary_link|tertiary_link|living_street|service|pedestrian|track|road)$"]["crossing"!~"."]["name"];);out meta;>;out meta qt;
        final String query = "area[\"admin_level\"=6][name=\"Kraków\"]->.a;(way(area.a)[\"highway\"~\"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|motorway_link|trunk_link|primary_link|secondary_link|tertiary_link|living_street|service|pedestrian|track|road)$\"][\"crossing\"!~\".\"][\"name\"];);out meta;>;out meta qt;";

        URL url = new URL("https://overpass-api.de/api/interpreter");
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestMethod("POST");
        urlConn.setDoOutput(true);
        urlConn.setRequestProperty("data", query);
        urlConn.setRequestProperty("Content-Length", Integer.toString(query.length()));
        urlConn.getOutputStream().write(query.getBytes(StandardCharsets.UTF_8));

        BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConn.getInputStream());
        ParsingMapDataHandler dataHandler = new ParsingMapDataHandler();
        OsmMapDataFactory factory = new OsmMapDataFactory();
        MapDataParser mapDataParser = new MapDataParser(dataHandler, factory);
        mapDataParser.parse(bufferedInputStream);

        Graph<Node, ImportedEdge> graph = dataHandler.getGraph();
        ImportedGraphToDOT.exportGraphToFile(graph, "src/main/java/OSMToGraph/exportedGraphs/" + graphExportFile, dataHandler);
    }
}
