import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

//VERY IMPORTANT
// Maps need to match 5000x3500 pixels

public class Map extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;

    private final List<Image> maps;
    private int currentMapIndex = 0;
    private int playerX = 500, playerY = 500;
    private final int PLAYER_SIZE = 50;

    private int mapTotalWidth = 3000; // Replace with your desired width
    private int mapTotalHeight = 3000; // Replace with your desired height

    // Viewport (camera) position and zoom
    private int viewportX = 0;
    private int viewportY = 0;
    private double zoom = 1.0;

    double zoomMax = 2.5;
    double zoomMin = 0.3;

    public Map() {
        super();
        setLayout(null);
        setBackground(Color.GRAY);

        // Load images (replace with your actual image paths)
        maps = new ArrayList<>();
        maps.add(Toolkit.getDefaultToolkit().createImage("./mapImg/Map.png"));

        setFocusable(true);
        setPreferredSize(new Dimension(1000, 750)); // Match map size

        // Mouse drag state for right mouse button
        final int[] lastMouseX = {0};
        final int[] lastMouseY = {0};
        final boolean[] dragging = {false};

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) { // Right mouse button
                    dragging[0] = true;
                    lastMouseX[0] = e.getX();
                    lastMouseY[0] = e.getY();
                }
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    dragging[0] = false;
                }
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (dragging[0]) {
                    int dx = (int)((lastMouseX[0] - e.getX()) / zoom);
                    int dy = (int)((lastMouseY[0] - e.getY()) / zoom);
                    viewportX = Math.max(0, Math.min(mapTotalWidth - (int)(getWidth() / zoom), viewportX + dx));
                    viewportY = Math.max(0, Math.min(mapTotalHeight - (int)(getHeight() / zoom), viewportY + dy));
                    lastMouseX[0] = e.getX();
                    lastMouseY[0] = e.getY();
                    repaint();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                int panStep = (int)(20 / zoom); // Pan step scales with zoom
                int playerStep = 5;
                switch (key) {
                    // Panning (move viewport)
                    case KeyEvent.VK_LEFT -> viewportX = Math.max(0, viewportX - panStep);
                    case KeyEvent.VK_RIGHT -> viewportX = Math.min(mapTotalWidth - (int)(getWidth() / zoom), viewportX + panStep);
                    case KeyEvent.VK_UP -> viewportY = Math.max(0, viewportY - panStep);
                    case KeyEvent.VK_DOWN -> viewportY = Math.min(mapTotalHeight - (int)(getHeight() / zoom), viewportY + panStep);
                    // Zooming
                    case KeyEvent.VK_PLUS, KeyEvent.VK_EQUALS -> {
                        if (zoom < zoomMax) {
                            zoom += 0.1;
                            // Clamp viewport to map bounds after zoom
                            viewportX = Math.min(viewportX, mapTotalWidth - (int)(getWidth() / zoom));
                            viewportY = Math.min(viewportY, mapTotalHeight - (int)(getHeight() / zoom));
                        }
                    }
                    case KeyEvent.VK_MINUS -> {
                        if (zoom > zoomMin) {
                            zoom -= 0.1;
                            // Clamp viewport to map bounds after zoom
                            viewportX = Math.min(viewportX, mapTotalWidth - (int)(getWidth() / zoom));
                            viewportY = Math.min(viewportY, mapTotalHeight - (int)(getHeight() / zoom));
                        }
                    }
                    // Player movement (on map)
                    case KeyEvent.VK_A -> playerX = Math.max(0, playerX - playerStep);
                    case KeyEvent.VK_D -> playerX = Math.min(mapTotalWidth - PLAYER_SIZE, playerX + playerStep);
                    case KeyEvent.VK_W -> playerY = Math.max(0, playerY - playerStep);
                    case KeyEvent.VK_S -> playerY = Math.min(mapTotalHeight - PLAYER_SIZE, playerY + playerStep);
                    // Change map
                    case KeyEvent.VK_SPACE -> currentMapIndex = (currentMapIndex + 1) % maps.size();
                }
                repaint();
            }
        });
        addMouseWheelListener((java.awt.event.MouseWheelEvent e) -> {
            int notches = e.getWheelRotation();
            double oldZoom = zoom;
            if (notches < 0 && zoom < zoomMax) {
                zoom += 0.1;
            } else if (notches > 0 && zoom > zoomMin) {
                zoom -= 0.1;
            }
            // Keep the mouse position fixed relative to the map when zooming
            int mouseX = e.getX();
            int mouseY = e.getY();
            double relX = (mouseX / oldZoom) + viewportX;
            double relY = (mouseY / oldZoom) + viewportY;
            viewportX = (int)(relX - mouseX / zoom);
            viewportY = (int)(relY - mouseY / zoom);
            // Clamp viewport
            int srcW = (int)(getWidth() / zoom);
            int srcH = (int)(getHeight() / zoom);
            viewportX = Math.max(0, Math.min(viewportX, mapTotalWidth - srcW));
            viewportY = Math.max(0, Math.min(viewportY, mapTotalHeight - srcH));
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!maps.isEmpty()) {
            int srcW = (int)(getWidth() / zoom);
            int srcH = (int)(getHeight() / zoom);
            // Clamp viewport to map bounds
            
            // Center map if smaller than visible space
            int centerX = Math.max(0, (srcW - mapTotalWidth) / 2);
            int centerY = Math.max(0, (srcH - mapTotalHeight) / 2);

            // Clamp viewport to map bounds
            viewportX = Math.max(-centerX, Math.min(viewportX, mapTotalWidth - srcW));
            viewportY = Math.max(-centerY, Math.min(viewportY, mapTotalHeight - srcH));
            
            g.drawImage(
                maps.get(currentMapIndex),
                0, 0, getWidth(), getHeight(), // Destination rectangle (panel)
                viewportX, viewportY, viewportX + srcW, viewportY + srcH, // Source rectangle (image)
                this
            );
        }
        // Draw player as a blue circle (relative to map, then to viewport)
        int playerScreenX = (int)((playerX - viewportX) * zoom);
        int playerScreenY = (int)((playerY - viewportY) * zoom);
        int playerScreenSize = (int)(PLAYER_SIZE * zoom);
        g.setColor(Color.BLUE);
        g.fillOval(playerScreenX, playerScreenY, playerScreenSize, playerScreenSize);
    }
}