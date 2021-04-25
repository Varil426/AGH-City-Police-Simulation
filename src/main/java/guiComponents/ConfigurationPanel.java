package guiComponents;


import OSMToGraph.ImportGraphFromRawData;
import World.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ConfigurationPanel extends JFrame {

    private JFrame frame;

    private JPanel citySelectionPanel;
    private JPanel districtConfigurationPanel;
    private JPanel simulationConfigurationPanel;
    private JPanel buttonsPanel;

    private final int textInputColumns = 20;
    // TODO Change into dynamically generated
    private final String[] availableCountries = {"Poland", "Great Britain"};
    private final String[] availableCities = {"Kraków", "Warszawa", "Rzeszów"};

    private JComboBox<String> countrySelectionComboBox;
    private JComboBox<String> citySelectionComboBox;
    private JTextField numberOfCityPatrolsTextField;
    private JTextField timeRateTextField;
    private JTextField simulationDurationTextField;

    // TODO Add validation for input data
    public void createWindow(){
        frame = new JFrame("City Police Simulation");
        frame.setSize( 1200, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.setLayout(new GridLayout(1,4));

        citySelectionPanel = new JPanel();
        frame.add(citySelectionPanel);

        countrySelectionComboBox = new JComboBox<>(availableCountries);
        citySelectionPanel.add(countrySelectionComboBox);
        citySelectionComboBox = new JComboBox<>(availableCities);
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
        timeRateTextField = new JTextField();
        timeRateTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(timeRateTextField);

        // TODO Change into better input type than plain seconds
        simulationConfigurationPanel.add(new JLabel("Simulation Duration [sec]"));
        simulationDurationTextField = new JTextField();
        simulationDurationTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(simulationDurationTextField);

        simulationConfigurationPanel.add(new JLabel("Number of City Patrols"));
        numberOfCityPatrolsTextField = new JTextField();
        numberOfCityPatrolsTextField.setColumns(textInputColumns);
        simulationConfigurationPanel.add(numberOfCityPatrolsTextField);

        buttonsPanel = new JPanel();
        frame.add(buttonsPanel);
        var runSimulationButton = new Button("Run the simulation!");
        runSimulationButton.addActionListener(e -> runSimulationButtonClicked());
        buttonsPanel.add(runSimulationButton);

        // Disable further sections
        setComponentEnabledRecursively(districtConfigurationPanel, false);
        setComponentEnabledRecursively(simulationConfigurationPanel, false);
        setComponentEnabledRecursively(buttonsPanel, false);

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
        World.getInstance().setConfig(new WorldConfiguration(cityName));
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
        // TODO
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
