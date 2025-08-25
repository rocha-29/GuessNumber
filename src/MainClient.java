import com.guess.client.GameFrame;

public class MainClient {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new GameFrame().setVisible(true));
    }
}
