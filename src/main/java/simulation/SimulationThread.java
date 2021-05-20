package simulation;

import World.World;
import de.westnordost.osmapi.map.data.Node;
import entities.*;

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
                HQAssignTasks();
                updateStatesOfAgents();
                performAgentsActions();

                sleep(40);
            } catch (Exception e) {
                System.out.println(e);
                System.out.println(e.getMessage());
                e.printStackTrace();
                e.getCause();
            }
        }
    }

    private void HQAssignTasks() {
        var allHQs = World.getInstance().getAllEntities().stream().filter(x -> x instanceof Headquarters).map(x -> (Headquarters)x).collect(Collectors.toList());
        for (var hqs : allHQs) {
            hqs.assignTasks();
        }
    }

    private void updateStatesOfAgents() throws Exception {
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
