package OSMToGraph;

import de.westnordost.osmapi.map.data.Node;
import org.jgrapht.io.ComponentNameProvider;
import utils.Haversine;


public class ElementNameProvider<Object> implements ComponentNameProvider<Object> {

    private final ParsingMapDataHandler nodesMapProvider;

    public ElementNameProvider(ParsingMapDataHandler nodesMapProvider) {
        this.nodesMapProvider = nodesMapProvider;
    }

    public String getName(Object component) {
        if (component == null)
            return "";
        if (component instanceof Long) {
            return component + "";
        } else if (component instanceof ImportedEdge) {

            ImportedEdge edge = (ImportedEdge) component;

            Node n1 = nodesMapProvider.getNodesMap().get(edge.sourceNode);
            Node n2 = nodesMapProvider.getNodesMap().get(edge.targetNode);

            double dist = Haversine.distance(n1.getPosition().getLatitude(),
                    n1.getPosition().getLongitude(),
                    n2.getPosition().getLatitude(),
                    n2.getPosition().getLongitude());

            dist *= 100000;
            dist = Math.round(dist);
            return (dist / 100) + "";
        } else
            return component.toString();

    }

}

