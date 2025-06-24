import java.awt.*;
import javax.swing.*;

public class PhysikSaal extends JPanel {

    private Image background;

    public PhysikSaal() {
       
        this.background = new ImageIcon("img/test.png").getImage();

        
      

        // Optional: maximale Größe festlegen
        setPreferredSize(new Dimension(800, 600)); 
        if (background == null) {
    System.err.println("Bild konnte nicht geladen werden!");
}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

       
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
