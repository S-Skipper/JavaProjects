package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Snake {

	public static final int MAX_LENGTH = 20;
	
	private GamePanel gp;
	
	private Point [] body;
	private int length;
	private int head,tail;
	public int diameter;
	private int x,y;
	private int speed;
	
	public Snake(GamePanel gamePanel) {
		this.gp = gamePanel;
		
		body = new Point[MAX_LENGTH];
		head = -1;
		tail = -1;
		length = 1;
        x = 50;
        y = 50;
        diameter = 10;
        speed = 10;
	}

	public void draw(Graphics g) {
		g.setColor(Color.BLUE);
		if (length>1) {
			int i=tail;
			while (i != head) {
                g.fillOval(body[i].x, body[i].y, diameter, diameter);
                i = (i + 1) % body.length;
            }
		}
		
		g.setColor(Color.GREEN);
		g.fillOval(body[head].x, body[head].y, diameter, diameter);
	}

	public void update() {
		int direction = gp.getDirection();
		switch (direction) {
		case GamePanel.SOUTH:
			y += speed;
			break;
		case GamePanel.NORTH:
			y -= speed;
			break;
		case GamePanel.EAST:
			x += speed;
			break;
		case GamePanel.WEST:
			x -= speed;
			break;
		}
		
		if (x>gp.width) {
			x = -diameter;
		}
		if (y>gp.height) {
			y = -diameter;
		}
		if (x < -diameter) {
			x = gp.width;
		}
		if (y < -diameter) {
			y = gp.height;
		}
		
		//System.out.println(body.length);
		head = (head+1)%body.length;
		tail = (head + body.length - length + 1) % body.length;
		body[head]=new Point(x,y);
	}
	
	public boolean Bounce(Food food){
		if (Math.abs(x-food.location.x)<(diameter/2+food.size.x/2)
		  &&Math.abs(y-food.location.y)<(diameter/2+food.size.y/2)) 
		{
			food.generateLocation(gp.width, gp.height);
			if (length<MAX_LENGTH) {
				length++;
			}
			return true;
		}
		
		return false;
	}

}
