import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Map extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;

    private List<Image> backgrounds;
    private int currentBackgroundIndex = 0;
    private int playerX = 50, playerY = 50;
    private final int PLAYER_SIZE = 20;

    public Map() {
        super();
        setLayout(null);
        setBackground(Color.RED);

        // Load images (replace with your actual image paths)
        backgrounds = new ArrayList<>();
        backgrounds.add(Toolkit.getDefaultToolkit().createImage("./img/test.jpg"));
        backgrounds.add(Toolkit.getDefaultToolkit().createImage("background1.jpg"));
        backgrounds.add(Toolkit.getDefaultToolkit().createImage("background2.jpg"));


        setFocusable(true);
        setPreferredSize(new Dimension(400, 300)); // Adjust as needed

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_LEFT -> playerX = Math.max(0, playerX - 5);
                    case KeyEvent.VK_RIGHT -> playerX = Math.min(getWidth() - PLAYER_SIZE, playerX + 5);
                    case KeyEvent.VK_UP -> playerY = Math.max(0, playerY - 5);
                    case KeyEvent.VK_DOWN -> playerY = Math.min(getHeight() - PLAYER_SIZE, playerY + 5);
                    case KeyEvent.VK_SPACE -> currentBackgroundIndex = (currentBackgroundIndex + 1) % backgrounds.size();
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw current background
        if (!backgrounds.isEmpty()) {
            g.drawImage(backgrounds.get(currentBackgroundIndex), 0, 0, getWidth(), getHeight(), this);
        }
        // Draw player as a blue circle
        g.setColor(Color.BLUE);
        g.fillOval(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
    }
}