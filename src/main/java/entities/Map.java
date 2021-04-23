package entities;

import OSMToGraph.ImportedEdge;
import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.Node;
import org.jgrapht.Graph;

import java.util.HashMap;
import java.util.List;

public class Map {

    private final Graph<Node, ImportedEdge> graph;
    private final HashMap<Long, Node> myNodes;
    private final BoundingBox boundingBox;
    private final List<District> districts;

    public Map(Graph<Node, ImportedEdge> graph, HashMap<Long, Node> myNodes, BoundingBox boundingBox, List<District> districts) {
        this.graph = graph;
        this.myNodes = myNodes;
        this.boundingBox = boundingBox;
        this.districts = districts;
    }

    public HashMap<Long, Node> getMyNodes() {
        return myNodes;
    }

    public Graph<Node, ImportedEdge> getGraph() {
        return graph;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public List<District> getDistricts() {
        return districts;
    }
}
