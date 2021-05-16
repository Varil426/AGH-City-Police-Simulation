package entities;

import World.World;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public abstract class Incident extends Entity implements IEvent, IDrawable {

    protected long timeOfLastUpdate;
    private long startTime;

    public Incident() {
        startTime = World.getInstance().getSimulationTimeLong();
        timeOfLastUpdate = startTime;
    }

    public Incident(double latitude, double longitude) {
        super(latitude,longitude);
        startTime = World.getInstance().getSimulationTimeLong();
        timeOfLastUpdate = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    protected void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isActive() {
        return World.getInstance().getSimulationTime() >= startTime;
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        if (isActive()) {
            var oldColor = g.getColor();

            g.setColor(new Color(255,0,0, 175));

            final var size = 10;
            var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));

            var mark = new Ellipse2D.Double((int) (point.getX() - size / 2), (int) (point.getY() - size / 2), size, size);
            g.fill(mark);

            g.setColor(oldColor);
        }
    }
}
