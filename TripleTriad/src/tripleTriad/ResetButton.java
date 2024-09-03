package tripleTriad;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ResetButton extends JButton {
	
	private GamePanel gp;

    public ResetButton(GamePanel gp) {
        super("Reset Game");

        this.gp = gp;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call a method to reset the game state
                gp.resetGame();
            }
        });
    }

}
