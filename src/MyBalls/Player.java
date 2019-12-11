package MyBalls;

import javax.swing.*;
import java.awt.*;

class Player extends JPanel {

    int SCORE;
    boolean turnToShoot;
    Table theTable;
    Color playerColor;
    Keep playerKeep;
    Ball cueBall;
    String playerName;

    Player(String name, int a) {
        setPreferredSize(new Dimension(100, 150));
        SCORE = 0;
        turnToShoot = false;
        playerName = name;
        if (a == 0) {
            playerColor = Color.green;
        } else {
            playerColor = Color.blue;
        }
    }

    void addScores(double MULTIPLIER) {
        this.SCORE += 1 * MULTIPLIER;
        repaint();
    }

    void resetScores() {
        {
            SCORE = 0;
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(playerColor);
        g.fillRect(0, 0, 100, 150);
        Font textFont = new Font("Serif", Font.BOLD, 20);
        g.setFont(textFont);
        g.setColor(Color.black);
        g.drawString(playerName, 14, 25);
        Font scoreFont = new Font("Serif", Font.BOLD, 45);
        g.setFont(scoreFont);
        g.drawString("" + SCORE, 35, 100);
    }

}
