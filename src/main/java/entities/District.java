package entities;

import math.geom2d.polygon.SimplePolygon2D;

import java.util.List;

public class District {
    private Long id;
    private String name;

    private SimplePolygon2D simplePolygon2D;

    public District(Long id, String name, SimplePolygon2D simplePolygon2D) {
        this.simplePolygon2D = simplePolygon2D;
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public SimplePolygon2D getSimplePolygon2D() {
        return simplePolygon2D;
    }

    public String getName() {
        return name;
    }
    // TODO Get all nodes in district (?)
}
