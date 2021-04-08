package OSMToGraph;

import org.jgrapht.graph.DefaultEdge;

public class ImportedEdge extends DefaultEdge {

    Long sourceNode, targetNode;

    public ImportedEdge(Long source, Long target) {
        this.sourceNode = source;
        this.targetNode = target;
    }

    public Object getSource() {
        return this.sourceNode;
    }

    public Object getTarget() {
        return this.targetNode;
    }

    @Override
    public String toString() {
        return this.sourceNode + "->" + this.targetNode;
    }
}