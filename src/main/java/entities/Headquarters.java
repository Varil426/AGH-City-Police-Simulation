package entities;

import java.util.ArrayList;
import java.util.List;

public class Headquarters extends Entity {

    private List<Patrol> patrols = new ArrayList<>();
    private List<Incident> incidents = new ArrayList<>();

    public Headquarters(double x, double y) {
        super(x,y);
    }

    // TODO Lists methods
}
