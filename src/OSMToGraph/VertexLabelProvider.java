package OSMToGraph;

import de.westnordost.osmapi.map.data.LatLon;
import org.jgrapht.io.ComponentNameProvider;


public class VertexLabelProvider<Object> implements ComponentNameProvider<Object> {

    private final ParsingMapDataHandler nodesMapProvider;

    public VertexLabelProvider(ParsingMapDataHandler nodesMapProvider) {
        this.nodesMapProvider = nodesMapProvider;
    }

    public String getName(Object component) {
        if (component == null)
            return "";
        if (component instanceof Long) {
            Long node = (Long) component;
            LatLon position = nodesMapProvider.getNodesMap().get(node).getPosition();
            return position.getLatitude() + ";" + position.getLongitude();
        }
        return component.toString();
    }
}

