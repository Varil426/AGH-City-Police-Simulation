package entities;

public abstract class Incident extends Entity implements IEvent {

    protected long timeOfLastUpdate;
    private long startTime;

    public Incident() {
        super();

        // TODO set time of last update to creation time from world, set start time
    }

    public Incident(double x, double y) {
        super(x,y);

        // TODO set time of last update to creation time from world, set start time
    }

    public long getStartTime() {
        return startTime;
    }
}
