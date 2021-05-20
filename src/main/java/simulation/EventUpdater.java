package simulation;

import World.World;

public class EventUpdater extends Thread {

    private final World world = World.getInstance();

    public EventUpdater() {
    }

    @Override
    public void run() {
        // TODO Exit condition
        while (true) {
            var activeEvents = world.getActiveEvents();
            for (var intervention : activeEvents) {
                intervention.updateState();
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e);
                System.out.println(e.getMessage());
                e.printStackTrace();
                e.getCause();
            }
        }
    }
}