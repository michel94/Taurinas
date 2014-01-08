import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;


public class Functions {
	public static boolean collision(Coordinates player, Coordinates tile){
		int size = 1;
		
		double x = player.x - tile.x;
		double y = player.y - tile.y;
		
		if(x < 0) x *= -1;
		if(y < 0) y *= -1;
		
		if(x < size && y < size) return true;
		else return false;
	}
	
	public static boolean semiCollision(double x1, double x2){
		int size = 1;
		double x = x1 - x2;
		
		if(x < 0) x *= -1;
		
		if(x < size) return true;
		else return false;
	}
	
	public static String socketSend(Socket clientSock, String s){
		PrintStream pw;
		Scanner input = null;
		OutputStream os;
		
		try {
			os = clientSock.getOutputStream();
			pw = new PrintStream(os, true);
		
			pw.println(s);
			
			input = new Scanner(clientSock.getInputStream());
		} catch (IOException e) {}
		
		return input.next();
	}
	
	public static Coordinates findScreenIntersect(Coordinates origin, Coordinates second, Dimension screen){
		double realX = 0;
		double realY = 0;
		
		double dx = second.x - origin.x;
		double dy = second.y - origin.y;
		
		if(origin.x > second.x) realX = 0;
		else if(origin.x < second.x) realX = screen.width;
		else dy = 0.0001; //maneira anceleira de evitar divide by zero
		
		if(origin.y > second.y) realY = 0;
		else if(origin.y < second.y) realY = screen.width;
		else dy = 0.0001;

		
		double k1 = (realX-origin.x)/dx;
		Coordinates result1 = new Coordinates(realX, origin.y + dy*k1);
		
		//double k2 = (screen.height-origin.y)/dy;
		//Coordinates result2 = new Coordinates(origin.x + dx*k2, screen.width);
		
		//falta verificar qual está mais perto
		
		
		
		
		return result1;
	}
	
	public static Coordinates closer(Coordinates origin, Coordinates a, Coordinates b){
		return b; 
	}
}
