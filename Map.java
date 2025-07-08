import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
    private final int PLAYER_SIZE = 25;

    private int mapTotalWidth = 3000; // Replace with your desired width
    private int mapTotalHeight = 3000; // Replace with your desired height

    // Viewport (camera) position and zoom
    private int viewportX = 0;
    private int viewportY = 0;
    private double zoom = 1.0;

    double zoomMax = 2.5;
    double zoomMin = 0.3;

    // Cache for optimized collision detection
    private java.awt.image.BufferedImage cachedMapImage;
    private int lastCachedMapIndex = -1;
    
    // Movement optimization
    private long lastMoveTime = 0;
    private static final long MOVE_COOLDOWN = 16; // ~60fps (16ms between moves)

    public Map() {
        super();
        setLayout(null);
        setBackground(Color.GRAY);
        setBackground(Color.GRAY);

        // Load images (replace with your actual image paths)
        maps = new ArrayList<>();
        Image mapImage = Toolkit.getDefaultToolkit().createImage("./mapImg/Map.png");
        maps.add(mapImage);
        
        // Wait for image to load and get actual dimensions for better collision detection
        try {
            java.awt.MediaTracker tracker = new java.awt.MediaTracker(this);
            tracker.addImage(mapImage, 0);
            tracker.waitForID(0);
            
            if (mapImage.getWidth(null) > 0 && mapImage.getHeight(null) > 0) {
                mapTotalWidth = mapImage.getWidth(null);
                mapTotalHeight = mapImage.getHeight(null);
            }
        } catch (InterruptedException e) {
            // Use default dimensions if loading fails
            System.out.println("Could not load image dimensions, using defaults");
        }

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
                
                // Handle different types of movement
                switch (key) {
                    // Panning (move viewport)
                    case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_DOWN -> 
                        panViewport(key, panStep);
                    // Zooming
                    case KeyEvent.VK_PLUS, KeyEvent.VK_EQUALS, KeyEvent.VK_MINUS -> 
                        handleZoom(key);
                    // Player movement (on map)
                    case KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S -> 
                        movePlayer(key, playerStep);
                    // Change map
                    case KeyEvent.VK_SPACE -> currentMapIndex = (currentMapIndex + 1) % maps.size();
                }
                repaint();
            }
        });
        addMouseWheelListener((java.awt.event.MouseWheelEvent e) -> {
            handleMouseWheelZoom(e);
            repaint();
        });
    }

    // Movement helper methods
    private void panViewport(int direction, int panStep) {
        switch (direction) {
            case KeyEvent.VK_LEFT -> viewportX = Math.max(0, viewportX - panStep);
            case KeyEvent.VK_RIGHT -> viewportX = Math.min(mapTotalWidth - (int)(getWidth() / zoom), viewportX + panStep);
            case KeyEvent.VK_UP -> viewportY = Math.max(0, viewportY - panStep);
            case KeyEvent.VK_DOWN -> viewportY = Math.min(mapTotalHeight - (int)(getHeight() / zoom), viewportY + panStep);
        }
    }

    private void handleZoom(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_PLUS, KeyEvent.VK_EQUALS -> {
                if (zoom < zoomMax) {
                    zoom += 0.1;
                    clampViewportAfterZoom();
                }
            }
            case KeyEvent.VK_MINUS -> {
                if (zoom > zoomMin) {
                    zoom -= 0.1;
                    clampViewportAfterZoom();
                }
            }
        }
    }

    private void clampViewportAfterZoom() {
        viewportX = Math.min(viewportX, mapTotalWidth - (int)(getWidth() / zoom));
        viewportY = Math.min(viewportY, mapTotalHeight - (int)(getHeight() / zoom));
    }

    // Collision detection method - optimized with caching
    private boolean isWallAt(int x, int y) {
        if (maps.isEmpty() || x < 0 || y < 0 || x >= mapTotalWidth || y >= mapTotalHeight) {
            return true; // Treat out-of-bounds as walls
        }
        
        try {
            // Cache the BufferedImage for better performance
            if (cachedMapImage == null || lastCachedMapIndex != currentMapIndex) {
                Image mapImage = maps.get(currentMapIndex);
                
                if (mapImage instanceof java.awt.image.BufferedImage bufferedImage) {
                    cachedMapImage = bufferedImage;
                } else {
                    // Convert to BufferedImage and cache it
                    cachedMapImage = new java.awt.image.BufferedImage(
                        mapTotalWidth, mapTotalHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = cachedMapImage.createGraphics();
                    g2d.drawImage(mapImage, 0, 0, null);
                    g2d.dispose();
                }
                lastCachedMapIndex = currentMapIndex;
            }
            
            // Now use the cached image for fast pixel access
            int rgb = cachedMapImage.getRGB(x, y);
            
            // Extract RGB components
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;
            
            // Check if the pixel is black or very dark (wall)
            int threshold = 50; // Pixels darker than this are considered walls
            return (red < threshold && green < threshold && blue < threshold);
            
        } catch (Exception e) {
            // If there's any error accessing the pixel, treat it as a wall for safety
            return true;
        }
    }

    // Check if player can move to a specific position (optimized collision checking)
    private boolean canPlayerMoveTo(int newX, int newY) {
        // Only check 4 corners instead of 5 points for better performance
        int[][] checkPoints = {
            {newX, newY},                                    // Top-left
            {newX + PLAYER_SIZE - 1, newY},                  // Top-right
            {newX, newY + PLAYER_SIZE - 1},                  // Bottom-left
            {newX + PLAYER_SIZE - 1, newY + PLAYER_SIZE - 1} // Bottom-right
        };
        
        for (int[] point : checkPoints) {
            if (isWallAt(point[0], point[1])) {
                return false; // Can't move if any check point hits a wall
            }
        }
        return true;
    }

    private void movePlayer(int direction, int playerStep) {
        // Add movement cooldown to prevent lag from rapid key repeats
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime < MOVE_COOLDOWN) {
            return; // Skip movement if too soon
        }
        
        int newPlayerX = playerX;
        int newPlayerY = playerY;
        
        switch (direction) {
            case KeyEvent.VK_A -> newPlayerX = Math.max(0, playerX - playerStep);
            case KeyEvent.VK_D -> newPlayerX = Math.min(mapTotalWidth - PLAYER_SIZE, playerX + playerStep);
            case KeyEvent.VK_W -> newPlayerY = Math.max(0, playerY - playerStep);
            case KeyEvent.VK_S -> newPlayerY = Math.min(mapTotalHeight - PLAYER_SIZE, playerY + playerStep);
        }
        
        // Check collision at the new player position
        if (canPlayerMoveTo(newPlayerX, newPlayerY)) {
            playerX = newPlayerX;
            playerY = newPlayerY;
            lastMoveTime = currentTime; // Update last move time only on successful move
        }
    }

    private void handleMouseWheelZoom(java.awt.event.MouseWheelEvent e) {
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