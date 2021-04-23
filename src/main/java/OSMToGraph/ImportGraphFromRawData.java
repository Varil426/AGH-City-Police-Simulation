package OSMToGraph;

import de.westnordost.osmapi.map.MapDataParser;
import de.westnordost.osmapi.map.OsmMapDataFactory;
import de.westnordost.osmapi.map.data.*;
import entities.District;
import math.geom2d.polygon.SimplePolygon2D;
import org.apache.commons.io.FileUtils;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import utils.Haversine;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImportGraphFromRawData {

    static String defaultCityName = "Kraków";
    static String rawDataFile1 = "export";
    static String rawDataFile2 = "Raw.osm";
    static String rawDataDistrictFile1 = "export";
    static String rawDataDistrictFile2 = "DistrictsRaw.osm";
    static String graphExportFile = "KrkGraph.gv";
    static String rawDataPath = "src/main/java/OSMToGraph/rawData/";
    static String graphExportPath = "src/main/java/OSMToGraph/exportedGraphs/";
    // area["admin_level"=6][name="Kraków"]->.a;(way(area.a)["highway"~"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|motorway_link|trunk_link|primary_link|secondary_link|tertiary_link|living_street|service|pedestrian|track|road)$"]["crossing"!~"."]["name"];);out meta;>;out meta qt;
    static String query1 = "area[\"admin_level\"=6][name=\"";
    static String query2 = "\"]->.a;(way(area.a)[\"highway\"~\"^(motorway|trunk|primary|secondary|tertiary|unclassified|residential|motorway_link|trunk_link|primary_link|secondary_link|tertiary_link|living_street|service|pedestrian|track|road)$\"][\"crossing\"!~\".\"][\"name\"];);out meta;>;out meta qt;";
    static String apiURL = "https://overpass-api.de/api/interpreter";
    // area["admin_level"=6][name="Kraków"]->.a;(relation(area.a)["admin_level"=9][boundary=administrative]["name"];);out meta;>;out meta qt;
    static String queryDistrict1 = "area[\"admin_level\"=6][name=\"";
    static String queryDistrict2 = "]->.a;(relation(area.a)[\"admin_level\"=9][boundary=administrative][\"name\"];);out meta;>;out meta qt;";


    public static void main(String[] args) throws IOException, InterruptedException {

//        // example of file-based data handling: (It takes about 2 seconds)
//        ParsingMapDataHandler dataHandler = handleRawDataFromFile(defaultCityName);
//
//        // example of request-based data handling: (It takes about 16 seconds)
////        ParsingMapDataHandler dataHandler = handleRawDataFromRequest();
//
//        Graph<Node, ImportedEdge> graph = dataHandler.getGraph();

        entities.Map map = createMap(defaultCityName);
        Graph<Node, ImportedEdge> graph = map.getGraph();
        HashMap<Long, Node> myNodes = map.getMyNodes();

        // example of calculating the route between two points (the result is a GraphPath)
        // and the distance between these points
        //START
//        HashMap<Long, Node> myNodes = dataHandler.getNodesMap();
        AStarShortestPath<Node, ImportedEdge> path = new AStarShortestPath<>(graph, new Haversine.ownHeuristics());
        GraphPath<Node, ImportedEdge> path1 = path.getPath(myNodes.get(3195641657L), myNodes.get(244399516L));
        System.out.println(path1.getEdgeList());
        System.out.println(path1.getWeight());
        //END

        // an example of searching for the nearest node for a selected point
        //START
        LatLon latlon = new OsmLatLon(80.001769, 100.8174569);
        Long nearestNodeId = findNearestNode(latlon, myNodes);
        System.out.println(nearestNodeId);
        //END
    }

    public static entities.Map createMap(String cityName) throws IOException, InterruptedException {
        ParsingMapDataHandler dataHandler = handleRawData(rawDataPath+rawDataFile1 + cityName + rawDataFile2, query1 + cityName + query2, cityName, false);
        ParsingMapDataHandler districtDataHandler = handleRawData(rawDataPath+rawDataDistrictFile1 + cityName + rawDataDistrictFile2, queryDistrict1 + cityName + queryDistrict2, cityName, true);

        System.out.println(districtDataHandler.getDistricts().size());
        List<District> districts = districtDataHandler.getDistricts();

//        List<RelationMember> members = districtDataHandler.getRelations().get(0).getMembers();
//        Map<String, String> tags = districtDataHandler.getRelations().get(0).getTags();
//        System.out.println(Arrays.toString(members.toArray()));
//        System.out.println(districtDataHandler.getRelations().get(0).getId());
//
//        for (Map.Entry<String, String> entry : tags.entrySet()) {
//            System.out.print(entry.getKey() + ":" + entry.getValue().toString()+" ; ");
//        }

//        List<RelationMember> members = districtDataHandler.getRelations().get(0).getMembers();
//        RelationMember relationMember = members.get(0);
//        System.out.println(relationMember.getRef() + " ; " + relationMember.getRole() + " " + relationMember.getType());


        BoundingBox boundingBox = new BoundingBox(dataHandler.getMinLatitude(), dataHandler.getMinLongitude(), dataHandler.getMaxLatitude(), dataHandler.getMaxLongitude());

        // exporting the graph to a DOT file:
        ImportedGraphToDOT.exportGraphToFile(dataHandler.getGraph(), graphExportPath + graphExportFile, dataHandler);

        return new entities.Map(dataHandler.getGraph(), dataHandler.getNodesMap(), boundingBox, districts);
    }

    public static ParsingMapDataHandler handleRawData(String rawDataFilePath, String query, String cityName, boolean districtData) throws IOException, InterruptedException {
        ParsingMapDataHandler dataHandler;
        try {
            InputStream fin = new FileInputStream(rawDataFilePath);
            dataHandler = handleInputStream(cityName, fin, true, districtData);
            // close the file:
            fin.close();
        } catch (FileNotFoundException | InterruptedException e) {
            dataHandler = handleRawDataFromRequest(query, cityName, districtData);
        }
        return dataHandler;
    }

    public static ParsingMapDataHandler handleRawDataFromRequest(String query, String cityName, boolean districtData) throws IOException, InterruptedException {
        HttpURLConnection urlConn = makeRequest(apiURL,query);
        InputStream inputStream = urlConn.getInputStream();
        return handleInputStream(cityName, inputStream, false, districtData);
    }

    public static ParsingMapDataHandler handleInputStream(String cityName, InputStream inputStream, boolean doesFileExist, boolean districtData) throws IOException, InterruptedException {

        // TODO dodać jakiś konwerter do pliku wejściowego z surowymi danymi
        //  obecnie ręcznie konwertuję w Notepad++ z UTF-8 BOM na UTF-8
        //  UPDATE: chyba już po problemie, zostawię to, żeby pamiętać na wszelki

        BufferedInputStream bin;

        if (!doesFileExist) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            inputStream.transferTo(baos);
            InputStream firstClone = new ByteArrayInputStream(baos.toByteArray());
            InputStream secondClone = new ByteArrayInputStream(baos.toByteArray());

            writeRawDataToFile(cityName, firstClone, districtData);

            bin = new BufferedInputStream(secondClone);
        } else {
            bin = new BufferedInputStream(inputStream);
        }

        ParsingMapDataHandler dataHandler = new ParsingMapDataHandler();
        OsmMapDataFactory factory = new OsmMapDataFactory();
        MapDataParser mapDataParser = new MapDataParser(dataHandler, factory);
        mapDataParser.parse(bin);
        return dataHandler;
    }

    public static void writeRawDataToFile(String cityName, InputStream inputStream, boolean districtData) throws IOException {
        File myObj;
        if (districtData){
            myObj = new File(rawDataPath + rawDataDistrictFile1 + cityName + rawDataDistrictFile2);
        }else{
            myObj = new File(rawDataPath + rawDataFile1 + cityName + rawDataFile2);
        }
        FileUtils.copyInputStreamToFile(inputStream, myObj);
    }


    public static HttpURLConnection makeRequest(String apiURL, String query) throws IOException {
        URL url = new URL(apiURL);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestMethod("POST");
        urlConn.setDoOutput(true);
        urlConn.setRequestProperty("data", query);
        urlConn.setRequestProperty("Content-Length", Integer.toString(query.length()));
        urlConn.getOutputStream().write(query.getBytes(StandardCharsets.UTF_8));
        return urlConn;
    }

    public static Long findNearestNode(LatLon point, HashMap<Long, Node> myNodes) {
        double distance = Double.MAX_VALUE;
        Long id = null;
        for (Map.Entry<Long, Node> me : myNodes.entrySet()) {
            LatLon nodePosition = me.getValue().getPosition();
            double tmpDistance = Haversine.distance(point.getLatitude(), point.getLongitude(), nodePosition.getLatitude(), nodePosition.getLongitude());
            if (tmpDistance < distance) {
                distance = tmpDistance;
                id = me.getValue().getId();
            }
        }
        System.out.println(distance);
        return id;
    }
}
