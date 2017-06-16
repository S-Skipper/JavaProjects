package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;

public class Food {

	public Point location;
	public Point size;
	private Random rand;
	
	public Food(GamePanel gp,Snake snake) {
		rand = new Random();
		generateLocation(gp.width,gp.height);
		size = new Point(snake.diameter, snake.diameter);
		
	}

	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(location.x, location.y, size.x, size.y);
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public void generateLocation(int width,int height){
        location = new Point(Math.abs(rand.nextInt() % width), Math.abs(rand.nextInt() % height));
	}

}
