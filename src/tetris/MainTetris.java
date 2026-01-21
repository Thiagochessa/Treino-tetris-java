package tetris;

import javax.swing.SwingUtilities;

public class MainTetris {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JanelaTetris());
    }
}
