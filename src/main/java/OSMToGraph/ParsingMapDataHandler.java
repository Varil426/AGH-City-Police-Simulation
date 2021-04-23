package OSMToGraph;

import de.westnordost.osmapi.map.data.*;
import de.westnordost.osmapi.map.handler.DefaultMapDataHandler;
import de.westnordost.osmapi.map.handler.MapDataHandler;
import entities.District;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import utils.Haversine;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class ParsingMapDataHandler extends DefaultMapDataHandler implements MapDataHandler {

    private final Graph<Node, ImportedEdge> graph = new DefaultDirectedWeightedGraph<>(ImportedEdge.class);

    private final List<Relation> relations = new ArrayList<>();
    private final HashMap<Long, Relation> myRelations = new HashMap<>();

    private final HashMap<Long, List<Long>> waysInRelation = new HashMap<>();

    private final HashMap<Long, Node> myNodes = new HashMap<>();
    private final List<ImportedEdge> myEdges = new ArrayList<>();

    private final HashMap<Long, Way> myWays = new HashMap<>();


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

        myWays.put(way.getId(), way);

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
        List<RelationMember> members = relation.getMembers();
        List<Long> ways = new ArrayList<>();
        for (RelationMember r: members) {
            if (r.getType().name().equals("way") && r.getRole().equals("outer")){
                ways.add(r.getRef());
            }
        }
        waysInRelation.put(relation.getId(), ways);
        relations.add(relation);
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
//                System.out.println("edge has not been added to the graph");
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

    public Double getMaxLatitude() {
        return maxLatitude;
    }

    public Double getMaxLongitude() {
        return maxLongitude;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public List<District> getDistricts(){

        List<District> districts = new ArrayList<>();

        for (Relation r:relations) {
            List<Node> nodes = new ArrayList<>();

            List<Long> ways = waysInRelation.get(r.getId());
            for (Long way: ways) {
                Way way1 = myWays.get(way);
                List<Long> nodeIds = way1.getNodeIds();
                for (Long node: nodeIds) {
                    nodes.add(myNodes.get(node));
                }
            }
            double[] lats = new double[nodes.size()];
            double[] lons = new double[nodes.size()];
            for(int i = 0; i < nodes.size(); ++i) {
                LatLon position = nodes.get(i).getPosition();
                lats[i] = position.getLatitude();
                lats[i] = position.getLatitude();
            }

            SimplePolygon2D simplePolygon2D = new SimplePolygon2D(lats, lons);
            districts.add(new District(r.getId(), r.getTags().get("name"), simplePolygon2D));
        }
        return districts;
    }
}
