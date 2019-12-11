
package MyBalls;


import javax.swing.*;
import java.awt.*;


public class MyBalls {

    final static int framesPerSecond = 100;

    public static void main(String[] args) {
        JFrame startFrame = new JFrame();
        startFrame.setResizable(false);
        Player Player1 = new Player("Player 1", 0);
        Player Player2 = new Player("Player 2", 1);
        Player[] players = {Player1, Player2};
        ButtonPanel resetButton = new ButtonPanel("New Game", players);
        startFrame.setLayout(new BorderLayout());
        startFrame.add(Player1, BorderLayout.WEST);
        startFrame.add(Player2, BorderLayout.EAST);
        startFrame.add(resetButton, BorderLayout.SOUTH);
        startFrame.pack();
        startFrame.setVisible(true);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

