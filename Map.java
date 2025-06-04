public class Map extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;

    public Map() {
        super();
        setLayout(null);
        setBackground(java.awt.Color.RED);
        
        // Example of adding a component to the map
        javax.swing.JButton button = new javax.swing.JButton("Click Me");
        button.setBounds(50, 50, 100, 30);
        add(button);
    }
    
}