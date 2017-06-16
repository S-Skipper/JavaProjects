package ddd;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame {

	GameFrame() {
		Frame f = new Frame("MyGame");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		MyPanel mp = new MyPanel();
		f.setLocation(300, 200);
		f.setSize(500, 500);
		f.add(mp);
		f.setVisible(true);
		//mp.gameLoop();
		mp.gameStart();
	}

	public static void main(String args[]) {
		new GameFrame();
	}
}

@SuppressWarnings("serial")
class MyPanel extends Panel implements Runnable, KeyListener{
	private static final int FPS = 50;
	private static final int SOUTH = 0;
	private static final int NORTH = 1;
	private static final int EAST = 2;
	private static final int WEST = 3;
	
	
	private int width,height;
	private int x,y,d;
	private int dx,dy;
	private int direction;
	private boolean isPaused;
	//private int dcx,dcy;
	//private int d;
	private Image image;
	
	public MyPanel()
	{
		x=50;
		y=50;
		d=30;
		dx=5;
		dy=5;
		//dcx=1;
		//dcy=2;
		direction = EAST;
		isPaused = true;
		addKeyListener(this);
		//System.out.println("width="+this.getWidth()+",height="+height);
	}
	
	@Override
	public void run() {
		long t1,t2,dt,sleepTime;
		long period = 1000/FPS;
		
		t1 = System.nanoTime();
		
		while (true) {
			if (!isPaused) {
				gameUpdate();
			}
			
			gameRender();
			gamePaint();
			
			t2 = System.nanoTime();
			dt = (t2-t1)/1000000L;
			sleepTime = period - dt;
			if (sleepTime<=0) {
				sleepTime = 2;
			}
			
			try {
				//Thread.sleep(1000);
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				t1=System.nanoTime();
			}
			//System.out.println(x);
		}
	}
		
	public void gameStart()
	{
		width = this.getWidth();
		height = this.getHeight();
		Thread gameThread = new Thread(this);
		gameThread.start();
	}

	public void gameUpdate()
	{
		switch (direction) {
		case SOUTH:
			y+=dy;
			break;
		case NORTH:
			y-=dy;
			break;
		case EAST:
			x+=dx;
			break;
		case WEST:
			x-=dx;
			break;
		default:
			break;
		}
		
		/*x+=dx;
		y+=dy;
		if((x<0)||(x>width-d))  dx = -dx;
		if((y<0)||(y>height-d)) dy = -dy;*/
	}
	
	//--------- Ë«»º³å»æÖÆ -------------//
	public void gameRender()
	{
		image = createImage(getWidth(), getHeight());
		Graphics dbg = image.getGraphics();
		
		dbg.setColor(Color.RED);
		dbg.fillOval(x, y, d, d);
	}
	
	public void gamePaint()
	{
		Graphics g = getGraphics();
		g.drawImage(image, 0, 0, null);
	}
	//--------- Ë«»º³å»æÖÆ -------------//

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		switch (keyCode) {
		case KeyEvent.VK_P:
		case KeyEvent.VK_SPACE:
			isPaused = !isPaused;
			break;
		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:
			direction = SOUTH;
			break;
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			direction = NORTH;
			break;
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			direction = WEST;
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
			direction = EAST;
			break;
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
