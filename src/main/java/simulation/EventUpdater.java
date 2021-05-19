package simulation;

import World.World;
import entities.IEvent;

import java.util.List;

public class EventUpdater extends Thread {

    private final World world = World.getInstance();

    public EventUpdater() {
    }

    @Override
    public void run() {
        // TODO Exit condition
        while (true) {

            List<IEvent> activeEvents = world.getActiveEvents();
            for (var intervention : activeEvents) {
                intervention.updateState();
            }

            try {
                sleep(50);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
    }
}
