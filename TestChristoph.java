import javax.swing.*;

public class TestChristoph {
    public static void main(String[] args) {
        // Fenster erzeugen
        JFrame frame = new JFrame("RätselPhysik Test");

        // Panel hinzufügen
        frame.setContentPane(new RätselPhysik(frame));

        // Größe automatisch anpassen (nimmt preferredSize vom Panel)
        frame.pack();

        // Fenster zentrieren
        frame.setLocationRelativeTo(null);

        // Standard-Schließen-Verhalten
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Jetzt sichtbar machen
        frame.setVisible(true);
    }
}
