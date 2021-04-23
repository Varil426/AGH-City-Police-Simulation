package entities;

import de.westnordost.osmapi.map.data.LatLon;

import java.awt.geom.Path2D;

public class District {
    private final Long id;
    private final String name;

    private final Path2D boundaries;

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

    public boolean contains(LatLon latLon) {
        return this.boundaries.contains(latLon.getLatitude(), latLon.getLongitude());
    }

    // TODO Get all nodes in district (?)
}
