package guiComponents;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Panel extends JFrame implements ItemListener {
//    String coutries [] = {"Polska", "Niemcy", "USA"};
//    private JComboBox<String> countryList = new JComboBox<>(coutries);
//    String cities [] = {"Kraków", "Warszawa", "Katowice", "Łódź"};

    String [] cities = {"Kraków","Warszawa"};
    private JComboBox<String> cityList = new JComboBox<>(cities);
    JFrame frame;
    JPanel mainPanel;

    DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            try{
                checkCorrectValue(e.getDocument().getText(0,e.getDocument().getLength()));
            }
            catch (Exception e1){
                //TODO logger
                e1.printStackTrace();
            }

        }
        @Override
        public void removeUpdate(DocumentEvent e) {
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            try{
                checkCorrectValue(e.getDocument().getText(0,e.getDocument().getLength()));
            }
            catch (Exception e1){
                //TODO logger
                e1.printStackTrace();
            }
        }
    };

    public void createWindow(){
        this.frame = new JFrame("Police Simulation");
        frame.setSize( 1100, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));

        //cityList
        cityList.addItemListener(this);
        JLabel label2 = new JLabel("Select city:");
        label2.setForeground(Color.blue);
        this.mainPanel = new JPanel(new GridLayout(1,3));
        mainPanel.add(label2);
        mainPanel.add(cityList);

        //police patrols
        JLabel label4 = new JLabel("Select police patrols (int):");
        label4.setForeground(Color.blue);
        JTextField policePatrols = new JTextField(8);
        policePatrols.getDocument().addDocumentListener(documentListener);
        JPanel panel4 = new JPanel();
        panel4.add(label4);
        panel4.add(policePatrols);

        //time Rate
        JLabel label5 = new JLabel("Select time rate (int):");
        label5.setForeground(Color.blue);
        JTextField enterTimeRate = new JTextField(8);
        enterTimeRate.getDocument().addDocumentListener(documentListener);
        JPanel panel5 = new JPanel();
        panel5.add(label5);
        panel5.add(enterTimeRate);

        //simulation Duration
        JLabel label6 = new JLabel("Select simulation time [sec]:");
        label6.setForeground(Color.blue);
        JTextField simulationDuration = new JTextField(8);
        simulationDuration.getDocument().addDocumentListener(documentListener);
        JPanel panel6 = new JPanel();
        panel6.add(label6);
        panel6.add(simulationDuration);

//        mainPanel.add(panel3);
//        mainPanel.add(panel4);
//        mainPanel.add(panel5);
//        mainPanel.add(panel6);

        //startButton
        Button start = new Button("Start");
        frame.add(mainPanel);
        frame.add(panel4);
        frame.add(panel5);
        frame.add(panel6);
        frame.add(start);

        frame.setVisible(true);

        start.addActionListener(e -> {
            //TODO create configuration and world and start simulation
        });
    }

    private void checkCorrectValue(String text) {
        try{
            var pattern = Pattern.compile("^[0-9]*$");
            if (!pattern.matcher(text).matches()){
                JOptionPane.showMessageDialog(null, "Invalid value");
            }
        }
        catch (Exception exception){
            //TODO logger
            exception.printStackTrace();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // if the state combobox is changed
        if (e.getSource() == cityList && e.getStateChange()==ItemEvent.SELECTED) {
            System.out.println("Wybrane miasto");
            addDistrictPanel();
        }
    }

    private void addDistrictPanel() {
        JLabel label = new JLabel("Select dangerous districts:");
        String[] districts = {"Krowodrza", "Stare Miasto", "Kazimierz", "Nowa Huta", "Bieńczyce", "Czyżyny", "Wola Justowska", "Podgórze"};
        ArrayList<JCheckBox> district = new ArrayList<>();
        JPanel subpanel = new JPanel(new GridLayout(5,2));
        for (String s : districts) {
            district.add(new JCheckBox(s));
        }
        subpanel.add(label);
        for (JCheckBox jCheckBox : district) {
            subpanel.add(jCheckBox);
        }
        this.mainPanel.add(subpanel);
        this.frame.setVisible(true);
    }
}
