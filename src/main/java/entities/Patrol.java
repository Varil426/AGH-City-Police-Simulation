package entities;

import World.World;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import org.apache.commons.lang3.NotImplementedException;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import utils.Haversine;
import utils.Logger;

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

        if (state == State.PATROLLING) {
            if (action == null) {
                // if action is not defined then patrol goes to HQ
                Entity HQ = World.getInstance().getAllEntities().stream().filter(x -> x instanceof Headquarters).collect(Collectors.toList()).get(0);
                action = new Transfer(World.getInstance().getSimulationTimeLong(), new Point(HQ.getPosition().getLatitude(), HQ.getPosition().getLongitude()));
                Logger.getInstance().logNewMessage(this + " action set to " + action.getClass().toString() + " target: " + action.target.toString());
            } else if (action instanceof Transfer) {
                // if pathNodeList is empty, it draws a new patrol target
                if (((Transfer) action).pathNodeList.size() == 0) {
                    drawNewTarget();
                }
            } else {
                throw new Exception("Action should be 'Transfer' and it is not");
            }
        } else if (state == State.TRANSFER_TO_INTERVENTION) {
            // if patrol has reached his destination, patrol changes state to INTERVENTION
            if (action instanceof Transfer) {
                if (((Transfer) action).pathNodeList.size() == 0) {
                    setState(State.INTERVENTION);
                    action = new IncidentParticipation(World.getInstance().getSimulationTimeLong(), (Incident) action.target);
                }
            } else {
                throw new Exception("Action should be 'Transfer' and it is not");
            }
        } else if (state == State.INTERVENTION) {
            if (action.target instanceof Firing){
                setState(State.FIRING);
                ((Firing) action.target).addSolvingPatrol(this);
                action = new IncidentParticipation(World.getInstance().getSimulationTimeLong(), (Incident) action.target);
            }
            // if the duration of the intervention is over, patrol changes state to PATROLLING
            else if (action instanceof IncidentParticipation) {
                if (!(((Intervention) (action).target).isActive())) {
                    World.getInstance().removeEntity((action.target));
                    setState(State.PATROLLING);
                    drawNewTarget();
                }
            } else {
                throw new Exception("Action should be 'IncidentParticipation' and it is not");
            }
        } else if (state == State.TRANSFER_TO_FIRING) {
            // if patrol has reached his destination, patrol changes state to FIRING
            if (action instanceof Transfer) {
                if (((Transfer) action).pathNodeList.size() == 0) {
                    setState(State.FIRING);
                    ((Firing) action.target).removeReachingPatrol(this);
                    ((Firing) action.target).addSolvingPatrol(this);
                    action = new IncidentParticipation(World.getInstance().getSimulationTimeLong(), (Incident) action.target);
                }
            } else {
                throw new Exception("Action should be 'Transfer' and it is not");
            }
        } else if (state == State.FIRING) {
            // when the firing strength drops to zero, patrol changes state to PATROLLING
//            System.out.println(getUniqueID()+" "+state+" "+this.getAction().target+" "+((Firing) action.target).isActive()+" "+((Firing) action.target).getStrength()+" "+((Firing) action.target).getStrength()+" "+((Firing) action.target).getPatrolsSolving().size());
            if (action instanceof IncidentParticipation) {
                if (action.target == null) {
                    setState(State.PATROLLING);
                    drawNewTarget();
                } else if (!((Firing) action.target).isActive()) {
                    World.getInstance().removeEntity((action.target));
                    setState(State.PATROLLING);
                    drawNewTarget();
                }
            } else {
                throw new Exception("Action should be 'IncidentParticipation' and it is not");
            }
        }
    }

    private void drawNewTarget() {
        var world = World.getInstance();
        Random generator = new Random();
        var node = (Node) world.getMap().getMyNodes().values().toArray()[generator.nextInt(world.getMap().getMyNodes().size())];
        action = new Transfer(World.getInstance().getSimulationTimeLong(), new Point(node.getPosition().getLatitude(), node.getPosition().getLongitude()));
        Logger.getInstance().logNewMessage(this + " action set to " + action.getClass().toString() + " target: " + action.target.toString());
    }

    public void performAction() throws Exception {
        // TODO

        double simulationTime = World.getInstance().getSimulationTime();
        switch (state) {
            case PATROLLING, TRANSFER_TO_INTERVENTION, TRANSFER_TO_FIRING -> {
                move(simulationTime);
            }
            case INTERVENTION -> {
                // tu się chyba nic nie będzie działo
            }
            case FIRING -> {
                // tu nie wiem co się będzie działo, zależy gdzie będzie tracone "HP" przez strzelaninę
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

    private void move(double simulationTime) throws Exception {
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

    @Override
    public void takeOrder(State state, Action action) {
        this.state = state;
        this.action = action;
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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public long getTimeSinceLastActive() {
        // TODO Calc based on world state
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var oldColor = g.getColor();

        //TODO wybrać kolory patroli dla poszczególnych czynności
        switch (this.state) {
            case PATROLLING -> g.setColor(new Color(0, 153, 0)); // green
            case TRANSFER_TO_INTERVENTION -> g.setColor(new Color(255, 166, 77)); // orangeish
            case TRANSFER_TO_FIRING -> g.setColor(new Color(255, 77, 77)); // redish
            case INTERVENTION -> g.setColor(new Color(0, 92, 230)); // blue
            case FIRING -> g.setColor(new Color(153, 0, 204)); // purple
            case NEUTRALIZED -> g.setColor(new Color(255, 255, 255)); // white
            default -> {
                g.setColor(Color.BLACK); // black
                System.out.println("the patrol has no State");
            }
        }

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
        public Entity target;

        public Action(Long startTime) {
            this.startTime = startTime;
        }

        public void setTarget(Entity target) {
            this.target = target;
        }
    }

    public class Transfer extends Action {
        public ArrayList<Node> pathNodeList;

        public Transfer(Long startTime, Entity target) {
            super(startTime);
            this.target = target;
            this.pathNodeList = (ArrayList<Node>) World.getInstance().getMap().getPathNodeList(getLatitude(), getLongitude(), target.getLatitude(), target.getLongitude());
        }

    }

    public class IncidentParticipation extends Action {

        public IncidentParticipation(Long startTime, Incident incident) {
            super(startTime);
            this.target = incident;
        }
    }
}
