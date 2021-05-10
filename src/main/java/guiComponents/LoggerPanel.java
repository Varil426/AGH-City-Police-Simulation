package guiComponents;

import javax.swing.*;
import java.time.LocalDateTime;

public class LoggerPanel {

    private final JFrame frame = new JFrame("Logger");
    private final JPanel scrollContent = new JPanel();
    private final JScrollPane scrollPane = new JScrollPane(scrollContent);

    public void createWindow() {
        frame.setSize( 400, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setSize(400, 800);
        frame.add(scrollPane);

        frame.setVisible(true);
    }

    public void showNewLogMessage(String message, LocalDateTime realWorldDate, long simulationTime) {
        var messageComponent = new LoggerMessageComponent(message, realWorldDate, simulationTime);
        scrollContent.add(messageComponent);
        frame.revalidate();

        var scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
    }

}
