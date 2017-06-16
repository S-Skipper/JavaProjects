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
class MyPanel extends Panel {
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
		Timer timer = new Timer();
		MyTimerTask game = new MyTimerTask();
		timer.scheduleAtFixedRate(game, 0, 1000/50);
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
	
	public void paint(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval(x, y, d, d);
		g.dispose();
	}
	
	public void gameLoop(){
		while (true) {
			//repaint();
			for (int i = 0; i < 10000000; i++) {}
			gameUpdate();
			gameRender();
			gamePaint();
		}
	}
	
	class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			gameUpdate();
			gameRender();
			gamePaint();
		}
		
	}
}