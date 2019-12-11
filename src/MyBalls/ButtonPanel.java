package MyBalls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ButtonPanel extends JButton implements ActionListener {

    JFrame currentFrame;
    String buttonPressed;
    Player[] allPlayers;

    ButtonPanel(String title, Player[] players) {
        addActionListener(this);
        this.setText(title);
        allPlayers = players;
    }

    public void actionPerformed(ActionEvent e) {
        buttonPressed = ((JButton) (e.getSource())).getText();
        if ("New Game".equals(buttonPressed)) {
            startNewGame();
        }
    }

    void startNewGame() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new JFrame("Strategic Warfare Billiards");
        currentFrame.setResizable(false);
        Table newTable = new Table(allPlayers);
        currentFrame.add(newTable, BorderLayout.CENTER);
        currentFrame.pack();
        currentFrame.setVisible(true);
        currentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        for (Player player : newTable.allPlayers) {
            player.resetScores();
        }
    }

}
