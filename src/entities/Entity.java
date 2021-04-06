package entities;

import java.util.UUID;

// TODO Add entities in code documentation
public abstract class Entity {

    private UUID uniqueID;
    private double x = 0;
    private double y = 0;

    public Entity() {
        uniqueID = UUID.randomUUID();
    }

    public Entity(double x, double y) {
        this();
        this.setX(x);
        this.setY(y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        if (x < 0) {
            throw new IllegalArgumentException("X must be greater than 0.");
        }
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        if (y < 0) {
            throw new IllegalArgumentException("X must be greater than 0.");
        }
        this.y = y;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }
}
