package OSMToGraph;

import de.westnordost.osmapi.map.data.BoundingBox;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.Relation;
import de.westnordost.osmapi.map.data.Way;
import de.westnordost.osmapi.map.handler.DefaultMapDataHandler;
import de.westnordost.osmapi.map.handler.MapDataHandler;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import utils.Haversine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParsingMapDataHandler extends DefaultMapDataHandler implements MapDataHandler {

    private final Graph<Node, ImportedEdge> graph = new DefaultDirectedWeightedGraph<>(ImportedEdge.class);

    private final HashMap<Long, Node> myNodes = new HashMap<>();
    private final List<ImportedEdge> myEdges = new ArrayList<>();
    private Double minLatitude;
    private Double maxLatitude;
    private Double minLongitude;
    private Double maxLongitude;

    public HashMap<Long, Node> getNodesMap() {
        return this.myNodes;
    }

    @Override
    public void handle(BoundingBox bounds) {
        super.handle(bounds);
    }

    @Override
    public void handle(Node node) {
        super.handle(node);

        // counts the range of coordinates (will be useful for conversion):
        if (minLatitude == null) minLatitude = node.getPosition().getLatitude();
        if (maxLatitude == null) maxLatitude = node.getPosition().getLatitude();
        if (minLongitude == null) minLongitude = node.getPosition().getLongitude();
        if (maxLongitude == null) maxLongitude = node.getPosition().getLongitude();

        double latitude = node.getPosition().getLatitude();
        double longitude = node.getPosition().getLongitude();
        if (latitude > maxLatitude) maxLatitude = latitude;
        else if (latitude < minLatitude) minLatitude = latitude;
        if (longitude > maxLongitude) maxLongitude = longitude;
        else if (longitude < minLongitude) minLongitude = longitude;

        // add a node to the graph and myNodes:
        graph.addVertex(node);
        myNodes.put(node.getId(), node);
    }

    @Override
    public void handle(Way way) {
        super.handle(way);

        long previousNodeID = -1;
        boolean oneway = false;

        // check if road is oneway:
        if (way.getTags().containsKey("oneway")) {
            if (way.getTags().get("oneway").equals("yes")) {
                oneway = true;
            }
        }

        // loop creating all edges in a given path:
        for (long nodeID : way.getNodeIds()) {
            if (previousNodeID == -1) {
                previousNodeID = nodeID;
                continue;
            }
            // previously unmet node found in a way:
            if (!myNodes.containsKey(previousNodeID)) {
                myNodes.put(previousNodeID, null);
            }

            // previously unmet node found in a way:
            if (!myNodes.containsKey(nodeID)) {
                myNodes.put(nodeID, null);
            }

            // Adding edge from node ID: previousNodeID to: nodeID
            ImportedEdge edge = new ImportedEdge(previousNodeID, nodeID);
            myEdges.add(edge);
            if (!oneway) {
                // Adding edge from node ID: nodeID to: previousNodeID
                edge = new ImportedEdge(nodeID, previousNodeID);
                myEdges.add(edge);
            }
            previousNodeID = nodeID;
        }
    }

    @Override
    public void handle(Relation relation) {
        super.handle(relation);
    }

    public Graph<Node, ImportedEdge> getGraph() {

        // adds all edges to the graph:
        for (ImportedEdge edge : myEdges) {

            Node nodeS = myNodes.get(edge.sourceNode);
            Node nodeT = myNodes.get(edge.targetNode);

            // calculates the distance (edge weight):
            double dist = Haversine.distance(nodeS.getPosition().getLatitude(),
                    nodeS.getPosition().getLongitude(),
                    nodeT.getPosition().getLatitude(),
                    nodeT.getPosition().getLongitude());
            dist *= 100;
            dist = Math.round(dist);
            dist /= 100;

            // adds an edge and its weight:
            boolean b = graph.addEdge(nodeS, nodeT, edge);
            if (b){
                graph.setEdgeWeight(edge, dist);
            }
            else {
                // TODO kilka krawędzi dla Krakowa się nie dodają - nie mam pojęcia dlaczego
                //  hint: jak jest graf ważony to dodaje się do pliku słowo kluczowe "strict"
                System.out.println("edge has not been added to the graph");
            }
        }
        return graph;
    }

    public Double getMinLatitude() {
        return minLatitude;
    }

    public Double getMinLongitude() {
        return minLongitude;
    }
}
