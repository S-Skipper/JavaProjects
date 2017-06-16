package ddd;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

@SuppressWarnings("serial")
public class GameApplet extends Applet{

	MyPanel gp;
	
	public void init()
	{
		gp = new MyPanel();
		add(gp);
		setVisible(true);
	}
	
	public void start()
	{
		System.out.println("start...");
		gp.gameStart();
	}
}

@SuppressWarnings("serial")
class MyPanel extends Panel implements Runnable{
	private static final int FPS = 50;
	
	private int x;
	private int y;
	private int d;
	private Image image;
	
	public MyPanel()
	{
		x=50;
		y=50;
		d=30;
	}
		
	public void gameStart()
	{
		Thread gameThread = new Thread(this);
		gameThread.start();
	}

	public void gameUpdate()
	{
		x++;
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
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				t1=System.nanoTime();
			}
			//System.out.println(x);
		}
	}
	
}
