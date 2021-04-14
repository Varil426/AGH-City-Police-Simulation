package OSMToGraph;

import de.westnordost.osmapi.map.MapDataParser;
import de.westnordost.osmapi.map.OsmMapDataFactory;
import de.westnordost.osmapi.map.data.Node;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import utils.Haversine;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;


public class FileBasedDOTExportTest {
    public static void main(String[] args) throws IOException {

        // example of overpass data export to DOT file:
        // START
        final String rawDataFile = "exportKrkRaw.osm";
        final String graphExportFile = "KrkGraph.gv";

        // TODO dodawać jakiś konwerter do pliku wejściowego z surowymi danymi
        //  obecnie ręcznie konwertuję w Notepad++ z UTF-8 BOM na UTF-8

        FileInputStream fin = new FileInputStream("src/main/java/OSMToGraph/rawData/" + rawDataFile);
        BufferedInputStream bin = new BufferedInputStream(fin);

        ParsingMapDataHandler dataHandler = new ParsingMapDataHandler();
        OsmMapDataFactory factory = new OsmMapDataFactory();
        MapDataParser mapDataParser = new MapDataParser(dataHandler, factory);
        mapDataParser.parse(bin);

        Graph<Node, ImportedEdge> graph = dataHandler.getGraph();
        ImportedGraphToDOT.exportGraphToFile(graph, "src/main/java/OSMToGraph/exportedGraphs/" + graphExportFile, dataHandler);
        // close the file
        fin.close();
        // END

        // example of calculating the route between two points (the result is a GraphPath)
        // and the distance between these points
        //START
        HashMap<Long, Node> myNodes = dataHandler.getNodesMap();
        AStarShortestPath<Node, ImportedEdge> path = new AStarShortestPath<>(graph, new Haversine.ownHeuristics());
        GraphPath<Node, ImportedEdge> path1 = path.getPath(myNodes.get(3195641657L), myNodes.get(244399516L));
        System.out.println(path1.getEdgeList());
        System.out.println(path1.getWeight());
        //END
    }
}
