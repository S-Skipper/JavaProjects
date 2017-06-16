package bak;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame2 {

	GameFrame2() {
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
	}

	public static void main(String args[]) {
		new GameFrame2();
	}
}

@SuppressWarnings("serial")
class MyPanel extends Panel {
	private int x;
	private int y;
	private int d;
	
	public MyPanel()
	{
		x=50;
		y=50;
		d=30;
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval(x, y, d, d);
	}
	
	public void gameLoop(){
		while (true) {
			x++;
			for (int i = 0; i < 10000000; i++) {}
			repaint();
		}
	}
}