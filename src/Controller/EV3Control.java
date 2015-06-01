package Controller;

import Kogakusai2015.EV3.ShootingListener;
import Kogakusai2015.EV3.SoccerEventListener;
import selpm.control.ps.PSKeyEvent;

public class EV3Control extends Thread implements ShootingListener, SoccerEventListener{
	
	public KeyStatuses key = new KeyStatuses();
	
	private boolean enabled = false;
	
	private int controllerMode = 0;
	private int controllerMode_Max = 1;
	
	private EV3 ev3;
	
	private final int forwardButton 	= PSKeyEvent.PSK_CIRCLE;	//前進ボタン
	private final int backwardButton 	= PSKeyEvent.PSK_X;			//後進ボタン
	private final int steeringStick 	= PSKeyEvent.PSK_JX;		//ステアリング
	private final int axelStick 		= PSKeyEvent.PSK_JY;
	
	private final int leftStick			= PSKeyEvent.PSK_JY;
	private final int rightStick 		= PSKeyEvent.PSK_JRZ;
	
	private boolean threadRunnning = false;
	private long threadSpan = 100;
	
	private float forwardSpeed = (float) 0.75;
	private final float speedDwonRate = (float)0.7;
	private float forwardSpeed_prev = forwardSpeed;
	
	private boolean moving = false;
	private boolean shooting = false;
	
	
	private Timer shootTimer = new Timer();
	private final long coolDown_shoot = 3000; // シュートできるようになるまでの時間 (3000ms)
	
	// colors
	//private String[] colorID_stop = {"-1.00", "7.00"};
	private String[] colorID_stop = {};
	private String[] colorID_normal = {"6.00"};
	private String[] colorID_speedDown = {"1.00"};
	
	
	public EV3Control(String name){
		ev3 = new EV3(name);
		
		shootTimer.set(coolDown_shoot);	
	}
	
	public EV3Control(String name, String sensorPort){
		ev3 = new EV3(name, sensorPort);
		shootTimer.set(coolDown_shoot);
	}
	
	/**
	 * 終了するときに呼び出す
	 */
	public void destruct(){
		this.threadRunnning = false;
		try{
			if ( ev3 != null)
				ev3.destruct(); // モーターを停止し、接続を切る
		}
		catch (Exception e){
			System.out.println("EV3Control destruct() exception");
		}
	}
	
	@Override
	public void run(){
		threadRunnning = true;
		while(threadRunnning){
			try {
				Thread.sleep(threadSpan);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if( !this.enabled)
				continue;
			
			if ( this.ev3.isConnected() ){
				if ( this.isForward() == 0 & !this.key.isPushed(steeringStick) & this.moving){
					this.EV3_forward(0);
					this.moving = false;
				}
				else if ( this.isForward() != 0){
					this.EV3_forward(forwardSpeed * this.isForward());
					moving = true;
					
				}
				
				else if ( this.key.isPushed(steeringStick) & this.isForward() == 0 ){
					this.rotate( (float)(forwardSpeed * 0.5 * key.getValue(steeringStick) ));
					moving = true;
					
				}
				
			}
		}
	}
	
	public void changeMode(){
		this.controllerMode++;
		if (this.controllerMode > this.controllerMode_Max)
			this.controllerMode = 0;
	}
	
	public void EV3_forward(float speed){
		speed = this.speedFilter(speed); // color sensor の値からスピードを変更する
		
		if (this.controllerMode == 0)
			this.EV3_forward_mode0(speed);
		else if (this.controllerMode == 1)
			this.EV3_forward_mode1(speed);
	}
	
	/**
	 * 
	 * @param speed 回転するスピード ( (left)-1 <= speed <= 1(right) )
	 */
	public void rotate(float speed){
		speed = this.speedFilter(speed);
		ev3.forward("A", speed, "D", -speed);
	}
	
	
	/**
	 * forward: circle button
	 * backward: X button
	 * @param speed
	 */
	private void EV3_forward_mode0(float speed){
		
		System.out.println("forward, spped:" + speed);
		float speed2 =speed;
		
		float steering = key.isPushed(steeringStick)? 	(float)key.getValue(steeringStick):  0;
		
		ev3.forward("A", "D", speed2, (float) ( steering * 0.6) );
	}
	
	/**
	 * right: right stick
	 * left: left stick
	 * @param speed
	 */
	private void EV3_forward_mode1(float speed){
		float LMotorSpeed = key.isPushed(leftStick) 	? -(float)key.getValue(leftStick)	: 0;
		float RMotorSpeed = key.isPushed(rightStick)	? -(float)key.getValue(rightStick)	: 0;
		
		System.out.println("LStick: "+ key.isPushed(leftStick)+ " , value: "+ key.getValue(leftStick));
		
		LMotorSpeed *= speed;
		RMotorSpeed *= speed;
		
		ev3.forward("A", LMotorSpeed, "D", RMotorSpeed);
	}
	
	public void shoot(){
		if (!this.enabled)
			return;
		
		if ( !this.shooting ){
			new ShootThread(this.ev3, this).start(); // シュートする
		}
		else
			System.out.println("shooting");
	}
	
	private class ShootThread extends Thread{
		private EV3 ev3;
		ShootingListener slistener;
		
		public ShootThread(EV3 ev3, ShootingListener listener){
			this.ev3 = ev3;
			this.slistener = listener;
		}
		
		@Override
		public void run(){
			slistener.shoot_start();
			this.ev3.shoot();
			slistener.shooted();
		}
	}
 
	public String getSensorValue() {
		// TODO Auto-generated method stub
		return ev3.getSensorValue();
	}
	
	public float speedFilter(float speed){
		if (speed == 0)	return 0;
		if (speed < 0) this.forwardSpeed_prev = -Math.abs(forwardSpeed_prev);
		
		if (this.ev3 == null ) return this.forwardSpeed_prev;
		if (this.ev3.getSensorValue() == null) return this.forwardSpeed_prev;
		
		// Stop
		if ( exists(colorID_stop, this.ev3.getSensorValue()) ){
			//System.out.println("sensorvalue = 7.00");
			return 0;
		}
		// Normal
		else if ( exists(colorID_normal, this.ev3.getSensorValue()) ){
			//System.out.println("sensorvalue else");
			return  speed;
		}
		// Speed down
		else if ( exists(colorID_speedDown, this.ev3.getSensorValue()) ){
			return (float) (speed * speedDwonRate);
		}
		else{
			System.out.println("color not defined, color ID: " + this.ev3.getSensorValue() );
			return speed;
		}
	}
	public void opposite(){
		String[] tmp = colorID_normal;
		colorID_normal = colorID_speedDown;
		colorID_speedDown = tmp;
	}
	
	public boolean isConnected(){
		return this.ev3.isConnected();
	}
	
	private boolean exists(String[] list, String element){
		boolean ret = false;
		for ( String list_element : list){
			if ( list_element != null && list_element.equals(element) ){
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	private float isForward(){
		if (this.key.isPushed(axelStick) ){
			return (float) -this.key.getValue(axelStick);
		}
		else if (this.key.isPushed(forwardButton	) ){
			return (float)1.0;
		}
		else if (this.key.isPushed(backwardButton) ){
			return (float)-1.0;
		}
		else 
			return 0;
	}

	@Override
	public void shooted() {
		// TODO Auto-generated method stub
		System.out.println("shooted");
		shooting = false;
	}

	@Override
	public void shoot_start() {
		// TODO Auto-generated method stub
		System.out.println("shoot start");
		shooting = true;
	}

	
	@Override
	public void soccer_start() {
		// TODO Auto-generated method stub
		this.enabled = true;
	}

	@Override
	public void soccer_finish() {
		// TODO Auto-generated method stub
		this.enabled = false;	
		this.EV3_forward(0);
	}
}
