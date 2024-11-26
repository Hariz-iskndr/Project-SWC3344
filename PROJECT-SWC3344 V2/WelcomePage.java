import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomePage extends JFrame implements ActionListener {
    JButton startButton;
    public WelcomePage() 
    {
        // Create a title for the Vehicle Service
        setTitle("WELCOME TO PADU VEHICLE SERVICE");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        ImageIcon logo = null;
        try {
            logo = new ImageIcon("gta bengkel.png");
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        JLabel logoLabel = new JLabel(logo);
        
        // Create a Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel);
        
        // Create Start Button for the page
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        
        panel.add(startButton, BorderLayout.SOUTH);
        startButton.setBackground(Color.white);
        startButton.setBounds(200, 250, 100, 40);
        panel.add(logoLabel);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            dispose(); // Close the WelcomePage window
            new MainDashboard().setVisible(true); // Create and show the Vehicle Service window
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() { 
                        new WelcomePage();
                }
            });
    }
}