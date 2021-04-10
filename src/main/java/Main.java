import guiComponents.Panel;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import java.awt.*;

public class Main {

    /**
     * Entry point of the application.
     * @param args params passed to the application.
     */
    public static void main(String[] args) {
        Panel panel = new Panel();
        panel.createWindow();
    }
}
