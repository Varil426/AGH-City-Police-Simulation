package entities;

import java.util.ArrayList;
import java.util.List;

// TODO do usunięcia najprawdopodobniej

public class Node extends Entity {
    private List<Edge> edges;

    public Node(double x, double y) {
        super(x, y);
    }

    public  Node(double x, double y, List<Edge> edges) {
        super(x,y);
        this.edges = edges;
    }

    public List<Edge> getAllEdges() {
        return new ArrayList<>(edges);
    }

    public boolean addEdge(Edge edge) {
        return edges.add(edge);
    }

    public boolean removeEdge(Edge edge) {
        return edges.remove(edge);
    }

    public Edge removeEdgeAtIndex(int index) {
        return edges.remove(index);
    }
}
