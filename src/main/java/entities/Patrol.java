package entities;

import de.westnordost.osmapi.map.data.LatLon;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Patrol extends Entity implements IAgent, IDrawable {

    public enum State {
        // TODO
    }

    public class Action {

    }

    private final double baseTransferSpeed;
    private final double basePatrollingSpeed;
    private final double basePrivilegedSpeed;

    // TODO Add to constructors setting value for this
    private Long timeOfLastMove;

    private State state;
    private Action action;

    public Patrol() {
        // TODO Change default values
        baseTransferSpeed = 0;
        basePatrollingSpeed = 0;
        basePrivilegedSpeed = 0;
    }

    public Patrol(double latitude, double longitude) {
        this();
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public Patrol(LatLon position) {
        this(position.getLatitude(), position.getLongitude());
    }

    public Patrol(double x, double y, double baseTransferSpeed, double basePatrollingSpeed, double basePrivilegedSpeed) {
        this.setLatitude(x);
        this.setLongitude(y);
        this.basePatrollingSpeed = basePatrollingSpeed;
        this.baseTransferSpeed = baseTransferSpeed;
        this.basePrivilegedSpeed = basePrivilegedSpeed;
    }

    public double getSpeed() {
        // TODO - based on state
        throw new UnsupportedOperationException();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getTimeSinceLastActive() {
        // TODO Calc based on world state
        throw  new UnsupportedOperationException();
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var oldColor = g.getColor();
        g.setColor(Color.MAGENTA);

        final var size = 10;
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));

        var mark = new Ellipse2D.Double((int)(point.getX() - size/2), (int)(point.getY() - size/2), size, size);
        g.fill(mark);

        g.setColor(oldColor);
    }
}
