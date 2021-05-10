package entities;

import World.World;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import org.apache.commons.lang3.NotImplementedException;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import utils.Haversine;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Patrol extends Entity implements IAgent, IDrawable {

    private final double basePatrollingSpeed;
    private final double baseTransferSpeed;
    private final double basePrivilegedSpeed;
    // TODO Add to constructors setting value for this
    private double timeOfLastMove;
    private State state;
    private Action action;

    public Patrol() {
        // TODO Change default values
        basePatrollingSpeed = 40;
        baseTransferSpeed = 60;
        basePrivilegedSpeed = 80;
        this.timeOfLastMove = World.getInstance().getSimulationTimeLong();
    }

    public Patrol(double latitude, double longitude) {
        this();
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.timeOfLastMove = World.getInstance().getSimulationTimeLong();
    }

    public Patrol(LatLon position) {
        this(position.getLatitude(), position.getLongitude());
        this.timeOfLastMove = World.getInstance().getSimulationTimeLong();
    }

    public Patrol(double x, double y, double baseTransferSpeed, double basePatrollingSpeed, double basePrivilegedSpeed) {
        this.setLatitude(x);
        this.setLongitude(y);
        this.basePatrollingSpeed = basePatrollingSpeed;
        this.baseTransferSpeed = baseTransferSpeed;
        this.basePrivilegedSpeed = basePrivilegedSpeed;
        this.timeOfLastMove = World.getInstance().getSimulationTimeLong();
    }

    public void updateStateSelf() throws Exception {
        // TODO
        //  - sprawdzenie czy jest na miejscu
        //  - nwm co jeszcze

        if (state == State.PATROLLING) {
            if (action == null) {
                // if action is not defined then patrol goes to HQ
                Entity HQ = World.getInstance().getAllEntities().stream().filter(x -> x instanceof Headquarters).collect(Collectors.toList()).get(0);
                action = new Transfer(World.getInstance().getSimulationTimeLong(), new Point(HQ.getPosition().getLatitude(), HQ.getPosition().getLongitude()));
            } else {
                if (action instanceof Transfer) {
                    // if pathNodeList is empty, it draws a new patrol target
                    if (((Transfer) action).pathNodeList.size() == 0) {
                        var world = World.getInstance();
                        Random generator = new Random();
                        var node = (Node) world.getMap().getMyNodes().values().toArray()[generator.nextInt(world.getMap().getMyNodes().size())];
                        action = new Transfer(World.getInstance().getSimulationTimeLong(), new Point(node.getPosition().getLatitude(), node.getPosition().getLongitude()));
                    }
                } else {
                    throw new Exception("Action should be 'Transfer' and it is not");
                }
            }
        }
    }

    public void performAction() throws Exception {
        // TODO

        double simulationTime = World.getInstance().getSimulationTime();
        switch (state) {
            case PATROLLING -> {
                // speed changed from km/h to m/s
                double traveledDistance = getSpeed() * 1000 / 3600 * Math.abs(simulationTime - timeOfLastMove);
                if (action instanceof Transfer) {

                    double distanceToNearestNode = getDistanceToNearestNode();
                    while (distanceToNearestNode < traveledDistance) {
                        if (((Transfer) action).pathNodeList.size() == 1) break;

                        traveledDistance -= distanceToNearestNode;
                        Node removedNode = ((Transfer) action).pathNodeList.remove(0);
                        setPosition(removedNode.getPosition());
                        distanceToNearestNode = getDistanceToNearestNode();
                    }

                    LatLon nearestNodePosition = ((Transfer) action).pathNodeList.get(0).getPosition();
                    if (distanceToNearestNode > traveledDistance) {
                        double distanceFactor = traveledDistance / distanceToNearestNode;
                        setLatitude((getLatitude() + (nearestNodePosition.getLatitude() - getLatitude()) * distanceFactor));
                        setLongitude((getLongitude() + (nearestNodePosition.getLongitude() - getLongitude()) * distanceFactor));
                    } else {
                        setPosition(nearestNodePosition);
                        ((Transfer) action).pathNodeList.remove(0);
                    }
                } else {
                    throw new Exception("Action should be 'Transfer' and it is not");
                }
            }
            case TRANSFER_TO_INTERVENTION -> {
                throw new NotImplementedException("new implemented");
            }
            case TRANSFER_TO_FIRING -> {
                throw new NotImplementedException("new implemented");
            }
            case INTERVENTION -> {
                throw new NotImplementedException("new implemented");
            }
            case FIRING -> {
                throw new NotImplementedException("new implemented");
            }
            case NEUTRALIZED -> {
                throw new NotImplementedException("new implemented");
            }
            default -> {
                throw new Exception("Illegal state");
            }
        }
        timeOfLastMove = simulationTime;
    }

    private double getDistanceToNearestNode() throws Exception {
        if (((Transfer) action).pathNodeList.size() == 0) throw new Exception("pathNodeList is empty!");

        LatLon sourceNodePosition = ((Transfer) action).pathNodeList.get(0).getPosition();
        return Haversine.distance(getLatitude(), getLongitude(), sourceNodePosition.getLatitude(), sourceNodePosition.getLongitude());
    }

    public double getSpeed() {
        switch (state) {
            case PATROLLING -> {
                return basePatrollingSpeed;
            }
            case TRANSFER_TO_INTERVENTION -> {
                return baseTransferSpeed;
            }
            case TRANSFER_TO_FIRING -> {
                return basePrivilegedSpeed;
            }
            default -> {
                System.out.println("The patrol is currently not moving");
                return basePatrollingSpeed;
            }
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getTimeSinceLastActive() {
        // TODO Calc based on world state
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var oldColor = g.getColor();
        //TODO hejka
        g.setColor(Color.MAGENTA);

        final var size = 10;
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));

        var mark = new Ellipse2D.Double((int) (point.getX() - size / 2), (int) (point.getY() - size / 2), size, size);
        g.fill(mark);

        g.setColor(oldColor);
    }

    public enum State {
        PATROLLING,
        TRANSFER_TO_INTERVENTION,
        TRANSFER_TO_FIRING,
        INTERVENTION,
        FIRING,
        NEUTRALIZED
    }

    public class Action {
        public Long startTime;

        public Action(Long startTime) {
            this.startTime = startTime;
        }
    }

    public class Transfer extends Action {
        public Entity target;
        public ArrayList<Node> pathNodeList;

        public Transfer(Long startTime, Entity target) {
            super(startTime);
            this.target = target;
            this.pathNodeList = (ArrayList<Node>) World.getInstance().getMap().getPathNodeList(getLatitude(), getLongitude(), target.getLatitude(), target.getLongitude());
        }

    }

    public class IncidentParticipation extends Action {
        public Incident incident;

        public IncidentParticipation(Long startTime, Incident incident) {
            super(startTime);
            this.incident = incident;
        }
    }
}
