import entities.District;

import java.util.HashMap;

public class WorldConfiguration {

    private String cityName;
    private HashMap<District, Integer> districtsDangerLevels;
    private int numberOfPolicePatrols;
    private int timeRate;
    private long simulationDuration;

    public WorldConfiguration(String cityName, HashMap<District, Integer> districtsDangerLevels, int numberOfPolicePatrols, int timeRate, long simulationDuration) {
        this.cityName = cityName;
        this.districtsDangerLevels = districtsDangerLevels;
        this.numberOfPolicePatrols = numberOfPolicePatrols;
        this.timeRate = timeRate;
        this.simulationDuration = simulationDuration;
    }

    public String getCityName() {
        return cityName;
    }

    public long getSimulationTime() {
        return simulationDuration;
    }

    public HashMap<District, Integer> getDistrictsDangerLevels() {
        return districtsDangerLevels;
    }

    public int getNumberOfPolicePatrols() {
        return numberOfPolicePatrols;
    }
}
