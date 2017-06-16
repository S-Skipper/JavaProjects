package dtt;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class MyGraphics extends JFrame{

	public MyGraphics(){
		setTitle("Draw Ovals");
		setLocation(300, 200);
		getContentPane().add(new Oval());
	}
	class Oval extends JPanel
	{
		public void paintComponent(Graphics g)
		{
			g.drawOval(10,30,100,60);
			g.setColor(Color.red);
		    g.fillOval(130,30,100,60);
			
			//things to do: drawLines
			int x[]={10,20,30,40,50,60,70,80,90,100};
			int y[]={1,4,9,16,25,36,47,64,81,100};
		    g.drawPolyline(x,y,10);
			for(int i=0;i<9;i++)
			{
				int a=200-y[i];
				int b=200-y[i+1];
				g.drawLine(x[i],a,x[i+1],b);
			}
		}
	}

	public static void main(String [] args)
	{
		 MyGraphics f = new  MyGraphics();
		 f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 f.setSize(300,200);
		 f.setVisible(true);
	}
	
}
