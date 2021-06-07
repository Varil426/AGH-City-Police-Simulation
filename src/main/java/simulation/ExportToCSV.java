package simulation;

import World.World;
import com.opencsv.CSVWriter;
import entities.Firing;
import entities.Incident;
import entities.Intervention;
import entities.Patrol;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.stream.Collectors;

public class ExportToCSV extends Thread {

    private final World world = World.getInstance();
    private final File csvFile;
    private final String csvDirectoryPath = "results";
    private final String[] header = new String[]{
            "simulationTime",
            "amountOfPatrols",
            "amountOfPatrollingPatrols",
            "amountOfCalculatingPathPatrols",
            "amountOfTransferToInterventionPatrols",
            "amountOfTransferToFiringPatrols",
            "amountOfInterventionPatrols",
            "amountOfFiringPatrols",
            "amountOfNeutralizedPatrols",
            "amountOfReturningToHqPatrols",
            "amountOfIncidents",
            "amountOfInterventions",
            "amountOfInterventionsBeingSolving",
            "amountOfFirings",
            "amountOfFiringBeingSolving"
    };
    private final DateTimeFormatter dateFormat = new DateTimeFormatterBuilder().appendPattern("dd-MM-yyyy_HH-mm-ss").toFormatter();
    private int exportCounter = 1;
    private final int neutralizedPatrolsTotal = 0;

    public ExportToCSV() {
        File csvDirectory = new File(csvDirectoryPath);
        if (!(csvDirectory.exists() && csvDirectory.isDirectory())) {
            csvDirectory.mkdir();
        }

        csvFile = new File(csvDirectoryPath + "/" + dateFormat.format(LocalDateTime.now()) + ".csv");
        try {
            csvFile.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));
            csvWriter.writeNext(header);
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!world.hasSimulationDurationElapsed()) {
            if (!world.isSimulationPaused() && exportCounter <= (world.getSimulationTimeLong() / 600)) {
                exportCounter++;
                var allEntities = world.getAllEntities();
                var allPatrols = allEntities.stream()
                        .filter(x -> x instanceof Patrol)
                        .map(x -> (Patrol) x)
                        .collect(Collectors.toList());
                var allIncidents = allEntities.stream()
                        .filter(x -> x instanceof Incident && ((Incident) x).isActive())
                        .map(x -> (Incident) x)
                        .collect(Collectors.toList());
                var simulationTimeLong = world.getSimulationTimeLong();

                try {
                    var csvWriter = new CSVWriter(new FileWriter(csvFile, true));
                    csvWriter.writeNext(new String[]{
                            String.valueOf(simulationTimeLong),
                            String.valueOf(allPatrols.size()),
                            String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.PATROLLING).count()),
                            String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.CALCULATING_PATH).count()),
                            String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.TRANSFER_TO_INTERVENTION).count()),
                            String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.TRANSFER_TO_FIRING).count()),
                            String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.INTERVENTION).count()),
                            String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.FIRING).count()),
                            String.valueOf(world.getNeutralizedPatrolsTotal() + allPatrols.stream().filter(x -> x.getState() == Patrol.State.NEUTRALIZED).count()),
                            String.valueOf(allPatrols.stream().filter(x -> x.getState() == Patrol.State.RETURNING_TO_HQ).count()),
                            String.valueOf(allIncidents.size()),
                            String.valueOf(allIncidents.stream().filter(x -> x instanceof Intervention).count()),
                            String.valueOf(allIncidents.stream().filter(x -> x instanceof Intervention && ((Intervention) x).getPatrolSolving() != null).count()),
                            String.valueOf(allIncidents.stream().filter(x -> x instanceof Firing).count()),
                            String.valueOf(allIncidents.stream().filter(x -> x instanceof Firing && ((Firing) x).getPatrolsSolving().size() > 0).count())
                    }, false);
                    csvWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // sleep for next 10 minutes in simulation time
                var sleepTime = ((600 - (world.getSimulationTime() % 600)) * 1000) / world.getConfig().getTimeRate();
                try {
                    sleep((long) sleepTime, (int) ((sleepTime - (long) sleepTime) * 1000000));
                } catch (InterruptedException e) {
                    // Ignore
                }
            }
        }
    }
}
