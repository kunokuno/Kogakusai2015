package Controller;


import selpm.control.ps.AbstractPSController;
import selpm.control.ps.PSKeyEvent;

public class EV3PSController extends AbstractPSController{
	public EV3Control kunokuno = null;
	public EV3Control kakerun = null;
	
	private boolean enabled = false;
	
	private final int playerCode_kunokuno = PSKeyEvent.PLAYER1;
	private final int playerCode_kakerun = PSKeyEvent.PLAYER2;
	
	public EV3PSController(){
		kakerun = new EV3Control("10.0.1.1", "S2");
		if ( kakerun.isConnected())
			System.out.println("connected to kakerun");
		else
			System.out.println("failed to connect to kakerun");
		
		kunokuno = new EV3Control("10.0.1.2", "S3");
		if ( kunokuno.isConnected() )
			System.out.println("connected to kunokuno");
		else 
			System.out.println("failed to connect to kunokuno");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		kunokuno.start();
		kakerun.opposite();
		kakerun.start();
		
		this.enable();
	}
	
	public void enable(){
		this.enabled = true;
	}
	public void disable(){
		this.enabled = false;
	}
	
	/**
	 * 終了するときに呼び出す
	 */
	public void destruct(){
		if (kunokuno != null)
			kunokuno.destruct();//
		if ( kakerun != null)
			kakerun.destruct(); //
	}
	
	@Override
	public void pressed(PSKeyEvent e) {
		// TODO Auto-generated method stub
		if ( !enabled ) return;
		
		EV3Control ev3 = null;
		if (e.getPlayerCode() == playerCode_kunokuno){
			ev3 = kunokuno;
			System.out.println("kunokuno: ");
		}
		else if (e.getPlayerCode() == playerCode_kakerun){
			ev3 = kakerun;
			System.out.println("kakerun: ");
		}
		else{
			System.out.println("Error, unknown player code.");
			return;
		}
		
		if ( ev3 == null ){
			System.out.println("error, ev3 is null");
			return;
		}
		
		
		boolean JX_updated = false;
		// フラグをセット
		switch( e.getKeyCode() ){
			case PSKeyEvent.PSK_JX:
			case PSKeyEvent.PSK_JY:
			case PSKeyEvent.PSK_JZ:
			case PSKeyEvent.PSK_JRZ:
				JX_updated = ev3.key.pushed(e.getKeyCode(), e.getValue());
				break;
				
			default:
				ev3.key.pushed(e.getKeyCode());
				break;
		}
		
		switch( e.getKeyCode() ){
		case PSKeyEvent.PSK_JX:
			/*
				if ( JX_updated )
					ev3.EV3_forward((float)0.6);
					*/
			break;
			
		case PSKeyEvent.PSK_JY:
			break;
			
		case PSKeyEvent.PSK_JZ:
		case PSKeyEvent.PSK_JRZ:
		case PSKeyEvent.PSK_POV:
		break;
		
		case PSKeyEvent.PSK_RECTANGLE:
			System.out.println("sensor: " + ev3.getSensorValue());
			break;
			
		case PSKeyEvent.PSK_L1:
		case PSKeyEvent.PSK_R1:
		case PSKeyEvent.PSK_L2:
		case PSKeyEvent.PSK_R2:
			ev3.shoot();
			
		break;
		
		case PSKeyEvent.PSK_TRIANGLE:
			//ev3.changeMode();
		break;	
			
		case PSKeyEvent.PSK_CIRCLE:
		case PSKeyEvent.PSK_X:
			// ev3.EV3_forward((float)0.6);	
		break;	
			//System.out.print( "　：　"+e.getValue() );
		}
		
		
		
	}

	@Override
	public void released(PSKeyEvent e) {
		if ( !this.enabled ) return;
		
		// TODO Auto-generated method stub
		EV3Control ev3 = null;
		if (e.getPlayerCode() == playerCode_kunokuno){
			ev3 = kunokuno;
			System.out.println("kunokuno: key released");
		}
		else if (e.getPlayerCode() == playerCode_kakerun){
			ev3 = kakerun;
			System.out.println("kakerun: key released");
		}
		else{
			System.out.println("Error, unknown player code.");
			return;
		}
		
		//フラグをセット
	 	ev3.key.released(e.getKeyCode());
	 	
		switch( e.getKeyCode() ){
		case PSKeyEvent.PSK_JX:
			System.out.println("psk_jx released");
			break;
			
		case PSKeyEvent.PSK_CIRCLE:
		case PSKeyEvent.PSK_X:
			//ev3.EV3_forward((float)0.6);
		break;
		}
		
	}
}