package OSMToGraph;

import de.westnordost.osmapi.map.MapDataParser;
import de.westnordost.osmapi.map.OsmMapDataFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileBasedImportTest {
    public static void main(String[] args) throws IOException {

        final String rawDataFile = "exportKrkRaw.osm";
        final String graphExportFile = "KrkGraph.gv";

        FileInputStream fin = new FileInputStream("src/OSMToGraph/rawData/" + rawDataFile);
        BufferedInputStream bin = new BufferedInputStream(fin);

        ParsingMapDataHandler dataHandler = new ParsingMapDataHandler();
        OsmMapDataFactory factory = new OsmMapDataFactory();
        MapDataParser mapDataParser = new MapDataParser(dataHandler, factory);
        mapDataParser.parse(bin);

        ImportedGraphToDOT.exportOSMGraphToFile(dataHandler.getGraph(), "src/OSMToGraph/exportedGraphs/" + graphExportFile, dataHandler);

        // close the file
        fin.close();
    }
}
