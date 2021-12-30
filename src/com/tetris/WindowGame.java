package com.tetris;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;

public class WindowGame extends JFrame {
    public static final int WIDTH = 445, HEIGHT = 640;

    private Board board;
    private Title title;
    public static String userName;

    public WindowGame() {
    	super("Tetris");
    	addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	addRecords();
                e.getWindow().dispose();
            }
        });
        
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        board = new Board();
        title = new Title(this);

        addKeyListener(board);
        addKeyListener(title);

        add(title);

        setVisible(true);
    }
    
    //	files
    
    private void addRecords() {
    	FileWriter file;
		try {
			file = new FileWriter("./bestScores.txt", true);
			BufferedWriter b = new BufferedWriter(file);
	        b.write(String.format("%s %s\n", Board.score, (userName != null && !userName.trim().isEmpty())? userName: "noName"));
	        b.newLine();
	        b.close();
	        file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot read file");
		}
    }

    public void startTetris() {
        remove(title);
        addMouseMotionListener(board);
        addMouseListener(board);
        add(board);
        board.startGame();
        revalidate();
    }

    public static void main(String[] args) {
        new WindowGame();
    }
}
