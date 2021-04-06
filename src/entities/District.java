package entities;

import java.util.List;

public class District extends Entity {

    private List<Point> borders;

    public District(List<Point> borders, double x, double y) {
        super(x,y);
        this.borders = borders;
    }

    public District(List<Point> borders) {
        this.borders = borders;
    }

    public List<Point> getBorders() {
        return borders;
    }

    // TODO Get all nodes in district (?)
}
