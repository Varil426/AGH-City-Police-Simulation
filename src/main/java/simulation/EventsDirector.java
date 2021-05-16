package simulation;

import World.World;
import de.westnordost.osmapi.map.data.Node;
import entities.District;
import entities.Intervention;
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
        // TODO Improve - logic for creating Firing
        var numberOfIncidentsInNextHour = district.getThreatLevel();
        for (var i = 0; i < numberOfIncidentsInNextHour; i++) {
            var randomNode = (Node) world.getMap().getMyNodes().values().toArray()[ThreadLocalRandom.current().nextInt(world.getMap().getMyNodes().size())];
            while (!district.contains(randomNode.getPosition())) {
                randomNode = (Node) world.getMap().getMyNodes().values().toArray()[ThreadLocalRandom.current().nextInt(world.getMap().getMyNodes().size())];
            }

            var newEvent = new Intervention(randomNode.getPosition().getLatitude(), randomNode.getPosition().getLongitude(), ThreadLocalRandom.current().nextInt(5, 60));
            var sleepTime = ThreadLocalRandom.current().nextDouble((3600000.)/world.getConfig().getTimeRate());
            new DelayedAction((long) sleepTime, (int)((sleepTime - (long) sleepTime) * 1000000), () -> world.addEntity(newEvent)).start();
        }
    }
}
