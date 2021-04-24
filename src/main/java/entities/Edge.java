package entities;

import utils.Pair;

// TODO do usunięcia najprawdopodobniej

public class Edge {

    private Pair<Node, Node> nodes;

    public Edge(Node startNode, Node endNode) {
        this.nodes = new Pair<>(startNode, endNode);
    }

    public Edge(Pair<Node, Node> nodes) {
        this.nodes = nodes;
    }

    public Pair<Node, Node> getNodes() {
        return nodes;
    }
}
