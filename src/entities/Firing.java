package entities;

import java.util.ArrayList;
import java.util.List;

public class Firing extends Incident {

    private int strength;
    private final int requiredPatrols;
    private List<Patrol> patrolsSolving = new ArrayList<>();

    public Firing(double x, double y) {
        super(x, y);

        // TODO Parameters random selection
        this.requiredPatrols = 2;
        this.strength = 500;
    }

    public Firing(double x, double y, int requiredPatrols, int initialStrength) {
        super(x, y);
        this.requiredPatrols = requiredPatrols;
        this.strength = initialStrength;
    }

    @Override
    public void updateState() {
        // TODO
    }
}
