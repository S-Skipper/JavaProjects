package ddd;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

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
			System.out.println(x);
		}
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
	
}