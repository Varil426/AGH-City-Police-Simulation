package World;

import de.westnordost.osmapi.map.data.LatLon;
import de.westnordost.osmapi.map.data.OsmLatLon;
import entities.*;
import org.jxmapviewer.viewer.GeoPosition;
import simulation.EventUpdater;
import simulation.ExportToCSV;
import simulation.StatisticsCounter;
import utils.Haversine;
import simulation.EventsDirector;
import utils.Logger;

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

    private final List<Entity> allEntities = new ArrayList<>();

    private LocalDateTime startTime;
    private double timePassedUntilPause = 0;
    private boolean isSimulationPaused = false;

    private LatLon position;

    private Map map;

    private final WorldConfiguration worldConfig = new WorldConfiguration();

    private boolean hasSimulationStarted = false;

    // TODO let the user to choose durationOfTheShift
    private final double durationOfTheShift = 28800;

    private int neutralizedPatrolsTotal = 0;

    private World() {
        this.startTime = LocalDateTime.now();
    }

    public WorldConfiguration getConfig() {
        return worldConfig;
    }

    public List<Entity> getAllEntities() {
        return new ArrayList<>(this.allEntities);
    }

    public List<Entity> getEntitiesNear(double x, double y, double range) {
        synchronized (allEntities){
            return this.allEntities.stream().filter(entity -> Haversine.distance(entity.getLatitude(), entity.getLongitude(), x, y) <= range).collect(Collectors.toList());
        }
    }

    public void addEntity(Entity entity) {
        synchronized (allEntities){
            allEntities.add(entity);
            Logger.getInstance().logNewMessage("Added new " + entity.toString());

            if (entity instanceof Patrol) {
                StatisticsCounter.getInstance().increaseNumberOfPatrols();
            } else if (entity instanceof Intervention) {
                StatisticsCounter.getInstance().increaseNumberOfInterventions();
            } else if (entity instanceof Firing) {
                StatisticsCounter.getInstance().increaseNumberOfFirings();
            }
        }
    }

    public void removeEntity(Entity entity) {
        synchronized (allEntities){
            if (allEntities.remove(entity)) {
                Logger.getInstance().logNewMessage("Removed " + entity.toString());

                if (entity instanceof Patrol && ((Patrol) entity).getState() == Patrol.State.NEUTRALIZED) {
                    StatisticsCounter.getInstance().increaseNumberOfNeutralizedPatrols();
                } else if (entity instanceof Intervention) {
                    StatisticsCounter.getInstance().increaseNumberOfSolvedInterventions();
                } else if (entity instanceof Firing) {
                    StatisticsCounter.getInstance().increaseNumberOfSolvedFirings();
                }
            }
        }
    }

    public List<Entity> getEntitiesNear(Entity target, double range) {
        return getEntitiesNear(target.getLatitude(), target.getLongitude(), range);
    }

    public List<IEvent> getActiveEvents() {
        synchronized (allEntities){
            return allEntities.stream().filter(x -> x instanceof IEvent && ((IEvent) x).isActive()).map(x -> (IEvent)x).collect(Collectors.toList());
        }
    }
    public List<IEvent> getEvents() {
        synchronized (allEntities){
            return allEntities.stream().filter(x -> x instanceof IEvent).map(x -> (IEvent)x).collect(Collectors.toList());
        }
    }

    public int getNeutralizedPatrolsTotal() {
        return neutralizedPatrolsTotal;
    }

    public void setNeutralizedPatrolsTotal(int neutralizedPatrolsTotal) {
        this.neutralizedPatrolsTotal += neutralizedPatrolsTotal;
    }

    public double getDurationOfTheShift(){
        return durationOfTheShift;
    }

    public long getSimulationTimeLong() {
        return (long) getSimulationTime();
    }

    public double getSimulationTime() {
        if (!hasSimulationStarted) {
            return -1;
        }

        if (isSimulationPaused) {
            return  timePassedUntilPause;
        }

        var duration = Duration.between(this.startTime, LocalDateTime.now());
        return ((duration.getSeconds() + duration.getNano() / Math.pow(10, 9)) * worldConfig.getTimeRate()) + timePassedUntilPause;
    }

    public Map getMap() {
        synchronized (map){
            return map;
        }
    }

    public boolean hasSimulationDurationElapsed() {
        return getSimulationTime() > worldConfig.getSimulationDuration();
    }

    public boolean isSimulationPaused() {
        return isSimulationPaused;
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
        Logger.getInstance().logNewMessage("Map has been set.");
    }

    public LatLon getPosition() {
        return position;
    }

    public List<District> getDistricts() {
        return map.getDistricts();
    }

    public void simulationStart() {
        StatisticsCounter.getInstance().reset();
        startTime = LocalDateTime.now();
        timePassedUntilPause = 0;
        isSimulationPaused = false;
        hasSimulationStarted = true;
        new EventsDirector().start();
        new EventUpdater().start();
        new ExportToCSV().start();
        Logger.getInstance().logNewMessage("Simulation has started.");
    }

    public void pauseSimulation() {
        timePassedUntilPause = getSimulationTime();
        isSimulationPaused = true;
        Logger.getInstance().logNewMessage("Simulation has been paused.");
    }

    public  void resumeSimulation() {
        startTime = LocalDateTime.now();
        isSimulationPaused = false;
        Logger.getInstance().logNewMessage("Simulation has been resumed.");
    }

}
