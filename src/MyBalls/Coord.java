package MyBalls;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

class Coord {

    double x;
    double y;

    Coord(double xCoord, double yCoord) {
        x = xCoord;
        y = yCoord;
    }

    Coord(MouseEvent event) {
        x = event.getX();
        y = event.getY();
    }
    static final Coord ZERO = new Coord(0, 0);

    double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    Coord norm() {
        return new Coord(x / magnitude(), y / magnitude());
    }

    void increase(Coord c) {
        x += c.x;
        y += c.y;
    }

    void decrease(Coord c) {
        x -= c.x;
        y -= c.y;
    }

    static double scal(Coord a, Coord b) {
        return a.x * b.x + a.y * b.y;
    }

    static Coord sub(Coord a, Coord b) {
        return new Coord(a.x - b.x, a.y - b.y);
    }

    static Coord mul(double k, Coord c) {
        return new Coord(k * c.x, k * c.y);
    }

    static double distance(Coord a, Coord b) {
        return Coord.sub(a, b).magnitude();
    }

    static void paintLine(Graphics2D g, Coord a, Coord b) {
        g.setColor(Color.black);
        Stroke oldstroke = ((Graphics2D) g).getStroke();
        ((Graphics2D) g).setStroke(new BasicStroke(4));
        ((Graphics2D) g).draw(new Line2D.Double((int) a.x, (int) a.y, (int) b.x, (int) b.y));
        ((Graphics2D) g).setStroke(oldstroke);
    }

}
