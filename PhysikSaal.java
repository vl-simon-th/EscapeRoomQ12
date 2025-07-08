import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PhysikSaal extends JPanel {

    private Image background;
    private int imageIndex = 0;
    private final String[] images = {
        "roomImg/physiksaal/ph1.png",
        "roomImg/physiksaal/ph2.png",
        "roomImg/physiksaal/ph3.png",
        "roomImg/physiksaal/fensterclosed.png"
    };
    private ArrowComponent arrowUp;
    private ArrowComponent arrowDown;

    public PhysikSaal() {
        setLayout(null);
        background = new ImageIcon(images[0]).getImage();
        setPreferredSize(new Dimension(800, 600));

        // Oberer Pfeil (vorwärts)
        arrowUp = new ArrowComponent(true); // true = nach oben
        arrowUp.setSize(100, 40);
        add(arrowUp);

        // Unterer Pfeil (rückwärts)
        arrowDown = new ArrowComponent(false); // false = nach unten
        arrowDown.setSize(100, 40);
        add(arrowDown);

        // Dynamische Zentrierung oben/unten (ca. 1cm = 38px Abstand)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelWidth = getWidth();
                int arrowWidth = arrowUp.getWidth();
                int arrowHeight = arrowUp.getHeight();
                int x = (panelWidth - arrowWidth) / 2;
                int yUp = 38; // oben
                int yDown = getHeight() - arrowHeight - 38; // unten
                arrowUp.setLocation(x, yUp);
                arrowDown.setLocation(x, yDown);
            }
        });

        // Oberer Pfeil: vorwärts
        arrowUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (imageIndex < images.length - 1) {
                    imageIndex++;
                    background = new ImageIcon(images[imageIndex]).getImage();
                    repaint();
                    if (imageIndex == images.length - 1) {
                        arrowUp.setVisible(false);
                    }
                    arrowDown.setVisible(true);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { arrowUp.setHover(true); }
            @Override
            public void mouseExited(MouseEvent e) { arrowUp.setHover(false); }
        });

        // Unterer Pfeil: rückwärts
        arrowDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (imageIndex > 0) {
                    imageIndex--;
                    background = new ImageIcon(images[imageIndex]).getImage();
                    repaint();
                    if (imageIndex == 0) {
                        // Von ph1 zurück zur Map
                        background = new ImageIcon("mapImg/map.png").getImage();
                        arrowDown.setVisible(false);
                    }
                    arrowUp.setVisible(true);
                } else if (imageIndex == 0) {
                    // Von ph1 zurück zur Map
                    background = new ImageIcon("mapImg/map.png").getImage();
                    arrowDown.setVisible(false);
                    arrowUp.setVisible(true);
                    repaint();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { arrowDown.setHover(true); }
            @Override
            public void mouseExited(MouseEvent e) { arrowDown.setHover(false); }
        });

        // Initial sichtbar/nicht sichtbar
        arrowUp.setVisible(true);
        arrowDown.setVisible(false);
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

    // Angepasste ArrowComponent für Richtung
    private static class ArrowComponent extends JComponent {
        private boolean hover = false;
        private boolean up;

        public ArrowComponent(boolean up) {
            this.up = up;
        }

        public void setHover(boolean hover) {
            this.hover = hover;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Hintergrund leicht transparent beim Hover
            if (hover) {
                g2.setColor(new Color(100, 100, 255, 60));
                g2.fillRoundRect(0, 0, w, h, 20, 20);
            }

            g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(hover ? new Color(30, 80, 200) : new Color(50, 50, 120));

            if (up) {
                // Pfeil nach oben
                g2.drawLine(w / 2, 10, w / 2, h - 15);
                g2.drawLine(w / 2, 10, w / 4, h - 25);
                g2.drawLine(w / 2, 10, 3 * w / 4, h - 25);
            } else {
                // Pfeil nach unten
                g2.drawLine(w / 2, h - 10, w / 2, 15);
                g2.drawLine(w / 2, h - 10, w / 4, 25);
                g2.drawLine(w / 2, h - 10, 3 * w / 4, 25);
            }
            g2.dispose();
        }
    }
}
