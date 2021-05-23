package guiComponents;


import OSMToGraph.ImportGraphFromRawData;
import World.*;
import utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;

public class ConfigurationPanel {

    private JFrame frame = new JFrame("City Police Simulation");

    private JPanel citySelectionPanel;
    private JPanel districtConfigurationPanel;
    private JPanel simulationConfigurationPanel;
    private JPanel buttonsPanel;

    private final int textInputColumns = 20;
    // TODO Change into dynamically generated
    private final HashMap<String, String[]> availablePlaces = new HashMap<>();

    private JComboBox<String> countrySelectionComboBox;
    private JComboBox<String> citySelectionComboBox;
    private JTextField numberOfCityPatrolsTextField = new JTextField();
    private JTextField timeRateTextField = new JTextField();
    private JTextField simulationDurationTextField = new JTextField();
    private JCheckBox drawDistrictsBoundariesCheckBox = new JCheckBox();;

    public ConfigurationPanel() {
        availablePlaces.put("Poland", new String[]{"Kraków", "Warszawa", "Rzeszów"});
        availablePlaces.put("United Kingdom", new String[]{"London", "Sheffield", "Manchester"});
    }

    private void setDefaultValues() {
        var worldConfig = World.getInstance().getConfig();
        numberOfCityPatrolsTextField.setText(Integer.toString(worldConfig.getNumberOfPolicePatrols()));
        timeRateTextField.setText(Integer.toString(worldConfig.getTimeRate()));
        simulationDurationTextField.setText(Long.toString(worldConfig.getSimulationDuration()));
    }

    // TODO Add validation for input data
    public void createWindow(){
        frame.setSize( 1200, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.setLayout(new GridLayout(1,4));

        citySelectionPanel = new JPanel();
        frame.add(citySelectionPanel);

        countrySelectionComboBox = new JComboBox<>(availablePlaces.keySet().toArray(new String[0]));
        countrySelectionComboBox.addActionListener(e -> {
            var selectedItem = countrySelectionComboBox.getSelectedItem().toString();
            var newModel = new DefaultComboBoxModel<>(availablePlaces.get(selectedItem));
            citySelectionComboBox.setModel(newModel);
        });
        citySelectionPanel.add(countrySelectionComboBox);
        citySelectionComboBox = new JComboBox<>(availablePlaces.get(availablePlaces.keySet().stream().findFirst().get()));
        citySelectionPanel.add(citySelectionComboBox);
        var citySelectionButton = new Button("Select");

        citySelectionButton.addActionListener(e -> citySelectionButtonClicked());

        citySelectionPanel.add(citySelectionButton);

        districtConfigurationPanel = new JPanel();
        frame.add(districtConfigurationPanel);

        var scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        var districtScrollPane = new JScrollPane(scrollContent);
        districtScrollPane.setPreferredSize(new Dimension(300, 500));
        districtScrollPane.setBounds(300,0,300,500);
        districtScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        districtScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        districtConfigurationPanel.add(districtScrollPane);

        simulationConfigurationPanel = new JPanel();
        frame.add(simulationConfigurationPanel);

        simulationConfigurationPanel.add(new JLabel("Simulation Time Rate"));
        timeRateTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(timeRateTextField);

        // TODO Change into better input type than plain seconds
        simulationConfigurationPanel.add(new JLabel("Simulation Duration [sec]"));
        simulationDurationTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(simulationDurationTextField);

        simulationConfigurationPanel.add(new JLabel("Number of City Patrols"));
        numberOfCityPatrolsTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(numberOfCityPatrolsTextField);

        simulationConfigurationPanel.add(new JLabel("Draw districts boundaries"));
        simulationConfigurationPanel.add(drawDistrictsBoundariesCheckBox);


        buttonsPanel = new JPanel();
        frame.add(buttonsPanel);
        var runSimulationButton = new Button("Run the simulation!");
        runSimulationButton.addActionListener(e -> runSimulationButtonClicked());
        buttonsPanel.add(runSimulationButton);

        // Disable further sections
        setComponentEnabledRecursively(districtConfigurationPanel, false);
        setComponentEnabledRecursively(simulationConfigurationPanel, false);
        setComponentEnabledRecursively(buttonsPanel, false);

        setDefaultValues();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setComponentEnabledRecursively(JComponent section, boolean isEnabled) {
        for (var component : section.getComponents()) {
            if (component instanceof JComponent) {
                setComponentEnabledRecursively((JComponent) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    private void citySelectionButtonClicked() {
        var cityName = citySelectionComboBox.getSelectedItem().toString();
        World.getInstance().getConfig().setCityName(cityName);
        if (loadMapIntoWorld(cityName)) {
            var scrollContent = (JPanel) ((JScrollPane)Arrays.stream(districtConfigurationPanel.getComponents()).filter(x -> x instanceof JScrollPane).findFirst().get()).getViewport().getView();
            scrollContent.removeAll();
            for (var district : World.getInstance().getMap().getDistricts()) {
                scrollContent.add(new DistrictConfigComponent(district));
            }
            scrollContent.revalidate();

            setComponentEnabledRecursively(districtConfigurationPanel, true);
            setComponentEnabledRecursively(simulationConfigurationPanel, true);
            setComponentEnabledRecursively(buttonsPanel, true);
        }
    }

    private void runSimulationButtonClicked() {
        var mapPanel = new MapPanel();
        mapPanel.createMapWindow();

        var config = World.getInstance().getConfig();
        config.setNumberOfPolicePatrols(Integer.parseInt(numberOfCityPatrolsTextField.getText()));
        config.setTimeRate(Integer.parseInt(timeRateTextField.getText()));
        config.setSimulationDuration(Long.parseLong(simulationDurationTextField.getText()));
        config.setDrawDistrictsBorders(drawDistrictsBoundariesCheckBox.isSelected());
        Logger.getInstance().logNewMessage("World config has been set.");

        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

        mapPanel.selectHQLocation();
    }

    private boolean loadMapIntoWorld(String cityName) {
        try {
            var map = ImportGraphFromRawData.createMap(cityName);
            World.getInstance().setMap(map);
        } catch (Exception e) {
            // TODO Add logger
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
