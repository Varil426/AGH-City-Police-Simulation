package entities;

public class Intervention extends Incident {

    private final long duration;
    private final boolean willChangeIntoFiring;
    private final long timeToChange;

    private Patrol patrolSolving;

    public Intervention(double x, double y) {
        super(x, y);

        // TODO Random parameters selection
        this.duration = 0;
        this.willChangeIntoFiring = false;
        this.timeToChange = -1;
    }

    public Intervention(double x, double y, long duration) {
        super(x, y);
        this.duration = duration;

        this.willChangeIntoFiring = false;
        this.timeToChange = -1;
    }

    public Intervention(double x, double y, long duration, boolean willChangeIntoFiring, long timeToChange) {
        super(x, y);
        this.duration = duration;
        this.willChangeIntoFiring = willChangeIntoFiring;
        if (timeToChange < 0) {
            throw new IllegalArgumentException("timeToChange must be greater thatn or equal to zero");
        }
        this.timeToChange = timeToChange;
    }

    @Override
    public void updateState() {
        // TODO
    }

}
