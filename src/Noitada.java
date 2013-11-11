import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;


public class Noitada extends Applet implements Runnable, KeyListener{
	//Threads	
	Thread refreshRate = new Thread(this);
	Thread client = new Thread(this);
	
	//Graphics
	Graphics bufferG;
	Image bufferI;

	//Map
	Map m1 = new Map();
	
	//Players
	Player players[] = new Player[10];
	Player p1 = new Player(new Coordinates(13, 15));	

	//Input
	boolean[] pressedKeys = new boolean[5];
	char[] keys = {'w', 'a', 's', 'd', 'j'};
	
	//Server
	private static final int PORT = 1713;
	private static final String host = "localhost";
	
	//Functions
	public void init(){
		setSize(800, 600);
		setBackground(Color.BLACK);	
		addKeyListener(this);
	}
	
	public void start(){
		refreshRate.start();
		client.start();
	}
	
	public void run(){
		long tm = System.currentTimeMillis();
		
		while (Thread.currentThread() == refreshRate) {
			updatePosition();
			repaint();
			
		    try {
		    	tm += 20;	
				Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
		    } catch (InterruptedException e) {
		    	break;
		    }
		}
		if(Thread.currentThread() == client){
			String message;
			try{
				Socket clientSock = new Socket(host, PORT);
				
				Functions.socketSend(clientSock, "n " + p1.name);
				String id = Functions.socketSend(clientSock, "i");
				
				double x, y;
				message = Functions.socketSend(clientSock, "g c " + id);
				
				
				x = Double.parseDouble(message.substring(0,message.indexOf(",")));
				y = Double.parseDouble(message.substring(message.indexOf(",") + 1));
				p1 = new Player(new Coordinates(x, y));
				
				
				
				while (true){
					for(int i = 0; i < 3; i++){
						if(i != Integer.parseInt(id)){
							message = Functions.socketSend(clientSock, "g c " + i);

							if (message.indexOf("Invalid") == -1){
								x = Double.parseDouble(message.substring(0,message.indexOf(",")));
								y = Double.parseDouble(message.substring(message.indexOf(",") + 1));
								if(players[i] == null){
									players[i] = new Player(new Coordinates(x, y));
									players[i].name = Functions.socketSend(clientSock, "g n " + i);
								}
								else players[i].setPosition(new Coordinates(x, y));
							}
							else{
								if(players[i] != null) players[i] = null;
							}
						}
						else{
							Functions.socketSend(clientSock, "p c " + p1.getPosition().x + "," +p1.getPosition().y);
						}
					}
					
					Thread.sleep(5); //refresh ms
				}
			}
			catch (Exception e) {} //reconnect
		}
	}

	public void update(Graphics g) {
		bufferI = createImage(getSize().width, getSize().height);
		bufferG = bufferI.getGraphics();
		bufferG.setColor(getBackground());
		bufferG.fillRect(0, 0, getSize().width, getSize().height);
		
		m1.paint(bufferG, p1.getPosition(), getSize());
		
		for(int i = 0; i < players.length; i++){
			if(players[i] != null)
				players[i].paint2(bufferG, getSize().width/2 - (int)((p1.getPosition().x - players[i].getPosition().x) * 50),getSize().height/2 - (int)((p1.getPosition().y - players[i].getPosition().y) * 50));
		}
		
		
		p1.paint(bufferG, getSize().width/2, getSize().height/2);
		p1.paintVision(bufferG, m1, p1.getPosition(), getSize());
		
		g.drawImage(bufferI, 0, 0, null);
	}
	
	public void keyPressed(KeyEvent e){		
		for(int i = 0; i < keys.length; i++){
			if(e.getKeyChar() == keys[i]) pressedKeys[i] = true;
		}
		e.consume();
	}

	public void keyReleased(KeyEvent e) {
		for(int i = 0; i < keys.length; i++){
			if(e.getKeyChar() == keys[i]) pressedKeys[i] = false;
		}
		e.consume();
	}
	
	public void keyTyped(KeyEvent arg0) {
	}
	
	private void updatePosition(){
		
		int movX = 0;
		int movY = 0;
		
		if(pressedKeys[0]) movY--;
		if(pressedKeys[2]) movY++;
		if(pressedKeys[1]) movX--;
		if(pressedKeys[3]) movX++;
		/*if (movX != 0) {
			p1.myx+=5;
		}
		else {
			if (p1.myx > 50) p1.myx-=2;
		}
		if (movY != 0) {
			p1.myy+=5;
		}
		else {
			if (p1.myy > 50) p1.myy-=2;
		}
		
		if(pressedKeys[4]) {
			p1.myx+=2;
			p1.myy+=2;
		}
		else {
			if (p1.myx > 50){
				p1.myx-=2;
			}
			if (p1.myy> 50){
				p1.myy-=2;
			}
		}*/
		
		if(movX != 0 || movY != 0){
			double oldX = p1.getPosition().x;
			double oldY = p1.getPosition().y;
			
			double newX = p1.getPosition().x + (movX * p1.speed);
			double newY = p1.getPosition().y + (movY * p1.speed);

			for(int i = (int)newY - 1; i <= (int)newY + 1; i++){
				for(int j = (int)newX - 1; j <= (int)newX + 1; j++){
					if(m1.getMatrixVal(j, i) == 1){
						
						if(Functions.collision(new Coordinates(oldX, newY), new Coordinates(j,i)))
							newY = i - movY;
						
						if(Functions.collision(new Coordinates(newX, oldY), new Coordinates(j,i)))
							newX = j - movX;
						
					}
				}
			}
			p1.setPosition(new Coordinates(newX, newY));
		}	
	}	

	
}
