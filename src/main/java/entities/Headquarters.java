package entities;

import World.World;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Headquarters extends Entity implements IDrawable {

    //TODO improve value of 'range'
    private final double range = 600.0;
    private List<Patrol> patrols = new ArrayList<>();
    private List<Incident> incidents = new ArrayList<>();

    public Headquarters(double latitude, double longitude) {
        super(latitude, longitude);
    }

    @Override
    public void drawSelf(Graphics2D g, JXMapViewer mapViewer) {
        var oldColor = g.getColor();
        g.setColor(Color.BLUE);

        final var size = 10;
        var point = mapViewer.convertGeoPositionToPoint(new GeoPosition(getLatitude(), getLongitude()));

        var mark = new Ellipse2D.Double((int) (point.getX() - size / 2), (int) (point.getY() - size / 2), size, size);
        g.fill(mark);

        g.setColor(oldColor);
    }

    public void assignTasks() {
        updatePatrolsAndIncidents();
        var allInterventions = incidents.stream().filter(x -> x instanceof Intervention).collect(Collectors.toList());
        var allFirings = incidents.stream().filter(x -> x instanceof Firing).collect(Collectors.toList());

        for (Entity intervention : allInterventions) {
            //TODO obsługa interwencji

            if (((Intervention) intervention).getPatrolSolving() == null) {
                Patrol availablePatrol = null;
                for (int i = 1; i < 10; i++) {
                    availablePatrol = World.getInstance().getEntitiesNear(intervention, range * i)
                            .stream()
                            .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.PATROLLING)
                            .map(x -> (Patrol) x).findFirst()
                            .orElse(null);
                    if (availablePatrol != null) {
                        break;
                    }
                }

                if (availablePatrol != null) {
                    availablePatrol.takeOrder(
                            Patrol.State.TRANSFER_TO_INTERVENTION,
                            availablePatrol.new Transfer(World.getInstance().getSimulationTimeLong(),
                            intervention));
                    ((Intervention) intervention).setPatrolSolving(availablePatrol);
                }
            }
        }
        for (Entity firing : allFirings) {
            //TODO obsługa strzalnin
            int requiredPatrols = ((Firing) firing).getRequiredPatrols();
            List<Patrol> patrolsSolving = ((Firing) firing).getPatrolsSolving();
            List<Patrol> patrolsReaching = ((Firing) firing).getPatrolsReaching();
            if (requiredPatrols <= patrolsSolving.size()) {
                for (Patrol patrolReaching : patrolsReaching) {
                    ((Firing) firing).removeReachingPatrol(patrolReaching);
                    patrolReaching.setState(Patrol.State.PATROLLING);
                }
            }
            if (patrolsSolving.size() + patrolsReaching.size() < requiredPatrols) {
                for (int i = 1; i < 4; i++) {
                    List<Patrol> foundPatrols = World.getInstance().getEntitiesNear(firing, range * i)
                            .stream()
                            .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.PATROLLING)
                            .map(x -> (Patrol) x)
                            .collect(Collectors.toList());
                    for (Patrol p : foundPatrols) {
                        p.takeOrder(Patrol.State.TRANSFER_TO_FIRING, p.new Transfer(World.getInstance().getSimulationTimeLong(), firing));
                        ((Firing) firing).addReachingPatrol(p);
                    }
                    if (foundPatrols.size() + patrolsSolving.size() + patrolsReaching.size() >= requiredPatrols) {
                        break;
                    }
                }
                if (patrolsSolving.size() + patrolsReaching.size() < requiredPatrols) {
                    List<Patrol> foundTransferringToInterventionPatrols = World.getInstance().getEntitiesNear(firing, range)
                            .stream()
                            .filter(x -> x instanceof Patrol && ((Patrol) x).getState() == Patrol.State.TRANSFER_TO_INTERVENTION)
                            .map(x -> (Patrol) x)
                            .collect(Collectors.toList());
                    for (Patrol p : foundTransferringToInterventionPatrols) {
                        p.takeOrder(Patrol.State.TRANSFER_TO_FIRING, p.new Transfer(World.getInstance().getSimulationTimeLong(), firing));
                        ((Firing) firing).addReachingPatrol(p);
                    }
                }
            }
        }
    }

    public void updatePatrolsAndIncidents() {
        var allEntities = World.getInstance().getAllEntities();
        patrols = allEntities.stream().filter(x -> x instanceof Patrol).map(x -> (Patrol) x).collect(Collectors.toList());
        incidents = allEntities.stream().filter(x -> x instanceof Incident).map(x -> (Incident) x).collect(Collectors.toList());
    }

    // TODO Lists methods
}
