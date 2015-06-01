package GUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import selpm.control.ps.PSKeyEvent;
import Controller.EV3PSController;

public class Window{
	private  EV3PSController ev3controller = null;
	
	
	public static void main(String[] args){
		System.out.println("ev3pscontroller test kunokuno");
		Window window = new Window();
	}
	
	public Window(){
		  //============================================================================
		  //まずは、基礎フレームの設定。
		  //============================================================================
		  JFrame frame = new JFrame("PS test kunokuno");
		  frame.setVisible(true);
			
		  frame.setSize(250,250);
		  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  
		 ev3controller = new EV3PSController();
		  
		  WindowListener listener = new WindowAdapter() {
				public void windowClosing(WindowEvent w) {
					if ( ev3controller != null)
						ev3controller.destruct();
					
					System.exit(0);
				}
			};
			frame.addWindowListener(listener);	  
	}
}
