package OSMToGraph;

import de.westnordost.osmapi.map.data.*;
import de.westnordost.osmapi.map.handler.DefaultMapDataHandler;
import de.westnordost.osmapi.map.handler.MapDataHandler;
import entities.District;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import utils.Haversine;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class ParsingMapDataHandler extends DefaultMapDataHandler implements MapDataHandler {

    private final Graph<Node, ImportedEdge> graph = new DefaultDirectedWeightedGraph<>(ImportedEdge.class);

    private final List<Relation> relations = new ArrayList<>();

    private final HashMap<Long, List<Long>> waysInRelation = new HashMap<>();

    private final HashMap<Long, Node> myNodes = new HashMap<>();
    private final List<ImportedEdge> myEdges = new ArrayList<>();

    private final HashMap<Long, Way> myWays = new HashMap<>();


    private Double minLatitude;
    private Double maxLatitude;
    private Double minLongitude;
    private Double maxLongitude;

    public static int findNearestNode(Node node, List<Node> nodes) {
        double distance = Double.MAX_VALUE;
        int id = 0;
        double latitude = node.getPosition().getLatitude();
        double longitude = node.getPosition().getLongitude();
        for (int i = 0; i < nodes.size(); i++) {
            double tmpDistance = Haversine.distance(latitude, longitude, nodes.get(i).getPosition().getLatitude(), nodes.get(i).getPosition().getLongitude());
            if (tmpDistance < distance) {
                distance = tmpDistance;
                id = i;
            }
        }
        return id;
    }

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
        for (RelationMember r : members) {
            if (r.getType().name().equals("WAY") && r.getRole().equals("outer")) {
                ways.add(r.getRef());
            }
        }
        waysInRelation.put(relation.getId(), ways);
        relations.add(relation);
    }

    public Graph<Node, ImportedEdge> getGraph() {
        // adds all edges to the graph:
        for (ImportedEdge edge : myEdges) {

            Node nodeS = myNodes.get(edge.sourceNodeID);
            Node nodeT = myNodes.get(edge.targetNodeID);

            // calculates the distance (edge weight):
            double dist = Haversine.distance(nodeS.getPosition().getLatitude(),
                    nodeS.getPosition().getLongitude(),
                    nodeT.getPosition().getLatitude(),
                    nodeT.getPosition().getLongitude());
            dist *= 100;
            dist = Math.round(dist);
            dist /= 100;

            edge.setDistance(dist);

            // adds an edge and its weight:
            boolean b = graph.addEdge(nodeS, nodeT, edge);
            if (b) {
                graph.setEdgeWeight(edge, dist);
            } else {
                // TODO kilka krawędzi dla Krakowa się nie dodają - nie mam pojęcia dlaczego
                //  hint: jak jest graf ważony to dodaje się do pliku słowo kluczowe "strict"
//                System.out.println("edge has not been added to the graph "+nodeS.getId()+" "+nodeT.getId());
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

    public List<District> getDistricts() {

        List<District> districts = new ArrayList<>();

        for (Relation r : relations) {
            List<Node> unorderedNodes = new ArrayList<>();
            List<Long> ways = waysInRelation.get(r.getId());

            for (Long wayID : ways) {
                Way way = myWays.get(wayID);
                List<Long> nodeIds = way.getNodeIds();
                for (Long node : nodeIds) {
                    unorderedNodes.add(myNodes.get(node));
                }
            }

            unorderedNodes = new ArrayList<>(new HashSet<>(unorderedNodes));

            List<Node> sortedNodes = new ArrayList<>();
            Node removed1 = unorderedNodes.remove(0);
            sortedNodes.add(removed1);
            int size = unorderedNodes.size();
            for (int i = 0; i < size; i++) {
                Node node = sortedNodes.get(sortedNodes.size() - 1);
                int nearestNode = findNearestNode(node, unorderedNodes);
                sortedNodes.add(unorderedNodes.remove(nearestNode));
            }

            double[] lats = new double[sortedNodes.size()];
            double[] lons = new double[sortedNodes.size()];
            for (int i = 0; i < sortedNodes.size(); ++i) {
                LatLon position = sortedNodes.get(i).getPosition();
                lats[i] = position.getLatitude();
                lons[i] = position.getLongitude();
            }

            Path2D path = new Path2D.Double();
            path.moveTo(lats[0], lons[0]);
            for (int i = 1; i < lats.length; ++i) {
                path.lineTo(lats[i], lons[i]);
            }
            path.closePath();
            districts.add(new District(r.getId(), r.getTags().get("name"), path));
        }
        return districts;
    }
}
