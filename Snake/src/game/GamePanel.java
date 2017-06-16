package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@SuppressWarnings("serial")
public class GamePanel extends Panel implements Runnable, KeyListener{
	private static final int FPS = 30;
	public static final int SOUTH = 0;
	public static final int NORTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;
	
	private Thread gameThread;
	private Graphics dbg;
	private Image image;
	
	private Snake snake;
	private Food food;
	
	public int width,height;
	private int direction;
	private boolean isPaused,isRunning;
	
	public GamePanel()
	{
		width = 300;
		height = 300;
		setPreferredSize(new Dimension(width, height));
		isRunning = false;
		isPaused = false;
		
		snake = new Snake(this);
		food = new Food(this,snake);
		
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
	}
	
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	@SuppressWarnings("deprecation")
	public void gameStop() {
		isRunning = false;
		gameThread.stop();
	}
	
	@Override
	public void run() {
		long t1,t2,dt,sleepTime;
		long period = 1000/FPS;
		t1 = System.nanoTime();
		
		while (true) {
			gameUpdate();
			gameRender();
			gamePaint();
			
			t2 = System.nanoTime();
			dt = (t2-t1)/1000000L;
			sleepTime = period - dt;
			if (sleepTime<=0) {
				sleepTime = 2;
			}
			
			try {
				Thread.sleep(300);
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				t1=System.nanoTime();
			}
			//System.out.println(x);
		}
	}
		
	public void gameStart()
	{
		//width = this.getWidth();
		//height = this.getHeight();
		if (!isRunning) {
			gameThread = new Thread(this);
			gameThread.start();
		}
		
	}

	public void gameUpdate()
	{
		if (!isPaused) {
			snake.Bounce(food);
			snake.update();
		}
		
	}
	
	//--------- Ë«»º³å»æÖÆ -------------//
	public void gameRender()
	{
		if (image == null) {
			image = createImage(getWidth(), getHeight());
			dbg = image.getGraphics();
		}
		dbg.setColor(Color.WHITE);
		dbg.fillRect(0, 0, width, height);
		//dbg.clearRect(0, 0, width, height);
		
		snake.draw(dbg);
		food.draw(dbg);
	}
	
	public void gamePaint()
	{
		Graphics g;
		try {
			g = this.getGraphics();
			if (g!=null&&image!=null) {
				g.drawImage(image, 0, 0, null);
			}
			g.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//--------- Ë«»º³å»æÖÆ -------------//

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if (keyCode==KeyEvent.VK_P || keyCode==KeyEvent.VK_SPACE) {
			isPaused = !isPaused;
		}
		
		if (!isPaused) {
			switch (keyCode) {
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				setDirection(SOUTH);
				break;
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				setDirection(NORTH);
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				setDirection(WEST);
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				setDirection(EAST);
				break;
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}