package com.tetris;


import java.awt.Color;

import java.awt.FlowLayout;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Title extends JPanel implements KeyListener{

	private static final long serialVersionUID = 1L;
	private BufferedImage instructions;
	private WindowGame window;
	private BufferedImage[] playButton = new BufferedImage[2];
	private Timer timer;
	private Scanner scanner;
	public static String[] names;
	public static int[] scores;
	
	public Title(WindowGame window){
		names = new String[3];
		scores = new int[3];
		for(int i = 0; i < 3; ++i) {
			scores[i] = 0;
		}
		openFile();
		readFile();
		closeFile();
		for(int i = 0; i < 3; ++i) {
			System.out.println(scores[i]);
		}
		Board.highScore = scores[2];
		WindowGame.userName = JOptionPane.showInputDialog("Enter your name");
		WindowGame.userName = WindowGame.userName.replaceAll("\\s+","_");
        instructions = ImageLoader.loadImage("/arrow.png");
		timer = new Timer(1000/60, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
			
		});
		timer.start();
		this.window = window;
	}
	
	private void openFile() {
        try {
            scanner = new Scanner(new File("./bestScores.txt"));
        } catch(Exception e) {
            System.out.println("Could not find file");
        }
    }

    private void readFile() {
        while(scanner.hasNext()) {
            int a = scanner.nextInt();
            String b = scanner.next();
            
            if(a >= scores[0] && a > scores[1] && a > scores[2]) {
            	if(scores[0] != 0) {
            		for(int i = 1; i >= 0; --i) {
            			scores[i+1] = scores[i];
            			names[i+1] = names[i];
            		}
            	}
				scores[0] = a;
				names[0] = b;
            }
            if(a < scores[0] && a >= scores[1] && a > scores[2]) {
            	if(scores[1] != 0) {
            		scores[2] = scores[1];
            		names[2] = names[1];
            	}
            	scores[1] = a;
            	names[1] = b;
            }
            if(a < scores[0] && a < scores[1] && a >= scores[2]) {
            	scores[2] = a;
            	names[2] = b;
            }   
        }
    }

    private void closeFile() {
        scanner.close();
    }
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.setColor(Color.BLACK);
		
		g.fillRect(0, 0, WindowGame.WIDTH, WindowGame.HEIGHT);
		
		g.setFont(new Font("SansSerif", Font.BOLD, 30));
        
		g.setColor(Color.WHITE);
        g.drawString("Tetris", WindowGame.WIDTH / 2 - 47, 50);
        
		g.drawImage(instructions, 445/2 - 150,
				30 - instructions.getHeight()/2 + 240, 300, 268, null);
		g.setFont(new Font("SansSerif", Font.BOLD, 15));
                g.setColor(Color.WHITE);
                
		
		g.drawString("HIGH SCORES:", 100, WindowGame.HEIGHT / 2 + 90);
		int nameHeight = 110;
		for(int i = 0; i < scores.length; ++i) {
			if(names[i] == null) {
				break;
			}
			g.drawString((i + 1) + ". " + names[i] + " " + scores[i], 100, WindowGame.HEIGHT / 2 + nameHeight + (i * 20));
		}
		
		g.setFont(new Font("Georgia", Font.BOLD, 15));
		
		g.drawString("Press space to play!", 100, WindowGame.HEIGHT / 2 + 180);
	}

    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == KeyEvent.VK_SPACE) {
            window.startTetris();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
    
    private class Handler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
    	
    }
}
