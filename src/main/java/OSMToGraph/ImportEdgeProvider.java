package OSMToGraph;

import de.westnordost.osmapi.map.data.Node;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.EdgeProvider;

import java.util.Map;


public class ImportEdgeProvider implements EdgeProvider<Node, ImportedEdge> {

    // TODO nie działa

    @Override
    public ImportedEdge buildEdge(Node node, Node v1, String s, Map<String, Attribute> map) {
        if (s == null) s = "0";
        new DefaultEdge();
        System.out.println("edge:" + node.getId() + " xd " + v1.getId() + " xd " + s + " xd " + map.toString());
        return new OSMToGraph.ImportedEdge(node.getId(), v1.getId(), Long.parseLong(s));
    }
}

