package OSMToGraph;

import de.westnordost.osmapi.map.data.Node;
import org.jgrapht.Graph;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.DOTImporter;
import org.jgrapht.io.ImportException;

import java.io.*;

public class ImportedGraphToDOT {

    public static void exportGraphToFile(Graph<Node, ImportedEdge> g, String fileName, ParsingMapDataHandler nodesMapProvider) {
        try {
            File outputFile = new File(fileName);
            if (!outputFile.exists()) {
                boolean newFile = outputFile.createNewFile();
                if (!newFile) throw new IOException();
            }
            ElementNameProvider<Node> vertexIDProvider = new ElementNameProvider<>(nodesMapProvider);
            VertexLabelProvider<Node> vertexLabelProvider = new VertexLabelProvider<>(nodesMapProvider);
            ElementNameProvider<ImportedEdge> edgeLabelProvider = new ElementNameProvider<>(nodesMapProvider);
            FileWriter fileWriter = new FileWriter(outputFile);
            DOTExporter<Node, ImportedEdge> dotExporter = new DOTExporter<>(vertexIDProvider,
                    vertexLabelProvider,
                    edgeLabelProvider);
            dotExporter.exportGraph(g, fileWriter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO import grafu z pliku nie działa
    public static void importGraph(Graph<Node, ImportedEdge> graph, String fileName) throws FileNotFoundException {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        ImportVertexProvider nodeProvider = new ImportVertexProvider();
        ImportEdgeProvider edgeProvider = new ImportEdgeProvider();

        DOTImporter<Node, ImportedEdge> dotImporter = new DOTImporter<>(nodeProvider, edgeProvider);
        try {
            dotImporter.importGraph(graph, reader);
        } catch (ImportException ex) {
            ex.printStackTrace();
        }
    }
}

