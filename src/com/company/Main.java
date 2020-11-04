package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        MainWindow mainFrame = new MainWindow();
    }
}

class MainWindow extends JFrame {
    MainWindow() {
        setSize(1280, 1024);
        setTitle("TD game");
        setResizable(false);
        this.add(new GamePanel());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

class GamePanel extends JPanel implements ActionListener, MouseListener {

    // Here is the game panel class
    // We implement both ActionListener and MouseListener initially

    // This means overriding their method calls in the interface class
    // if we dont need to use some of the methods then we can just skip calling them

    ArrayList<Coin> coinList = new ArrayList<>();
    ArrayList<Switch> switchList = new ArrayList<>();
    int height = 1024;
    int width = 1280;
    private final Timer simulationTimer;

    GamePanel() {
        // in the constructor of game panel, we will want to initalize the game board
        this.setSize(width, height);
        this.setVisible(true);
        // load a background image
        setInitialParams();
        addMouseListener(this);
        simulationTimer = new Timer((int) (1000.0 / 100.0), this);
    }

    void setInitialParams() {
        System.out.println("settng init params");
        setSwitches();
    }

    void setSwitches() {
        // imagine you can create a loop here to construct more than 1 switch
        Switch newSwitch;

        // randomize orientation
        double leftOrRight = Math.random();
        if (leftOrRight < 0.5) {
            newSwitch = new Switch("left", 100, 100);
        } else {
            newSwitch = new Switch("right", 100, 100);
        }
        switchList.add(newSwitch);

        // note for the future... now we are just painting the switch image regardless of orientation
        // in the future, we will want to flip the switch 180 degrees left or right depending on orientation
        // this can probably be accomplished using normal java paint methods used in paintcomponent to mirror the image
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        for (Coin coin : coinList) {
            if (coin.getMotion()) {
                moveCoin(coin);
                determineCoinCollision(coin);
            }
            repaint();
        }
        if (stillMoving()) {
            return;
        }
        simulationTimer.stop();

    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        // a mouse event consists of some information we can use
        // to find out what we can learn from a mouse event, we can see the documentation, just google it
        Point clicked = mouseEvent.getPoint();
        System.out.println("mouse clicked");
        // now we have the point given by the mouse event
        // we will want to load a coin on that specific point

        // in the future we will want to restrict which points clicked can actually generate a new coin
        // that is simple to check with an if-clause
        Coin newCoin = new Coin(clicked.x, clicked.y, true);
        coinList.add(newCoin);

        // now we have the coin object, we want to make it move within the game board
        simulationTimer.start();
        // see actionPerformed

    }

    void determineCoinCollision(Coin coin) {
        // coin stops moving either if it hits the border of the panel
        // or if it hits a switch

        // check if coin has hit bottom in this iteration
        if (coin.getY() == this.height) {
            coin.setMotion(false);
        }
        // check if coin has hit switch in this iteration
        for (Switch Switch : switchList) {
            if (Switch.getOrientation() == "left") {
                // detect: coin is same height as switch + switch doesn't have coin already + coin collides with lever part of switch
                if ((coin.getY() == Switch.getY()) && ((coin.getX() > Switch.getX() + Switch.getWidth() / 2) && (coin.getX() < Switch.getX() + Switch.getWidth()))) {
                    if (!Switch.getHasCoin()) {
                        Switch.setCoin(coin);
                        coin.setMotion(false);
                        Switch.setHasCoin();
                    } else {
                        coin.setX(coin.getX() - 50);
                    }
                } else if (((coin.getX() > Switch.getX()) && (coin.getX() < Switch.getX() + Switch.getWidth() / 2)) && (coin.getY() == Switch.getY() + Switch.getHeight() / 2)) {
                    if (!Switch.getHasCoin()) {
                        Switch.flipSwitch();
                    } else {
                        Switch.flipSwitch();
                        Coin presentCoin = Switch.getCoin();
                        presentCoin.setMotion(true);
                        Switch.coin = null;
                    }
                }
            } else {
                if ((coin.getY() == Switch.getY()) && ((coin.getX() > Switch.getX() + Switch.getWidth() / 2 && (coin.getX() < Switch.getX() + Switch.getWidth())))) {
                    if (!Switch.getHasCoin()) {
                        Switch.setCoin(coin);
                        coin.setMotion(false);
                        Switch.setHasCoin();
                    } else {
                        coin.setX(coin.getX() + 50);
                    }
                } else if (((coin.getX() < Switch.getX() + Switch.getWidth() / 2) && (coin.getX() > Switch.getX())) && (coin.getY() == Switch.getY() + Switch.getHeight() / 2)) {
                    if (!Switch.getHasCoin()) {
                        Switch.flipSwitch();
                    } else {
                        Switch.flipSwitch();
                        Coin presentCoin = Switch.getCoin();
                        presentCoin.setMotion(true);
                        Switch.coin = null;
                    }

                }
            }
        }

    }
    @Override

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Coin coin : coinList) {
            BufferedImage image;
            try {
                image = ImageIO.read(new File("resources/TDcircle.png"));
                // draw the coin at its location!
                g.drawImage(image, coin.getX(), coin.getY(), this);
            } catch (IOException e) {
                System.out.println("cant read image");
            }
        }
        for (Switch Switch : switchList) {
            BufferedImage image;
            try {
                // draw the switch at its location!
                if (Switch.getOrientation() == "left") {
                    image = ImageIO.read(new File("resources/tdSwitch.png"));
                    g.drawImage(image, Switch.getX(), Switch.getY(), this);
                } else {
                    image = ImageIO.read(new File("resources/tdSwitch-rotated.png"));
                    g.drawImage(image, Switch.getX(), Switch.getY(), this);
                    //Reset our graphics object so we can draw with it again.
                }
            } catch (IOException e) {
                System.out.println("cant read image");
            }
        }
        // paint component is one of those Java magic methods
        // its actually where you define the graphics of your Jpanel
        // in this case, we ideally want to use it whenever we perform an action that changes the game
        // for instance, dropping a coin, flipping a switch, etc.

        // its important to note that all graphics is done HERE ONLY
        // the graphic generated here depends on the parameters set elsewhere

        // if any coin is moving keep repainting graphics

    }

    boolean stillMoving() {
        boolean stillMoving = false;
        for (Coin coin : coinList) {
            if (coin.getMotion()) {
                stillMoving = true;
                //we only need to find 1 coin which is still moving
            }
        }
        return stillMoving;
    }

    void moveCoin(Coin coin) {
        // make coin drop vertically 1 pixel at a time
        coin.setY(coin.getY() + 2);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    void flipSwitch(Switch Switch) {
        // NEEDS TO BE DONE
        // for example, we can flip a switch if the coin hits a specific part of the switch
    }

    private class Coin {
        // Coinloader is a subclass of the game panel class, so its constructor is callable from within that class
        // here to define the inital parameters of a coin
        private int xcoord;
        private int ycoord;
        private boolean _isMoving;

        Coin(int x, int y, boolean move) {
            // when coin constructor is called, assign parameters to this object
            this.xcoord = x;
            this.ycoord = y;
            _isMoving = move;

        }

        void setY(int newVal) {
            this.ycoord = newVal;
        }

        void setX(int newVal) {
            this.xcoord = newVal;
        }

        int getX() {
            return this.xcoord;
        }

        int getY() {
            return this.ycoord;
        }

        Point getPosition() {
            return new Point(this.xcoord, this.ycoord);
        }

        boolean getMotion() {
            return _isMoving;
        }

        void setMotion(boolean newMotion) {
            _isMoving = newMotion;
        }
    }

    private class Switch {
        // Switch is a subclass of the game panel class, so its constuctor is callable from within that class
        // Here to define the parameters of a switch
        private String orientation;
        private int width;
        private int height;
        private int xcoord;
        private int ycoord;
        private boolean hasCoin;
        private Coin coin;

        Switch(String orient, int x, int y) {
            // When switch constructor is called, assign parameters to this object
            // you dont actually need to set .this here
            // it's just nice to recall that these parameters are set to this specific object
            this.hasCoin = false;
            this.orientation = orient;
            this.xcoord = x;
            this.ycoord = y;
            this.width = 200;
            this.height = 200;

        }

        void flipSwitch() {
            if (this.orientation == "left") {
                this.orientation = "right";
            } else {
                this.orientation = "left";
            }
        }

        int getY() {
            return this.ycoord;
        }

        int getX() {
            return this.xcoord;
        }

        int getWidth() {
            return this.width;
        }

        int getHeight() {
            return this.height;
        }

        String getOrientation() {
            return orientation;
        }

        boolean getHasCoin() {
            return this.hasCoin;
        }

        void setHasCoin() {
            hasCoin = !hasCoin;
        }

        void setCoin(Coin coin) {
            this.coin = coin;
        }


        Coin getCoin() {
            return this.coin;
        }
    }
}