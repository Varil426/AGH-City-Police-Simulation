package entities;

public class Intervention extends Incident {

    private final long duration;
    private final boolean willChangeIntoFiring;
    private final long timeToChange;

    private Patrol patrolSolving;

    public Intervention(double latitude, double longitude) {
        super(latitude, longitude);

        // TODO Random parameters selection
        this.duration = 0;
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
        // TODO
    }

}
