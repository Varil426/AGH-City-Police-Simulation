package simulation;

import World.World;
import entities.District;
import entities.Entity;
import entities.Patrol;
import entities.factories.IncidentFactory;
import utils.DelayedAction;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class EventsDirector extends Thread {

    private final World world = World.getInstance();

    public EventsDirector() {}

    @Override
    public void run() {
        while (!world.hasSimulationDurationElapsed()) {

            for (var district : world.getDistricts()) {
                generateNewEventsInDistrict(district);
            }

            var collect = World.getInstance().getAllEntities()
                    .stream()
                    .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.NEUTRALIZED)
                    .collect(Collectors.toList());
            for (Entity patrol : collect) {
                World.getInstance().removeEntity(patrol);
            }

            // Director goes to sleep for an hour in simulation time
            var sleepTime = (3600000.) / world.getConfig().getTimeRate();
            try {
                sleep((long) sleepTime, (int) ((sleepTime - (long) sleepTime) * 1000000));
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    private void generateNewEventsInDistrict(District district) {
        var numberOfIncidentsInNextHour = ThreadLocalRandom.current().nextInt(world.getConfig().getMaxIncidentForThreatLevel(district.getThreatLevel()));

        for (var i = 0; i < numberOfIncidentsInNextHour; i++) {
            var newEvent = IncidentFactory.createRandomInterventionForDistrict(district);
            var sleepTime = ThreadLocalRandom.current().nextDouble((3600000.) / world.getConfig().getTimeRate());
            new DelayedAction((long) sleepTime, (int) ((sleepTime - (long) sleepTime) * 1000000), () -> world.addEntity(newEvent)).start();
        }
    }
}
