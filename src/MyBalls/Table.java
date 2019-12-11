package MyBalls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Table extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

    private final Color[] colorArray = {new Color(255, 255, 255), new Color(255, 214, 117), new Color(242, 173, 45), new Color(242, 108, 45), new Color(210, 77, 13), new Color(120, 44, 7)};
    private final int width = 1000;
    private final int height = 600;
    private final int wallThickness = 20;
    final int X_BORDER_MAX = width + wallThickness;
    final int X_BORDER_MIN = wallThickness;
    final int Y_BORDER_MAX = height + wallThickness;
    final int Y_BORDER_MIN = wallThickness;
    private final Color color = Color.darkGray;
    private final Color wallColor = Color.black;
    Ball[] pointBalls = new Ball[5];
    Ball ball1, ball2, ball3, ball4, ball5, cueBall1, cueBall2;
    private final Timer simulationTimer;
    Ball[] player1CueBalls = new Ball[1];
    Ball[] player2CueBalls = new Ball[1];
    Ball[] allBalls = new Ball[pointBalls.length + player1CueBalls.length + player2CueBalls.length];
    Coord heavenCoordinates;
    Player[] allPlayers;
    Player currentPlayer;
    Hole[] cornerHoles = new Hole[2];
    Hole leftTopHole;
    Hole rightBottomHole;
    Keep[] allKeeps = new Keep[2];
    Keep Player1Keep, Player2Keep;

    Table(Player[] players) {
        heavenCoordinates = new Coord(-10000, -10000);
        setPreferredSize(new Dimension(width + 2 * wallThickness, height + 2 * wallThickness));
        createNewBalls();
        pointBalls[0] = ball1;
        pointBalls[1] = ball2;
        pointBalls[2] = ball3;
        pointBalls[3] = ball4;
        pointBalls[4] = ball5;
        allPlayers = players;
        player1CueBalls[0] = cueBall1;
        player2CueBalls[0] = cueBall2;
        allPlayers[0].turnToShoot = true;
        currentPlayer = allPlayers[0];
        createCornerHoles();
        cornerHoles[0] = leftTopHole;
        cornerHoles[1] = rightBottomHole;
        createNewKeeps();
        allKeeps[0] = Player1Keep;
        allKeeps[1] = Player2Keep;

        System.arraycopy(pointBalls, 0, allBalls, 0, pointBalls.length);
        System.arraycopy(player1CueBalls, 0, allBalls, pointBalls.length , player1CueBalls.length);
        for (int i = 0; i < player2CueBalls.length; i++) {
            allBalls[pointBalls.length + player1CueBalls.length + i] = player2CueBalls[i];
        }
        addMouseListener(this);
        addMouseMotionListener(this);
        simulationTimer = new Timer((int) (1000.0 / MyBalls.framesPerSecond), this);

    }

    private void createNewKeeps() {
        final Coord player1InitialKeepPosition = new Coord(50, 590);
        final Coord player2InitialKeepPosition = new Coord(990, 50);
        Player1Keep = new Keep(player1InitialKeepPosition, allPlayers[0].playerColor, allPlayers[0]);
        Player2Keep = new Keep(player2InitialKeepPosition, allPlayers[1].playerColor, allPlayers[1]);
    }

    void switchPlayers() {
        for (Player player : allPlayers) {
            player.turnToShoot = !player.turnToShoot;
        }
        for (Player player : allPlayers) {
            if (player.turnToShoot) {
                currentPlayer = player;
            }
        }
    }

    private void createCornerHoles() {
        final Coord leftTopHolePosition = new Coord(20, 20);
        final Coord rightBottomHolePosition = new Coord(1020, 620);
        leftTopHole = new Hole(leftTopHolePosition);
        rightBottomHole = new Hole(rightBottomHolePosition);
    }

    private void createNewBalls() {
        final Coord player1CueBallPos = new Coord(100, 500);
        final Coord player2CueBallPos = new Coord(900, 100);
        final Coord firstInitialPosition = new Coord((520.0 * 2.0 / 4.0), (320.0 * 2.0 / 4.0));
        final Coord secondInitialPosition = new Coord((520.0 * 3.0 / 4.0), (320.0 * 3.0 / 4.0));
        final Coord thirdInitialPosition = new Coord(520, 320);
        final Coord fourthInitialPosition = new Coord((520.0 * 5.0 / 4.0), (320.0 * 5.0 / 4.0));
        final Coord fifthInitialPosition = new Coord((520.0 * 6.0 / 4.0), (320.0 * 6.0 / 4.0));
        cueBall1 = new Ball(player1CueBallPos, this, true);
        cueBall2 = new Ball(player2CueBallPos, this, true);
        ball1 = new Ball(firstInitialPosition, this, false);
        ball2 = new Ball(secondInitialPosition, this, false);
        ball3 = new Ball(thirdInitialPosition, this, false);
        ball4 = new Ball(fourthInitialPosition, this, false);
        ball5 = new Ball(fifthInitialPosition, this, false);
    }

    public void actionPerformed(ActionEvent e) {
        for (Ball ball : allBalls) {
            ball.move();
        }
        repaint();
        for (Ball ball : allBalls) {
            if (ball.isMoving()) {
                return;
            }
        }
        simulationTimer.stop();
    }

    public void mousePressed(MouseEvent event) {
        for (Ball ball : allBalls) {
            if (ball.isMoving()) return;
        }
        Coord mousePosition = new Coord(event);
        if (allPlayers[0].turnToShoot) {
            for (Ball ball : player2CueBalls) {
                if (ball.position.x < 0) {
                    if (Coord.distance(mousePosition, Player1Keep.position) < Player1Keep.radius) {
                        ball.position = mousePosition;
                    }
                }
            }
            for (Ball ball : player1CueBalls) {
                ball.setAimPosition(mousePosition);
            }
            repaint();
        } else {
            for (Ball ball : player1CueBalls) {
                if (ball.position.x < 0) {
                    if (Coord.distance(mousePosition, Player2Keep.position) < Player2Keep.radius) {
                        ball.position = mousePosition;
                    }
                }
            }
            for (Ball ball : player2CueBalls) {
                ball.setAimPosition(mousePosition);
            }
            repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (allPlayers[0].turnToShoot) {
            for (Ball ball : player1CueBalls) {
                ball.shoot();
            }
            if (!simulationTimer.isRunning()) {
                simulationTimer.start();
            }
        } else {
            for (Ball ball : player2CueBalls) {
                ball.shoot();
            }
            if (!simulationTimer.isRunning()) {
                simulationTimer.start();
            }
        }
    }

    public void mouseDragged(MouseEvent event) {
        Coord mousePosition = new Coord(event);
        if (allPlayers[0].turnToShoot) {
            for (Ball ball : player1CueBalls) {
                ball.updateAimPosition(mousePosition);
            }
        } else {
            for (Ball ball : player2CueBalls) {
                ball.updateAimPosition(mousePosition);
            }
        }
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent event) {
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2D = (Graphics2D) graphics;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(wallColor);
        g2D.fillRect(0, 0, width + 2 * wallThickness, height + 2 * wallThickness);
        g2D.setColor(color);
        g2D.fillRect(wallThickness, wallThickness, width, height);
        for (Keep keep : allKeeps) {
            keep.paint(g2D);
        }
        for (Hole hole : cornerHoles) {
            g2D.setColor(Color.red);
            hole.paint(g2D);
        }
        for (Ball ball : player1CueBalls) {
            g2D.setColor(allPlayers[0].playerColor);
            ball.paint(g2D);
        }
        for (Ball ball : player2CueBalls) {
            g2D.setColor(allPlayers[1].playerColor);
            ball.paint(g2D);
        }
        g2D.setColor(Color.white);
        for (Ball ball : pointBalls) {
            if (ball.timesBeenHit >= 5) {
                ball.timesBeenHit = 5;
            }
            g2D.setColor(colorArray[ball.timesBeenHit]);
            ball.paint(g2D);
        }
    }

}
