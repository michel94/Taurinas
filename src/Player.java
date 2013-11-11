import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player{
	//Server
	private Coordinates position;
	private int battery, hp;
	public String name = "Graca";
	public double speed = 0.2;
	public int myx = 50, myy = 50;
	//private Items[20] items;
	
	BufferedImage image = null; 
	
	
	//Client
	private int SIZE = 50;
	
	public Player(Coordinates initialPos){
		position = initialPos;
		
		try{
			image = ImageIO.read(new File("../Img/Player/1.png"));
		}catch (IOException e) {}
		
		battery = 100;
		hp = 100;
	}
	
	
	//Server
	public void setPosition(Coordinates newPos){
		
		if(newPos.x < 0) newPos.x += 30;
		else if(newPos.x > 30) newPos.x -= 30;
		
		if(newPos.y < 0) newPos.y += 30;
		if(newPos.y > 30) newPos.y -= 30;
		
		position = newPos;
		
	}
	
	public Coordinates getPosition(){
		return position;
	}
	
	//Client
	public void paint(Graphics g, int x, int y){
		g.drawImage(image, x, y, myx, myy, null);
		g.setColor(Color.RED);
		g.drawString(name, x + 1, y);
	}
	
	public void paint2(Graphics g, int x, int y){
		for(int i = -1; i <= 1; i++){
			for(int j = -1; j <=1; j++){
				paint(g, x + (j*30*50), y  + (i*30*50));
			}
		}
	}
	
	public void paintVision(Graphics g, Map m1, Coordinates playerPos, Dimension screen){
		int x = (int)(playerPos.x);
		int y = (int)(playerPos.y);
		double restoX = (playerPos.x%1);
		double restoY = (playerPos.y%1);
		
		
		int pixelX, pixelY;
		int plPixelX = screen.width/2 + 25;
		int plPixelY = screen.height/2 + 25;
		
		for(int i = y-7; i < y + 7; i++){
			for (int j = x-14;  j < x + 14; j++) {
				
				pixelX = screen.width/2 - 15*50 + (int)((j-x+15)*50 - restoX * 50);
				pixelY = screen.height/2 - 8*50 + (int)((i-y+8)*50 - restoY * 50);				
				
				if(m1.getMatrixVal(j, i) == 1){
					for(int a = 0; a < 2; a++){
						for(int b = 0; b < 2; b++){
							Coordinates intersection = Functions.findScreenIntersect(new Coordinates(plPixelX, plPixelY), new Coordinates(pixelX + (b*50), pixelY + (a*50)), screen);
							g.drawLine(pixelX + (b*50), pixelY + (a*50), (int)intersection.x, (int)intersection.y);
							
						}
					}
				}
			}
		}
	}
	
	public Coordinates[] getPixelCoord(int x, int y){
		Coordinates[] coord = new Coordinates[9];
		int t = 0;
		for(int i = -1; i <= 1; i++){
			for(int j = -1; j <=1; j++){
				coord[t] = new Coordinates(x + (j*30*50), y  + (i*30*50));
			}
		}
		
		return coord;
	}
}
