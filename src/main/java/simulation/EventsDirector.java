package simulation;

import World.World;
import entities.District;
import entities.factories.IncidentFactory;
import utils.DelayedAction;

import java.util.concurrent.ThreadLocalRandom;

public class EventsDirector extends Thread {

    private final World world = World.getInstance();

    public EventsDirector() {

    }

    @Override
    public void run() {
        while (world.hasSimulationDurationElapsed()) {

            for(var district : world.getDistricts()) {
                generateNewEventsInDistrict(district);
            }

            // Director goes to sleep for an hour in simulation time
            var sleepTime = (3600000.)/world.getConfig().getTimeRate();
            try {
                sleep((long) sleepTime, (int)((sleepTime - (long) sleepTime) * 1000000));
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }

    private void generateNewEventsInDistrict(District district) {
        var numberOfIncidentsInNextHour = 0;

        switch (district.getThreatLevel()) {
            case Safe:
                numberOfIncidentsInNextHour = ThreadLocalRandom.current().nextInt(2);
                break;
            case RatherSafe:
                numberOfIncidentsInNextHour = ThreadLocalRandom.current().nextInt(4);
                break;
            case NotSafe:
                numberOfIncidentsInNextHour = ThreadLocalRandom.current().nextInt(7);
                break;
        }

        for (var i = 0; i < numberOfIncidentsInNextHour; i++) {
            var newEvent = IncidentFactory.createRandomInterventionForDistrict(district);
            var sleepTime = ThreadLocalRandom.current().nextDouble((3600000.)/world.getConfig().getTimeRate());
            new DelayedAction((long) sleepTime, (int)((sleepTime - (long) sleepTime) * 1000000), () -> world.addEntity(newEvent)).start();
        }
    }
}
