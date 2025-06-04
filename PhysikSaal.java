import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
public class PhysikSaal extends JPanel {
    public PhysikSaal() {
        Image backround = Toolkit.getDefaultToolkit().createImage();
        this.drawImage((background, 0, 0 null));
        setBackground(Color.YELLOW);
        setMaximumSize(new java.awt.Dimension(100, 100)); // Replace 800, 600 with your desired width and height
    }
}