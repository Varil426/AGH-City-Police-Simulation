package guiComponents;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class LoggerMessageComponent extends JPanel {

    private static DateTimeFormatter dateFormat = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss dd MM yyyy").toFormatter();

    private final String message;
    private final LocalDateTime realWorldDate;
    private final long simulationTime;

    private final JLabel messageLabel;
    private final JLabel realWorldDateLabel;
    private final JLabel simulationTimeLabel;

    public LoggerMessageComponent(String message, LocalDateTime realWorldDate, long simulationTime) {
        this.message = message;
        messageLabel = new JLabel(message);

        this.realWorldDate = realWorldDate;
        realWorldDateLabel = new JLabel(dateFormat.format(realWorldDate));

        this.simulationTime = simulationTime;
        simulationTimeLabel = new JLabel(Long.toString(simulationTime));

        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        add(messageLabel);
        add(realWorldDateLabel);
        add(simulationTimeLabel);
    }

}
