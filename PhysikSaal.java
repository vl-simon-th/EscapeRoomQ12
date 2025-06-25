import java.awt.*;
import javax.swing.*;

public class PhysikSaal extends JPanel {

    private Image background;

    public PhysikSaal() {
        background = new ImageIcon("img/test.jpg").getImage();
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.RED);
            g.drawString("Hintergrundbild fehlt!", 10, 20);
        }
    }
}
