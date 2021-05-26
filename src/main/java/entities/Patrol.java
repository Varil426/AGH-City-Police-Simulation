package entities;

import World.World;
import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.Node;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import simulation.PathCalculator;
import utils.Haversine;
import utils.Logger;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Patrol extends Entity implements IAgent, IDrawable {

    private final double durationOfTheShift;
    private final double basePatrollingSpeed;
    private final double baseTransferSpeed;
    private final double basePrivilegedSpeed;
    private final double shiftEndTime;
    private final double timeBetweenDrawNeutralization;
    private double timeOfLastMove;
    private State state;
    private State previousState;
    private Action action;
    private double timeOfLastDrawNeutralization;

    public Patrol() {
        // TODO Change default values
        basePatrollingSpeed = 40;
        baseTransferSpeed = 60;
        basePrivilegedSpeed = 80;
        this.timeOfLastMove = World.getInstance().getSimulationTime();
        this.durationOfTheShift = World.getInstance().getDurationOfTheShift();
        this.shiftEndTime = World.getInstance().getSimulationTime() + durationOfTheShift;
        this.timeBetweenDrawNeutralization = new Random().nextInt(1000) + 3000;
        this.timeOfLastDrawNeutralization = World.getInstance().getSimulationTime();
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
        this.timeOfLastMove = World.getInstance().getSimulationTime();
        this.durationOfTheShift = World.getInstance().getDurationOfTheShift();
        this.shiftEndTime = World.getInstance().getSimulationTime() + durationOfTheShift;
        this.timeBetweenDrawNeutralization = new Random().nextInt(1000) + 3000;
        this.timeOfLastDrawNeutralization = World.getInstance().getSimulationTime();
    }

    public void updateStateSelf() throws Exception {
        if (state == State.PATROLLING) {
            if (isShiftOver()) {
                setState(State.RETURNING_TO_HQ);
                var HQ = World.getInstance().getAllEntities().stream().filter(x -> x instanceof Headquarters).findFirst().orElse(null);
                setAction(new Transfer(World.getInstance().getSimulationTimeLong(), HQ, this.state));
            } else if (action == null) {
                drawNewTarget();
                Logger.getInstance().logNewMessage(this + " action set to " + action.getClass().toString() + " target: " + action.target.toString());
            } else if (action instanceof Transfer) {
                // if pathNodeList is empty, it draws a new patrol target
                if (((Transfer) action).pathNodeList != null && ((Transfer) action).pathNodeList.size() == 0) {
                    drawNewTarget();
                }
            } else {
                drawNewTarget();
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
            if (action.target instanceof Firing) {
                setState(State.FIRING);
                ((Firing) action.target).addSolvingPatrol(this);
                action = new IncidentParticipation(World.getInstance().getSimulationTimeLong(), (Incident) action.target);
            }
            // if the duration of the intervention is over, patrol changes state to PATROLLING
            else if (action instanceof IncidentParticipation) {
                if (action.target == null) {
                    setState(State.PATROLLING);
                    drawNewTarget();
                } else if (!(((Intervention) (action).target).isActive())) {
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
                if (((Transfer) action).pathNodeList != null && ((Transfer) action).pathNodeList.size() == 0) {
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
            if (action instanceof IncidentParticipation) {
                if (action.target == null) {
                    setState(State.PATROLLING);
                    drawNewTarget();
                } else if (!((Firing) action.target).isActive()) {
                    setState(State.PATROLLING);
                    drawNewTarget();
                } else if (!(action.target instanceof Firing)) {
                    setState(State.PATROLLING);
                    drawNewTarget();
                } else if (World.getInstance().getSimulationTime() > timeOfLastDrawNeutralization + timeBetweenDrawNeutralization) {
                    if (ThreadLocalRandom.current().nextDouble() < 0.01) {
                        ((Firing) this.action.target).removeSolvingPatrol(this);
                        setState(State.NEUTRALIZED);
                    }
                    timeOfLastDrawNeutralization = World.getInstance().getSimulationTime();
                }
            } else {
                throw new Exception("Action should be 'IncidentParticipation' and it is not");
            }
        } else if (state == State.CALCULATING_PATH) {
            if (((Transfer) getAction()).pathNodeList != null) {
                setState(this.previousState);
            }
        } else if (state == State.RETURNING_TO_HQ) {
            if (action == null) {
                World.getInstance().getAllEntities()
                        .stream()
                        .filter(x -> x instanceof Headquarters)
                        .findFirst()
                        .ifPresent(HQ -> action = new Transfer(World.getInstance().getSimulationTimeLong(), HQ, this.state));
            } else if (!(action instanceof Transfer)) {
                throw new Exception("Action should be 'Transfer' and it is not");
            }
        }
    }

    private void drawNewTarget() {
        var world = World.getInstance();
        Random generator = new Random();
        var node = (Node) world.getMap().getMyNodes().values().toArray()[generator.nextInt(world.getMap().getMyNodes().size())];
        action = new Transfer(World.getInstance().getSimulationTimeLong(), new Point(node.getPosition().getLatitude(), node.getPosition().getLongitude()), this.state);
        Logger.getInstance().logNewMessage(this + " action set to " + action.getClass().toString() + " target: " + action.target.toString());
    }

    public void performAction() throws Exception {
        double simulationTime = World.getInstance().getSimulationTime();
        switch (state) {
            case PATROLLING -> {
                if (action instanceof Transfer && ((Transfer) this.action).pathNodeList != null) {
                    move(simulationTime);
                }
            }
            case RETURNING_TO_HQ -> {
                if (action instanceof Transfer && ((Transfer) this.action).pathNodeList != null) {
                    if (((Transfer) action).pathNodeList.size() == 0) {
                        World.getInstance().removeEntity(this);
                    } else {
                        move(simulationTime);
                    }
                }
            }
            case TRANSFER_TO_INTERVENTION, TRANSFER_TO_FIRING -> {
                move(simulationTime);
            }
            case INTERVENTION, CALCULATING_PATH, FIRING -> {
                // empty
            }
            case NEUTRALIZED -> {
                // empty
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
    public void takeOrder(Action action) {
        this.action = action;
    }

    private double getDistanceToNearestNode() throws Exception {
        if (((Transfer) action).pathNodeList.size() == 0) throw new Exception("pathNodeList is empty!");

        LatLon sourceNodePosition = ((Transfer) action).pathNodeList.get(0).getPosition();
        return Haversine.distance(getLatitude(), getLongitude(), sourceNodePosition.getLatitude(), sourceNodePosition.getLongitude());
    }

    public double getSpeed() {
        switch (state) {
            case PATROLLING, RETURNING_TO_HQ -> {
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

    public boolean isShiftOver() {
        return World.getInstance().getSimulationTime() > shiftEndTime;
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

        switch (this.state) {
            case PATROLLING -> g.setColor(new Color(0, 153, 0)); // green
            case RETURNING_TO_HQ -> g.setColor(new Color(0, 100, 0)); // dark green
            case TRANSFER_TO_INTERVENTION -> g.setColor(new Color(255, 166, 77)); // orangeish
            case TRANSFER_TO_FIRING -> g.setColor(new Color(255, 77, 77)); // redish
            case INTERVENTION -> g.setColor(new Color(0, 92, 230)); // blue
            case FIRING -> g.setColor(new Color(153, 0, 204)); // purple
            case NEUTRALIZED -> g.setColor(new Color(255, 255, 255)); // white
            case CALCULATING_PATH -> g.setColor(new Color(255, 123, 255)); // pink
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
        NEUTRALIZED,
        CALCULATING_PATH,
        RETURNING_TO_HQ
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

        public Transfer(Long startTime, Entity target, State nextState) {
            super(startTime);
            this.target = target;
            new PathCalculator(Patrol.this, target).start();
            Patrol.this.previousState = nextState;
            Patrol.this.state = State.CALCULATING_PATH;
        }

        public void setPathNodeList(ArrayList<Node> pathNodeList) {
            this.pathNodeList = pathNodeList;
        }
    }

    public class IncidentParticipation extends Action {

        public IncidentParticipation(Long startTime, Incident incident) {
            super(startTime);
            this.target = incident;
        }
    }
}
