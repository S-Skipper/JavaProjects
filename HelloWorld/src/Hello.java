import javax.swing.JFrame;


@SuppressWarnings("serial")
public class Hello extends JFrame{
	
	public static void main(String [] args){
		System.out.println("Hello Java!");
		
		JFrame frame = new JFrame();
		frame.setTitle("Frame");
		frame.setSize(300, 200);
		//frame.add(new Button("Button"));
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setLocation(200, 200);
		frame.setVisible(true);
		
	}
}



