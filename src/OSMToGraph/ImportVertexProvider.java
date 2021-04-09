package OSMToGraph;

import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import de.westnordost.osmapi.map.data.OsmLatLon;
import de.westnordost.osmapi.map.data.OsmNode;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.VertexProvider;

import java.util.Map;


public class ImportVertexProvider implements VertexProvider<Node> {

    public Node buildVertex(String s, Map<String, Attribute> map) {
        System.out.println(s);
        String label = map.get("label").getValue();
        String[] split = label.split(";");
        LatLon latlon = new OsmLatLon(Double.parseDouble(split[0]), Double.parseDouble(split[0]));

        return new OsmNode(Long.parseLong(s), 1, latlon, null);
    }
}

