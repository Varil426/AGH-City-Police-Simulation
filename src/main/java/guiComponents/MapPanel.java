package guiComponents;

import World.World;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.util.HashSet;

public class MapPanel extends JFrame {

    private JFrame frame = new JFrame();
    private JXMapViewer mapViewer = new JXMapViewer();

    public MapPanel() {
        var info = new OSMTileFactoryInfo();
        var tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addMouseMotionListener(new PanMouseInputListener(mapViewer));
    }

    public void createMapWindow() {
        frame.setSize( 1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        var map = World.getInstance().getMap();
        var minCoordinates = new GeoPosition(
                map.getGraph().vertexSet().stream().map(x -> x.getPosition().getLatitude()).min(Double::compare).get(),
                map.getGraph().vertexSet().stream().map(x -> x.getPosition().getLongitude()).min(Double::compare).get());

        var maxCoordinates = new GeoPosition(
                map.getGraph().vertexSet().stream().map(x -> x.getPosition().getLatitude()).max(Double::compare).get(),
                map.getGraph().vertexSet().stream().map(x -> x.getPosition().getLongitude()).max(Double::compare).get());

        mapViewer.setAddressLocation(new GeoPosition(
                (minCoordinates.getLatitude() + maxCoordinates.getLatitude())/2,
                (minCoordinates.getLongitude() + maxCoordinates.getLongitude())/2
        ));

        // TODO Add automatic zoom calculation
        /*mapViewer.calculateZoomFrom(new HashSet<>() {
            {
                add(minCoordinates);
                add(maxCoordinates);
            }
        });*/
        //mapViewer.setZoom(mapViewer.getZoom() / 2);
        //mapViewer.setZoom(mapViewer.getZoom());
        mapViewer.setZoom(7);

        frame.getContentPane().add(mapViewer);
        frame.setVisible(true);
    }

}
