package entities.factories;

import World.World;
import de.westnordost.osmapi.map.data.Node;
import entities.District;
import entities.Firing;
import entities.Intervention;
import utils.Logger;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class IncidentFactory {

    private static World world = World.getInstance();

    private static final int MIN_EVENT_DURATION = 5;
    private static final int MAX_EVENT_DURATION = 60;
    private static final int MIN_FIRING_STRENGTH = 1000;
    private static final int MAX_FIRING_STRENGTH = 3000;

    public static Intervention createRandomInterventionForDistrict(District district) {
        var randomNode = district.getAllNodesInDistrict().get(ThreadLocalRandom.current().nextInt(0, district.getAllNodesInDistrict().size()));
        var latitude = randomNode.getPosition().getLatitude();
        var longitude = randomNode.getPosition().getLongitude();

        // Will change into firing
        if (ThreadLocalRandom.current().nextDouble() < ThreatLevelToFiringChance(district.getThreatLevel())) {
            var duration = ThreadLocalRandom.current().nextInt(MIN_EVENT_DURATION, MAX_EVENT_DURATION);
            var timeToChange = ThreadLocalRandom.current().nextInt(0, duration);

            return new Intervention(latitude, longitude, duration, true, timeToChange);
        } else {
            return new Intervention(latitude, longitude, ThreadLocalRandom.current().nextInt(MIN_EVENT_DURATION, MAX_EVENT_DURATION+1));
        }
    }

    public static Firing createRandomFiringFromIntervention(Intervention intervention) {
        var strength = ThreadLocalRandom.current().nextInt(MIN_FIRING_STRENGTH, MAX_FIRING_STRENGTH+1);
        var numberOfRequiredPatrols = ThreadLocalRandom.current().nextInt(1, (int)Math.ceil(strength/500.));
        return new Firing(intervention.getLatitude(), intervention.getLongitude(), numberOfRequiredPatrols, strength);
    }

    private static double ThreatLevelToFiringChance(District.ThreatLevelEnum threatLevel) {
        return world.getConfig().getFiringChanceForThreatLevel(threatLevel);
    }

}
