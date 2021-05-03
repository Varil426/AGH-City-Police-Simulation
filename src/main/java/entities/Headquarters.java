package entities;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class Headquarters extends Entity implements IDrawable {

    private List<Patrol> patrols = new ArrayList<>();
    private List<Incident> incidents = new ArrayList<>();

    public Headquarters(double latitude, double longitude) {
        super(latitude,longitude);
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));


        var mark = new Ellipse2D.Double((int)point.getX(), (int)point.getY(), 10, 10);

        g.setColor(Color.BLUE);
        g.fill(mark);
    }

    // TODO Lists methods
}
