package entities;

import java.awt.geom.Path2D;

public class District {
    private Long id;
    private String name;

    private Path2D boundaries;

    public District(Long id, String name, Path2D boundaries) {
        this.boundaries = boundaries;
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Path2D getBoundaries() {
        return boundaries;
    }

    public String getName() {
        return name;
    }
    // TODO Get all nodes in district (?)
}
