package bak;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class GameFrame1 {

	GameFrame1() {
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

	}

	public static void main(String args[]) {
		new GameFrame1();
	}
}

@SuppressWarnings("serial")
class MyPanel extends Panel {
	public void paint(Graphics g) {
		// Graphics
		// draw
		g.setColor(Color.BLUE);
		g.drawLine(20, 20, 200, 20);
		// fill
		g.setColor(Color.RED);
		g.fillRect(20, 30, 200, 300);

		// Graphics2D
		// draw
		Point2D p1 = new Point2D.Double(20, 350);
		Point2D p2 = new Point2D.Double(360, 350);
		Line2D line = new Line2D.Double(p1, p2);
		Graphics2D graphics2d = (Graphics2D) g;
		graphics2d.setColor(Color.GREEN);
		graphics2d.draw(line);
		// fill

		Rectangle2D rec = new Rectangle2D.Double(260, 30, 200, 300);
		GradientPaint blueToRed = new GradientPaint(260, 30, Color.BLUE, 460, 330,
				Color.RED);
		graphics2d.setPaint(blueToRed);
		graphics2d.fill(rec);
				
		//AffineTransform
		//每一次变换都是相对于左上角而言
		//Graphics2D graphics2d = (Graphics2D) g;
		Rectangle2D rect = new Rectangle2D.Double(30, 360, 50, 50);
		graphics2d.draw(rect);
		
		AffineTransform at = new AffineTransform();  //start transform
		at.setToTranslation(100, 10);  
		//at.rotate(Math.PI/8.0);
		//at.setToShear(2.0, 2.0);     //错切变换
		//at.scale(2.0, 2.0);
		graphics2d.setTransform(at);
		
		graphics2d.setColor(Color.BLACK);
		graphics2d.draw(rect);
	}
}