package game;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame {

	public GameFrame() {
		Frame app = new Frame("MyGame");
		app.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		app.setLocation(300, 200);
		GamePanel gp = new GamePanel();
		//app.setSize(320, 320);
		//app.add(gp);
		app.add(gp, BorderLayout.CENTER);
        app.pack();
        app.setResizable(false);
		app.setVisible(true);
		
		gp.gameStart();
	}

	public static void main(String args[]) {
		new GameFrame();
	}
}