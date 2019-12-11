package MyBalls;

import java.awt.*;

class Hole {

    Coord position;
    double radius = 45.0;
    double diameter = radius * 2;
    Color color = Color.black;
    double borderThickness = 1.5;

    Hole(Coord holePosition) {
        position = holePosition;
    }

    void paint(Graphics2D g2D) {
        g2D.fillOval((int) (position.x - radius + 0.5), (int) (position.y - radius + 0.5), (int) diameter, (int) diameter);
        g2D.setColor(color);
        g2D.fillOval((int) (position.x - radius + 0.5 + borderThickness), (int) (position.y - radius + 0.5 + borderThickness), (int) (diameter - 2 * borderThickness), (int) (diameter - 2 * borderThickness));
    }

}
