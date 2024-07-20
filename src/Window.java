import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    public static final ImageIcon imageLogo = new ImageIcon("resources/LOGO.png");
    public Window(){
        this.setLayout(new BorderLayout());
        this.setSize(1000,800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Photo Editor");
        this.setIconImage(imageLogo.getImage());
        switchPanel(new EditorScreen(this));
        this.setVisible(true);
    }
    public void switchPanel(JPanel newPanel) {
        this.getContentPane().removeAll();
        this.add(newPanel);
        this.revalidate();
        this.repaint();
        newPanel.requestFocusInWindow();
    }
}
