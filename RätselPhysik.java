import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RätselPhysik extends JPanel {
    private JTextField[] fields = new JTextField[4];
    private JButton commitButton;

    public RätselPhysik(JFrame frame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Eingabefelder
        for (int i = 0; i < 4; i++) {
            fields[i] = new JTextField(1);
            fields[i].setFont(new Font("Monospaced", Font.BOLD, 32));
            fields[i].setHorizontalAlignment(JTextField.CENTER);
            gbc.gridx = i;
            gbc.gridy = 0;
            add(fields[i], gbc);
        }

        // Commit-Button
        commitButton = new JButton("Commit");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        add(commitButton, gbc);

        // Button-Action
        commitButton.addActionListener(e -> {
            StringBuilder code = new StringBuilder();
            for (JTextField field : fields) {
                code.append(field.getText().trim());
            }
            if ("2479".equals(code.toString())) {
                // Wechsel zu PhysikSaal
                frame.setContentPane(new PhysikSaal());
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fenster maximieren
                frame.setUndecorated(true); // Optional: Rahmen entfernen für echtes Vollbild
                frame.validate();
            } else {
                JOptionPane.showMessageDialog(this, "Falscher Code!", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}