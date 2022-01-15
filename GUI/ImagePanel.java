package GUI;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
    Image image;
    public void setBackground(Image image) {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics G) {
        super.paintComponent(G);
        G.drawImage(image, 0, 0, this.getWidth(), this.getHeight() + 200, null);
    }
}