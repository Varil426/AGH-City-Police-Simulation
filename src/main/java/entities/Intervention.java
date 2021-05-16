package entities;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Intervention extends Incident implements IDrawable {

    private final long duration;
    private final boolean willChangeIntoFiring;
    private final long timeToChange;

    private Patrol patrolSolving;

    public Intervention(double latitude, double longitude) {
        super(latitude, longitude);

        // TODO Random parameters selection
        this.duration = 500;
        this.willChangeIntoFiring = false;
        this.timeToChange = -1;
    }

    public Intervention(double latitude, double longitude, long duration) {
        super(latitude, longitude);
        this.duration = duration;

        this.willChangeIntoFiring = false;
        this.timeToChange = -1;
    }

    public Intervention(double latitude, double longitude, long duration, boolean willChangeIntoFiring, long timeToChange) {
        super(latitude, longitude);
        this.duration = duration;
        this.willChangeIntoFiring = willChangeIntoFiring;
        if (timeToChange < 0) {
            throw new IllegalArgumentException("timeToChange must be greater than or equal to zero");
        }
        this.timeToChange = timeToChange;
    }

    @Override
    public void updateState() {
        super.updateState();
        // TODO
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var oldColor = g.getColor();

        g.setColor(Color.ORANGE);

        final var size = 10;
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));

        var mark = new Ellipse2D.Double((int) (point.getX() - size / 2), (int) (point.getY() - size / 2), size, size);
        g.fill(mark);

        g.setColor(oldColor);
    }

    public Patrol getPatrolSolving() {
        return patrolSolving;
    }

    public void setPatrolSolving(Patrol patrolSolving) {
        this.patrolSolving = patrolSolving;
    }

    public long getDuration() {
        return duration;
    }
}
