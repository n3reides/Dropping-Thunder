package MyBalls;

import java.awt.*;

class Keep {

    Coord position;
    double radius = 150.0;
    double diameter = radius * 2;
    Color insideColor = Color.black;
    Color borderColor;
    double borderThickness = 1.5;
    Player playerOfKeep;

    Keep(Coord initialPosition, Color playerColor, Player player) {
        position = initialPosition;
        borderColor = playerColor;
        playerOfKeep = player;
    }

    void paint(Graphics2D g2D) {
        g2D.setColor(borderColor);
        g2D.fillOval((int) (position.x - radius + 0.5), (int) (position.y - radius + 0.5), (int) diameter, (int) diameter);
        g2D.setColor(insideColor);
        g2D.fillOval((int) (position.x - radius + 0.5 + borderThickness), (int) (position.y - radius + 0.5 + borderThickness), (int) (diameter - 2 * borderThickness), (int) (diameter - 2 * borderThickness));
    }

}
