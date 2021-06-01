package OSMToGraph;

import de.westnordost.osmapi.map.data.Node;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.IOException;


public class ImportDOTFileTest {
    public static void main(String[] args) throws IOException {

        //TODO nie działają krawędzie

        // example of importing a DOT file into a graph:
        final String graphExportFile = "KrkGraph.gv";
        Graph<Node, ImportedEdge> importGraph = new SimpleDirectedWeightedGraph<>(ImportedEdge.class);
        ImportedGraphToDOT.importGraph(importGraph, "OSMToGraph/exportedGraphs/" + graphExportFile);
    }
}
