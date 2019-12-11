package MyBalls;

import java.awt.*;

class Ball {

    private final double radius = 15;
    private final double friction = 0.0250;
    private final double frictionPerUpdate = 1.0 - Math.pow(1.0 - friction, 100.0 / MyBalls.framesPerSecond);
    private Coord velocity;
    private Coord aimPosition;
    private final double leftWallCoordinates;
    private final double rightWallCoordinates;
    private final double topWallCoordinates;
    private final double bottomWallCoordinates;
    private Coord lastPosition;
    private Ball[] allBalls;
    private Ball[] collidingBalls = new Ball[2];
    private Table theTable;
    Coord position;
    int timesBeenHit;
    Hole[] allHoles;
    Keep[] allKeeps;
    boolean cueBall;
    Coord startingPosition;

    Ball(Coord initialPosition, Table myTable, boolean whatBall) {
        theTable = myTable;
        timesBeenHit = 0;
        leftWallCoordinates = theTable.X_BORDER_MIN;
        rightWallCoordinates = theTable.X_BORDER_MAX;
        topWallCoordinates = theTable.Y_BORDER_MIN;
        bottomWallCoordinates = theTable.Y_BORDER_MAX;
        startingPosition = initialPosition;
        position = new Coord(startingPosition.x, startingPosition.y);
        allBalls = myTable.allBalls;
        lastPosition = new Coord(position.x, position.y);
        velocity = Coord.ZERO;
        allHoles = myTable.cornerHoles;
        allKeeps = myTable.allKeeps;
        cueBall = whatBall;
    }

    private boolean isAiming() {
        return aimPosition != null;
    }

    boolean isMoving() {
        return velocity.magnitude() > frictionPerUpdate;
    }

    void shoot() {
        if (isAiming()) {
            Coord aimingVector = Coord.sub(position, aimPosition);
            velocity = Coord.mul(Math.sqrt(15.0 * aimingVector.magnitude() * 2 / MyBalls.framesPerSecond), aimingVector.norm());
            aimPosition = null;
            theTable.switchPlayers();
        }
    }

    void setAimPosition(Coord grabPosition) {
        if (Coord.distance(position, grabPosition) <= radius) {
            aimPosition = grabPosition;
        }
    }

    void updateAimPosition(Coord newPosition) {
        if (isAiming()) {
            aimPosition = newPosition;
        }
    }

    void move() {
        if (isMoving()) {
            position.increase(velocity);
            velocity.decrease(Coord.mul(frictionPerUpdate, velocity.norm()));
            hitWall();
            hitHole();
            if (hitBall()) {
                impactVectors(collidingBalls);
            }
            lastPosition = new Coord(position.x, position.y);
        }
        if (!isMoving()) {
            if (this.timesBeenHit == 5 && !cueBall) {
                determineExplosionImpact();

            }
        }
    }

    void determineExplosionImpact() {
        for (Keep keep : allKeeps) {
            if (Coord.distance(this.position, keep.position) < keep.radius) {
                Keep scoringKeep = null;
                for (Keep otherKeep : allKeeps) {
                    if (keep != otherKeep) {
                        scoringKeep = otherKeep;
                    }
                }
                assert scoringKeep != null;
                scoringKeep.playerOfKeep.addScores(1);

            }
            this.position = new Coord(startingPosition.x, startingPosition.y);
            this.velocity = Coord.ZERO;
            this.timesBeenHit = 0;
        }

    }

    private void impactVectors(Ball[] balls) {
        Coord deltaCoords = Coord.sub(balls[0].position, balls[1].position);
        Coord unitVector = deltaCoords.norm();
        double impulse = Coord.scal(balls[1].velocity, unitVector) - Coord.scal(balls[0].velocity, unitVector);
        Coord unitImpulse = Coord.mul(impulse, unitVector);
        balls[0].velocity.x += unitImpulse.x;
        balls[0].velocity.y += unitImpulse.y;
        balls[1].velocity = Coord.sub(balls[1].velocity, unitImpulse);
    }

    private boolean hitBall() {
        double xDifference;
        double yDifference;
        for (Ball ball : allBalls) {
            if (ball != this) {
                xDifference = Math.abs(this.position.x - ball.position.x);
                yDifference = Math.abs(this.position.y - ball.position.y);
                double powOfDifference = Math.pow(xDifference, 2) + Math.pow(yDifference, 2);
                double radialDifference = this.radius + ball.radius;
                if (Math.sqrt(powOfDifference) <= radialDifference + .01 && movingTowardsEachOther(this, ball)) {
                    this.timesBeenHit++;
                    ball.timesBeenHit++;
                    collidingBalls[0] = this;
                    collidingBalls[1] = ball;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean movingTowardsEachOther(Ball ball1, Ball ball2) {
        return Coord.distance(ball1.lastPosition, ball2.lastPosition) > Coord.distance(ball1.position, ball2.position);
    }

    private void hitWall() {
        if (Math.abs(position.x - leftWallCoordinates) < radius && (position.x - lastPosition.x) < 0) {
            velocity = new Coord(-velocity.x, velocity.y);
        } else if (Math.abs(position.x - rightWallCoordinates) < radius && (position.x - lastPosition.x) > 0) {
            velocity = new Coord(-velocity.x, velocity.y);
        } else if (Math.abs(position.y - topWallCoordinates) < radius && (position.y - lastPosition.y) < 0) {
            velocity = new Coord(velocity.x, -velocity.y);
        } else if (Math.abs(position.y - bottomWallCoordinates) < radius && (position.y - lastPosition.y) > 0) {
            velocity = new Coord(velocity.x, -velocity.y);
        }
    }

    void hitHole() {
        Coord holeEdge;
        Coord otherHolePos;
        for (Hole hole : allHoles) {
            if (hole.position.x == 20 && hole.position.y == 20) {
                holeEdge = new Coord(hole.position.x + hole.radius / 2, hole.position.y + hole.radius / 2);
                otherHolePos = new Coord(980, 580);
            } else {
                holeEdge = new Coord(hole.position.x - hole.radius / 2, hole.position.y - hole.radius / 2);
                otherHolePos = new Coord(40, 40);
            }
            double disTance = Coord.distance(this.position, holeEdge);
            if (disTance < 10 && Coord.distance(this.lastPosition, hole.position) > Coord.distance(this.position, hole.position) && !this.cueBall) {
                this.position = new Coord(otherHolePos.x, otherHolePos.y);
            } else if (disTance < 10 && Coord.distance(this.lastPosition, hole.position) > Coord.distance(this.position, hole.position) && this.cueBall) {
                this.position = theTable.heavenCoordinates;
                this.velocity = Coord.ZERO;
            }
        }
    }

    void paint(Graphics2D g2D) {
        double diameter = 2 * radius;
        g2D.fillOval((int) (position.x - radius + 0.5), (int) (position.y - radius + 0.5), (int) diameter, (int) diameter);
        int borderThickness = 2;
        g2D.fillOval((int) (position.x - radius + 0.5 + borderThickness), (int) (position.y - radius + 0.5 + borderThickness), (int) (diameter - 2 * borderThickness), (int) (diameter - 2 * borderThickness));
        if (isAiming()) {
            paintAimingLine(g2D);
        }
    }

    private void paintAimingLine(Graphics2D graph2D) {
        Coord centerCueBall = this.position;
        Coord.paintLine(graph2D, aimPosition, centerCueBall);
    }

}
