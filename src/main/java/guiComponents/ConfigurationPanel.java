package guiComponents;


import javax.swing.*;
import java.awt.*;

public class ConfigurationPanel extends JFrame {

    private JFrame frame;

    private JPanel citySelectionPanel;
    private JPanel districtConfigurationPanel;
    private JPanel simulationConfigurationPanel;
    private JPanel buttonsPanel;

    private final int textInputColumns = 20;
    // TODO Change into dynamically generated
    private final String[] availableCountries = {"Poland", "Great Britain"};
    private final String[] availableCities = {"Kraków", "Warszawa"};

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
        citySelectionPanel.add(new Button("Select"));

        districtConfigurationPanel = new JPanel();
        frame.add(districtConfigurationPanel);

        var scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        // TODO Change to district settings
        for (int i = 0; i < 300; i++) {
            scrollContent.add(new JButton("Test-"+i));
        }

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
        buttonsPanel.add(new Button("Run the simulation!"));

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
