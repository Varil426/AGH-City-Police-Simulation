package entities;

import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.OsmLatLon;

import java.util.UUID;

// TODO Add entities in code documentation
public abstract class Entity {

    private UUID uniqueID = UUID.randomUUID();;
    private double latitude = 0;
    private double longitude = 0;

    protected Entity() {}

    public Entity(double latitude, double longitude) {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public Entity(LatLon position) {
        this(position.getLatitude(), position.getLongitude());
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (latitude < 0) {
            throw new IllegalArgumentException("X must be greater than 0.");
        }
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (longitude < 0) {
            throw new IllegalArgumentException("X must be greater than 0.");
        }
        this.longitude = longitude;
    }

    public LatLon getPosition() {
        return new OsmLatLon(latitude, longitude);
    }

    public UUID getUniqueID() {
        return uniqueID;
    }
}
