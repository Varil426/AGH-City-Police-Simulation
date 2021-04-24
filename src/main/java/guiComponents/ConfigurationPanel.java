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

public class ConfigurationPanel extends JFrame {

    private JFrame frame;
    private JPanel mainPanel;

    private JPanel citySelectionPanel;
    private JPanel districtConfigurationPanel;

   /*DocumentListener documentListener = new DocumentListener() {
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
    };*/

    public void createWindow(){
        frame = new JFrame("City Police Simulation");
        frame.setSize( 1200, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        /*mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1,4));*/

        frame.setLayout(new GridLayout(1,4));

        citySelectionPanel = new JPanel();
        frame.add(citySelectionPanel);

        // TODO Color to be removed
        citySelectionPanel.setBackground(Color.darkGray);

        citySelectionPanel.setLayout(new GroupLayout(citySelectionPanel));

        districtConfigurationPanel = new JPanel();
        frame.add(districtConfigurationPanel);

        var scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        for (int i = 0; i < 300; i++) {
            scrollContent.add(new JButton("Test-"+i));
        }

        var districtScrollPane = new JScrollPane(scrollContent);
        districtScrollPane.setPreferredSize(new Dimension(300, 500));
        districtScrollPane.setBounds(300,0,300,500);
        districtScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        districtScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        districtConfigurationPanel.add(districtScrollPane);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    /*private void checkCorrectValue(String text) {
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
    }*/
}
