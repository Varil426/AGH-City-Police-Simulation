package simulation;

import World.World;
import de.westnordost.osmapi.map.data.Node;
import entities.District;
import entities.Intervention;
import entities.factories.IncidentFactory;
import utils.DelayedAction;

import java.util.concurrent.ThreadLocalRandom;

public class EventsDirector extends Thread {

    private final World world = World.getInstance();

    public EventsDirector() {

    }

    @Override
    public void run() {
        // TODO Exit condition
        while (true) {

            for(var district : world.getDistricts()) {
                generateNewEventsInDistrict(district);
            }
//            for (int i = 0; i < 5; i++) {
//                generateNewEventsInDistrict(world.getDistricts().get(i));
//
//            }

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
        var numberOfIncidentsInNextHour = district.getThreatLevel();
        for (var i = 0; i < numberOfIncidentsInNextHour; i++) {
            var newEvent = IncidentFactory.createRandomInterventionForDistrict(district);
            var sleepTime = ThreadLocalRandom.current().nextDouble((3600000.)/world.getConfig().getTimeRate());
            new DelayedAction((long) sleepTime, (int)((sleepTime - (long) sleepTime) * 1000000), () -> world.addEntity(newEvent)).start();
        }
    }
}
