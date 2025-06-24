import java.awt.*;
import javax.swing.*;

public class PhysikSaal extends JPanel {

    private Image background;

    public PhysikSaal() {
        // Versuche, Bild aus dem Ressourcenpfad zu laden
        try {
            background = new ImageIcon(getClass().getResource("/img/test.png")).getImage();
        } catch (Exception e) {
            System.err.println("Bild konnte nicht geladen werden: " + e.getMessage());
        }

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
