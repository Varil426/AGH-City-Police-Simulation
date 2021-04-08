package OSMToGraph;

import org.jgrapht.Graph;
import org.jgrapht.io.DOTExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ImportedGraphToDOT {

    public static void exportOSMGraphToFile(Graph<Long, ImportedEdge> g, String fileName, ParsingMapDataHandler nodesMapProvider) {
        try {
            File outputFile = new File(fileName);
            if (!outputFile.exists()) {
                boolean newFile = outputFile.createNewFile();
                if (!newFile) throw new IOException();
            }
            ElementNameProvider<Long> vertexIDProvider = new ElementNameProvider<>(nodesMapProvider);
            VertexLabelProvider<Long> vertexLabelProvider = new VertexLabelProvider<>(nodesMapProvider);
            ElementNameProvider<ImportedEdge> edgeLabelProvider = new ElementNameProvider<>(nodesMapProvider);
            FileWriter fileWriter = new FileWriter(outputFile);
            DOTExporter<Long, ImportedEdge> dotExporter = new DOTExporter<>(vertexIDProvider,
                    vertexLabelProvider,
                    edgeLabelProvider);
            dotExporter.exportGraph(g, fileWriter);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

