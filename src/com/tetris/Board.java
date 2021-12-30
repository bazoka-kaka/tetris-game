package com.tetris;

import java.awt.event.WindowListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements KeyListener, MouseListener, MouseMotionListener{

	//Assets
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    public static int highScore;
    
    private Formatter formatter;

    private BufferedImage pause, refresh;

	//board dimensions (the playing area)
    private final int boardHeight = 20, boardWidth = 10;

	// block size
    public static final int blockSize = 30;

	// field
    private Color[][] board = new Color[boardHeight][boardWidth];

	// array with all the possible shapes
    private Shape[] shapes = new Shape[7];

	// currentShape
    private static Shape currentShape, nextShape;

	// game loop
    private Timer looper;

    private int FPS = 60;

    private int delay = 1000 / FPS;

	// mouse events variables
    private int mouseX, mouseY;

    private boolean leftClick = false;

    private Rectangle stopBounds, refreshBounds;

    private boolean gamePaused = false;

    private boolean gameOver = false;
    
    private Color[] colors = {Color.decode("#ed1c24"), Color.decode("#ff7f27"), Color.decode("#fff200"), 
        Color.decode("#22b14c"), Color.decode("#00a2e8"), Color.decode("#a349a4"), Color.decode("#3f48cc")};
    private Random random = new Random();
	// buttons press lapse
    private Timer buttonLapse = new Timer(300, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            buttonLapse.stop();
        }
    });

	// score
    public static int score = 0;
    
    private int index;
    
    //level
    private int level = 0;
    private int nextLevel = 5;
    
    //file
    private Scanner scanner;

    public Board() {
    	index = 2;
        pause = ImageLoader.loadImage("/pause.png");
        refresh = ImageLoader.loadImage("/refresh.png");

        mouseX = 0;
        mouseY = 0;

        stopBounds = new Rectangle(350, 500, pause.getWidth(), pause.getHeight() + pause.getHeight() / 2);
        refreshBounds = new Rectangle(350, 500 - refresh.getHeight() - 20, refresh.getWidth(),
                refresh.getHeight() + refresh.getHeight() / 2);

		// create game looper
        looper = new Timer(delay, new GameLooper());

		// create shapes
        shapes[0] = new Shape(new int[][]{
            {1, 1, 1, 1} // I shape;
        }, this, colors[0]);

        shapes[1] = new Shape(new int[][]{
            {1, 1, 1},
            {0, 1, 0}, // T shape;
        }, this, colors[1]);

        shapes[2] = new Shape(new int[][]{
            {1, 1, 1},
            {1, 0, 0}, // L shape;
        }, this, colors[2]);

        shapes[3] = new Shape(new int[][]{
            {1, 1, 1},
            {0, 0, 1}, // J shape;
        }, this, colors[3]);

        shapes[4] = new Shape(new int[][]{
            {0, 1, 1},
            {1, 1, 0}, // S shape;
        }, this, colors[4]);

        shapes[5] = new Shape(new int[][]{
            {1, 1, 0},
            {0, 1, 1}, // Z shape;
        }, this, colors[5]);

        shapes[6] = new Shape(new int[][]{
            {1, 1},
            {1, 1}, // O shape;
        }, this, colors[6]);
    }
    
    private void update() {
        if (stopBounds.contains(mouseX, mouseY) && leftClick && !buttonLapse.isRunning() && !gameOver) {
            buttonLapse.start();
            gamePaused = !gamePaused;
        }

        if (refreshBounds.contains(mouseX, mouseY) && leftClick) {
            
            startGame();
        }

        if (gamePaused || gameOver) {
            return;
        }
        
        if(score >= highScore && index >= 0) {
        	highScore = Title.scores[index--];
        	repaint();
        } else if(score >= highScore && index < 0) {
        	highScore = score;
        	repaint();
        }
        
        currentShape.update();
    }
    
    
    
    public void readFile() {
    	int count = 0;
        while(scanner.hasNext() && count++ < 3) {
            String a = scanner.next();
            String b = scanner.next();

            System.out.printf("%s %s\n", a, b);
        }
    }

    

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {

                if (board[row][col] != null) {
                    g.setColor(board[row][col]);
                    g.fillRect(col * blockSize, row * blockSize, blockSize, blockSize);
                }

            }
        }
        g.setColor(nextShape.getColor());
        for (int row = 0; row < nextShape.getCoords().length; row++) {
            for (int col = 0; col < nextShape.getCoords()[0].length; col++) {
                if (nextShape.getCoords()[row][col] != 0) {
                    g.fillRect(col * 30 + 320, row * 30 + 50, Board.blockSize, Board.blockSize);
                }
            }
        }
        currentShape.render(g);

        if (stopBounds.contains(mouseX, mouseY)) {
            g.drawImage(pause.getScaledInstance(pause.getWidth() + 3, pause.getHeight() + 3, BufferedImage.SCALE_DEFAULT), stopBounds.x + 3, stopBounds.y + 3, null);
        } else {
            g.drawImage(pause, stopBounds.x, stopBounds.y, null);
        }

        if (refreshBounds.contains(mouseX, mouseY)) {
            g.drawImage(refresh.getScaledInstance(refresh.getWidth() + 3, refresh.getHeight() + 3,
                    BufferedImage.SCALE_DEFAULT), refreshBounds.x + 3, refreshBounds.y + 3, null);
        } else {
            g.drawImage(refresh, refreshBounds.x, refreshBounds.y, null);
        }

        if (gamePaused) {
            String gamePausedString = "GAME PAUSED";
            g.setColor(Color.WHITE);
            g.setFont(new Font("Georgia", Font.BOLD, 30));
            g.drawString(gamePausedString, 35, WindowGame.HEIGHT / 2);
        }
        if (gameOver) {
            String gameOverString = "GAME OVER";
            g.setColor(Color.WHITE);
            g.setFont(new Font("Georgia", Font.BOLD, 30));
            g.drawString(gameOverString, 50, WindowGame.HEIGHT / 2);
        }
        g.setColor(Color.WHITE);

        g.setFont(new Font("Georgia", Font.BOLD, 20));
        
        //drawing next level at
        g.drawString("NEXT LV@", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 - 100);
        g.drawString(nextLevel + "", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 - 70);
        
        //drawing score
        g.drawString("NEXT HIGH", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 - 40);
        g.drawString(highScore + "", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 - 10);
        g.drawString("SCORE", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 + 20);
        g.drawString(score + "", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 + 50);
        
        //drawing level
        g.drawString("LEVEL", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 + 80);
        g.drawString(level + "", WindowGame.WIDTH - 125, WindowGame.HEIGHT / 2 + 110);
        
        g.setColor(Color.WHITE);

        for (int i = 0; i <= boardHeight; i++) {
            g.drawLine(0, i * blockSize, boardWidth * blockSize, i * blockSize);
        }
        for (int j = 0; j <= boardWidth; j++) {
            g.drawLine(j * blockSize, 0, j * blockSize, boardHeight * 30);
        }
    }

    public void setNextShape() {
    	//update the level
    	if(score % nextLevel == 0 && score != 0) {
        	level++;
        	nextLevel *= 2;
        	Shape.normal -= 5 * level;
        	System.out.println(Shape.normal);
        }
        int index = random.nextInt(shapes.length);
        int colorIndex = random.nextInt(colors.length);
        nextShape = new Shape(shapes[index].getCoords(), this, colors[colorIndex]);
    }

    public void setCurrentShape() {
        currentShape = nextShape;
        setNextShape();

        for (int row = 0; row < currentShape.getCoords().length; row++) {
            for (int col = 0; col < currentShape.getCoords()[0].length; col++) {
                if (currentShape.getCoords()[row][col] != 0) {
                    if (board[currentShape.getY() + row][currentShape.getX() + col] != null) {
                        gameOver = true;
                    }
                }
            }
        }

    }

    public Color[][] getBoard() {
        return board;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            currentShape.rotateShape();
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            currentShape.setDeltaX(1);
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            currentShape.setDeltaX(-1);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            currentShape.speedUp();
        }
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            currentShape.speedDown();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void startGame() {
        stopGame();
        setNextShape();
        setCurrentShape();
        gameOver = false;
        index = 2;
        highScore = Title.scores[index];
        nextLevel = 5;
        repaint();
        looper.start();
    }

    public void stopGame() {
        score = 0;
        level = 0;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = null;
            }
        }
        looper.stop();
    }

    class GameLooper implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            update();
            repaint();
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftClick = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftClick = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void addScore() {
        score++;
    }
}
