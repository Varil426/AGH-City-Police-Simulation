package OSMToGraph;

import de.westnordost.osmapi.map.data.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.io.IOException;


public class ImportDOTFileTest {
    public static void main(String[] args) throws IOException {

        //TODO kurwa nie działają krawędzie

        // example of importing a DOT file into a graph:
        final String graphExportFile = "KrkGraph.gv";
        Graph<Node, ImportedEdge> importGraph = new SimpleDirectedGraph<>(ImportedEdge.class);
        ImportedGraphToDOT.importGraph(importGraph, "src/OSMToGraph/exportedGraphs/" + graphExportFile);
    }
}
