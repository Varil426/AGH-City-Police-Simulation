package OSMToGraph;

import org.jgrapht.graph.DefaultEdge;

// the class of our "personalized" edge
public class ImportedEdge extends DefaultEdge {

    Long sourceNode, targetNode, distance;

    public ImportedEdge(Long source, Long target) {
        this.sourceNode = source;
        this.targetNode = target;
    }

    public ImportedEdge(Long source, Long target, Long distance) {
        this(source, target);
        this.distance = distance;
    }

    public Object getSource() {
        return this.sourceNode;
    }

    public Object getTarget() {
        return this.targetNode;
    }

    public Object getDistance() {
        return this.distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return this.sourceNode + "->" + this.targetNode;
    }
}