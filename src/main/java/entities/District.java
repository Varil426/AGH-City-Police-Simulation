package entities;

import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import utils.Logger;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


public class District implements IDrawable {

    public enum ThreatLevelEnum {
        Safe(1),
        RatherSafe(2),
        NotSafe(3);

        public final int value;

        private ThreatLevelEnum(int value) {
            this.value = value;
        }
    }

    private final Long id;
    private final String name;
    private final Path2D boundaries;
    private final List<Node> allNodesInDistrict = new ArrayList<>();
    private ThreatLevelEnum threatLevel = ThreatLevelEnum.RatherSafe;

    public District(Long id, String name, Path2D boundaries) {
        this.boundaries = boundaries;
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Path2D getBoundaries() {
        return boundaries;
    }

    public String getName() {
        return name;
    }

    public boolean contains(LatLon latLon) {
        return this.boundaries.contains(latLon.getLatitude(), latLon.getLongitude());
    }

    public void addNodeToDistrict(Node node) {
        allNodesInDistrict.add(node);
    }

    public ThreatLevelEnum getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(ThreatLevelEnum threatLevel) {
        if (this.threatLevel != threatLevel) {
            this.threatLevel = threatLevel;
            Logger.getInstance().logNewMessage(name + " district's thread level has been set to " + threatLevel);
        }
    }

    public List<Node> getAllNodesInDistrict() {
        return allNodesInDistrict;
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var oldStroke = g.getStroke();
        g.setStroke(new BasicStroke(2));

        var iterator = boundaries.getPathIterator(null);
        var line = new double[6];
        iterator.currentSegment(line);
        iterator.next();

        Point2D last = mapViewer.convertGeoPositionToPoint(new GeoPosition(line[0], line[1]));

        while (!iterator.isDone()) {
            iterator.currentSegment(line);
            iterator.next();
            var current = mapViewer.convertGeoPositionToPoint(new GeoPosition(line[0], line[1]));

            g.drawLine((int) last.getX(), (int) last.getY(), (int) current.getX(), (int) current.getY());
            last = current;
        }
        g.setStroke(oldStroke);
    }
}
