package simulation;

import World.World;
import de.westnordost.osmapi.map.data.Node;
import entities.Patrol;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationThread extends Thread {

    @Override
    public void run() {
        // TODO Set simulation stage
        var world = World.getInstance();
        for (int i = 0; i < world.getConfig().getNumberOfPolicePatrols(); i++) {
            // TODO Change to HQ when movement is ready
            var startingPoint = (Node)world.getMap().getMyNodes().values().toArray()[ThreadLocalRandom.current().nextInt(world.getMap().getMyNodes().size())];
            var newPatrol = new Patrol(startingPoint.getPosition());
            world.addEntity(newPatrol);
        }

        World.getInstance().setStartTime();
        while (true) {
            // TODO Exit condition
            updateStatesOfAgents();
            performAgentsActions();
            try {
                sleep(100);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private void updateStatesOfAgents() {
        // TODO
    }

    private void performAgentsActions() {
        // TODO
    }

}
