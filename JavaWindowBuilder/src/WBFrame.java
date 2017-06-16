import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;


public class WBFrame {
	JFrame mFrame;
	private JTextField textField;
	
	public WBFrame(){
		mFrame = new JFrame();
		
		mFrame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(110, 149, 93, 23);
		mFrame.getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(88, 58, 149, 31);
		mFrame.getContentPane().add(textField);
		textField.setColumns(10);
		
		mFrame.setSize(300,200);
		mFrame.setVisible(true);
	}
	
	public static void main(String [] args) {
		new WBFrame();
		
	}
}
