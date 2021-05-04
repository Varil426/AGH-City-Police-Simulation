package guiComponents;

import World.World;
import entities.Headquarters;
import entities.IDrawable;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import simulation.SimulationThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MapPanel extends JFrame {

    class MapPainter implements Painter<JXMapViewer> {

        @Override
        public void paint(Graphics2D g, JXMapViewer mapViewer, int width, int height) {
            World.getInstance().getAllEntities().stream().filter(x -> x instanceof IDrawable).forEach(x -> ((IDrawable) x).drawSelf(g, mapViewer));
            if (World.getInstance().getConfig().isDrawDistrictsBorders()) {
                World.getInstance().getMap().getDistricts().forEach(x -> x.drawSelf(g, mapViewer));
            }
        }
    }

    private JFrame frame = new JFrame();
    private JXMapViewer mapViewer = new JXMapViewer();

    public MapPanel() {
        var info = new OSMTileFactoryInfo();
        var tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addMouseMotionListener(new PanMouseInputListener(mapViewer));

        mapViewer.setOverlayPainter(new MapPainter());
    }

    public void createMapWindow() {
        frame.setSize( 1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        var position = World.getInstance().getPosition();

        mapViewer.setAddressLocation(new GeoPosition(position.getLatitude(), position.getLongitude()));

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

    public void selectHQLocation() {
        mapViewer.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var position = mapViewer.convertPointToGeoPosition(e.getPoint());

                var HQ = new Headquarters(position.getLatitude(), position.getLongitude());
                World.getInstance().addEntity(HQ);

                // GUI Drawing thread
                new Thread(() -> {
                    while (true) {
                        // TODO Exit condition
                        mapViewer.repaint();
                        try {
                            Thread.sleep(1000/30);
                        } catch (Exception exception) {
                            // Ignore
                        }
                    }
                }).start();

                // Simulation thread
                new SimulationThread().start();

                mapViewer.removeMouseListener(this);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        JOptionPane.showMessageDialog(this, "Please select HQ location.");
    }

}
