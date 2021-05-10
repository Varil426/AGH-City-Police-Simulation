package simulation;

import World.World;
import de.westnordost.osmapi.map.data.Node;
import entities.Entity;
import entities.IAgent;
import entities.Patrol;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SimulationThread extends Thread {

    @Override
    public void run() {
        // TODO Set simulation stage
        var world = World.getInstance();
        World.getInstance().simulationStart();

        for (int i = 0; i < world.getConfig().getNumberOfPolicePatrols(); i++) {
            // TODO Change to HQ when movement is ready
            var startingPoint = (Node) world.getMap().getMyNodes().values().toArray()[ThreadLocalRandom.current().nextInt(world.getMap().getMyNodes().size())];
            var newPatrol = new Patrol(startingPoint.getPosition());
            newPatrol.setState(Patrol.State.PATROLLING);
            world.addEntity(newPatrol);
        }

        while (true) {
            // TODO Exit condition
            try {
                updateStatesOfAgents();
                performAgentsActions();
                sleep(40);
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private void updateStatesOfAgents() throws Exception {
        // TODO
        // HQ

        var allAgents = World.getInstance().getAllEntities().stream().filter(x -> x instanceof IAgent).collect(Collectors.toList());
        for (Entity agents : allAgents) {
            ((IAgent) agents).updateStateSelf();
        }
    }

    private void performAgentsActions() throws Exception {
        // TODO
        var allAgents = World.getInstance().getAllEntities().stream().filter(x -> x instanceof IAgent).collect(Collectors.toList());
        for (Entity agents : allAgents) {
            ((IAgent) agents).performAction();
        }
    }
}
