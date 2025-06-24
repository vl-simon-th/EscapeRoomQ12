import java.awt.*;
import javax.swing.*;

public class PhysikSaal extends JPanel {

    private Image background;

    public PhysikSaal() {
       
        this.background = new ImageIcon("img/test.png").getImage();

        
        setBackground(Color.YELLOW);

        // Optional: maximale Größe festlegen
        setPreferredSize(new Dimension(800, 600)); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

       
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
