import entities.District;

import java.util.HashMap;

public class WorldConfiguration {

    private String cityName;
    private HashMap<District, Integer> districtsDangerLevels = new HashMap<>();
    private int numberOfPolicePatrols;
    private int timeRate;
    private long simulationDuration;

    public WorldConfiguration(String cityName) {
        this.cityName = cityName;
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

    public void setNumberOfPolicePatrols(int numberOfPolicePatrols) {
        this.numberOfPolicePatrols = numberOfPolicePatrols;
    }

    public int getTimeRate() {
        return timeRate;
    }

    public void setTimeRate(int timeRate) {
        if (timeRate <= 0 ) {
            throw new IllegalArgumentException("Time rate must be of positive value.");
        }
        this.timeRate = timeRate;
    }

    public void setSimulationDuration(long simulationDuration) {
        this.simulationDuration = simulationDuration;
    }
}
