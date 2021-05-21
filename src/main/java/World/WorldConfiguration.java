package World;

import entities.District;
import utils.Logger;

import java.util.HashMap;

public class WorldConfiguration {

    private String cityName;
    private HashMap<District, Integer> districtsDangerLevels = new HashMap<>();
    private int numberOfPolicePatrols = 10;
    private int timeRate = 300;
    private long simulationDuration = 86400;
    private boolean drawDistrictsBorders = false;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
        Logger.getInstance().logNewMessage("City has been set.");
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

    public boolean isDrawDistrictsBorders() {
        return drawDistrictsBorders;
    }

    public void setDrawDistrictsBorders(boolean drawDistrictsBorders) {
        this.drawDistrictsBorders = drawDistrictsBorders;
    }
}
