package World;

import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.OsmLatLon;
import entities.District;
import entities.Entity;
import entities.Map;
import org.jxmapviewer.viewer.GeoPosition;
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

    private List<Entity> allEntities = new ArrayList<>();
    private LocalDateTime startTime;

    private LatLon position;

    private Map map;

    private WorldConfiguration worldConfig;


    private World() {
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
        return this.allEntities.stream().filter(entity -> Point2D.distance(entity.getLatitude(), entity.getLongitude(), x, y) <= range).collect(Collectors.toList());
    }

    public void addEntity(Entity entity) {
        allEntities.add(entity);
    }

    public void removeEntity(Entity entity) {
        allEntities.remove(entity);
    }

    public List<Entity> getEntitiesNear(Entity target, double range) {
        return getEntitiesNear(target.getLatitude(), target.getLongitude(), range);
    }

    public long getSimulationTime() {
        var duration = Duration.between(this.startTime, LocalDateTime.now());
        return (long)(duration.getSeconds() + duration.getNano() / Math.pow(10, 9)) * worldConfig.getTimeRate();
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;

        // Set world position to center of a map
        var minCoordinates = new GeoPosition(
                map.getGraph().vertexSet().stream().map(x -> x.getPosition().getLatitude()).min(Double::compare).get(),
                map.getGraph().vertexSet().stream().map(x -> x.getPosition().getLongitude()).min(Double::compare).get());

        var maxCoordinates = new GeoPosition(
                map.getGraph().vertexSet().stream().map(x -> x.getPosition().getLatitude()).max(Double::compare).get(),
                map.getGraph().vertexSet().stream().map(x -> x.getPosition().getLongitude()).max(Double::compare).get());

        var latitude = (minCoordinates.getLatitude() + maxCoordinates.getLatitude())/2;
        var longitude = (minCoordinates.getLongitude() + maxCoordinates.getLongitude())/2;

        position = new OsmLatLon(latitude, longitude);
    }

    public LatLon getPosition() {
        return position;
    }

    public List<District> getDistricts() {
        return map.getDistricts();
    }

    public void setStartTime() {
        startTime = LocalDateTime.now();
    }
}
