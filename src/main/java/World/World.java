package World;

import entities.District;
import entities.Entity;
import entities.Map;
import utils.Pair;

import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class World {

    private static volatile World instance;

    public static World getInstance() {
        // Result variable here may seem pointless, but it's needed for DCL (Double-checked locking).
        var result = instance;
        if (instance != null) {
            return  result;
        }
        synchronized (World.class) {
            if (instance == null) {
                instance = new World();
            }
            return instance;
        }
    }

    private List<Entity> allEntities;
    private LocalDateTime startTime;

    private Pair<Double, Double> longitudes;
    private Pair<Double, Double> latitudes;

    private Map map;
    private List<District> districts;

    private WorldConfiguration worldConfig;


    private World() {
        // TODO

        // TODO Maybe move it to separate method called just after clicking start
        this.startTime = LocalDateTime.now();
    }

    public void setConfig(WorldConfiguration worldConfig) {
        this.worldConfig = worldConfig;
    }

    public WorldConfiguration getConfig() {
        return worldConfig;
    }

    public List<Entity> getAllEntities() {
        return new ArrayList<>(this.allEntities);
    }

    public List<Entity> getEntitiesNear(double x, double y, double range) {
        return this.allEntities.stream().filter(entity -> Point2D.distance(entity.getX(), entity.getY(), x, y) <= range).collect(Collectors.toList());
    }

    public List<Entity> getEntitiesNear(Entity target, double range) {
        return getEntitiesNear(target.getX(), target.getY(), range);
    }

    public long getSimulationTime() {
        var duration = Duration.between(this.startTime, LocalDateTime.now());
        return (long)(duration.getSeconds() + duration.getNano() / Math.pow(10, 9)); //TODO When config * this.config.getTimeRate();
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
