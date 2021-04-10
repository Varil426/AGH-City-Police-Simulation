package entities;

public class Patrol extends Entity implements IAgent {

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

    public Patrol(double x, double y) {
        this();
        this.setX(x);
        this.setY(y);
    }

    public Patrol(double x, double y, double baseTransferSpeed, double basePatrollingSpeed, double basePrivilegedSpeed) {
        this.setX(x);
        this.setY(y);
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

    public long getTimeSinceLastMove() {
        // TODO Calc based on world state
        throw  new UnsupportedOperationException();
    }
}
