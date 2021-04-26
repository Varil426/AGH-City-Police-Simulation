package guiComponents;

import entities.District;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class DistrictConfigComponent extends JPanel {

    private District district;

    private JTextField districtThreatLevelInput;

    public DistrictConfigComponent(District districtName) {
        this.district = districtName;
        districtThreatLevelInput = new JTextField();
        districtThreatLevelInput.setColumns(5);
        districtThreatLevelInput.setText("3");

        districtThreatLevelInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                inputChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                inputChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                inputChanged();
            }
        });

        this.add(new JLabel(this.district.getName()));
        this.add(districtThreatLevelInput);
    }

    private void inputChanged() {
        var newText = districtThreatLevelInput.getText();
        try {
            var value = Integer.parseInt(newText);
            district.setThreatLevel(value);
            districtThreatLevelInput.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        } catch (IllegalArgumentException exception) {
            districtThreatLevelInput.setBorder(new LineBorder(Color.RED, 2));
        }
    }

}
