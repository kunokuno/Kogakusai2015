package Controller;


import java.rmi.RemoteException;
import java.util.HashMap;

import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;
/**
 * EV3への接続を行う
 * @author motoki
 * @version 1.0
 * @see RemoteEV3
 */
public class EV3 {
	private RemoteEV3 rev3;
	private Sensor colorSensor;
	private boolean isConnected = false;
	
	// MOTORS
	String[] motorNames = {"A", "B", "C", "D"};
	private HashMap<String, RMIRegulatedMotor> motors = new HashMap<String, RMIRegulatedMotor>();
	
	
	/**
	 * 別スレッドでEV3に接続する
	 * @param name 接続するEV3の名前（IPアドレス）
	 * @see ConnectThread
	 */
	public EV3(String name){
		
		this.connect_thread(name);
	}
	
	/**
	 * 別スレッドでEV3に接続する
	 * @param name 接続するEV3の名前（IPアドレス）
	 * @param sensorPort 利用するセンサーのポート
	 * @see ConnectThread
	 */
	public EV3(String name, String sensorPort){
		this.connect_thread(name, sensorPort);
	}
	
	/**
	 * EV3には接続しない
	 * connect() または connect_thread()を呼び出し、手動で接続する
	 */
	public EV3(){
		
	}
	
	/**
	 * モーターを停止し、センサーの読み取りを停止し、EV3との接続を切る
	 * 終了する場合に呼び出す
	 */
	public void destruct(){
		try{
			this.stopMotors();
			this.closeSensor();
		}
		catch (Exception e){
			System.out.println("EV3::destruct() error");
		}
		finally{
			
		}
		
		this.isConnected = false;
		
	}
	
	/**
	 * EV3に接続する
	 * 接続に失敗した場合プログラムが停止する
	 * @param name 接続するEV3の名前（IPアドレス）
	 */
	public void connect(String name){
		this.connect(name, "S2");
	}
	
	/**
	 * EV3に接続する
	 * 接続に失敗した場合プログラムが停止する
	 * @param name 接続するEV3の名前（IPアドレス）
	 * @param sensorPort 利用するセンサーのポート
	 */
	public void connect(String name, String sensorPort){
		try {
			System.out.println("connectiong to "+ name);
			rev3 = new RemoteEV3(name);
			System.out.println("connected to "+ name);
			
		} catch (Exception e) {
			System.out.println("failed to connect to "+ name);
			e.printStackTrace();
		}
		System.out.println("creating regulated motors...");
		createRegulatedMotors();
		
		System.out.println("starting sensor...");
		try {
			colorSensor = new Sensor(rev3, sensorPort);	
			colorSensor.start();
			
		}
		catch (NullPointerException e){
			System.out.println("[[sensor NullPointerException]]");
			e.printStackTrace();
		}
		System.out.println("finished to start EV3.");
		isConnected = true;
	}
	
	/**
	 * EV3に接続する
	 * 別スレッドで接続を行い、一定時間接続処理が終わらなかった場合は接続失敗とする
	 * @param name 接続するEV3の名前（IPアドレス）
	 * @see ConnectThread
	 */
	public void connect_thread(String name){
		this.connect_thread(name, "S2");
	}
	
	/**
	 * EV3に接続する
	 * 別スレッドで接続を行い、一定時間接続処理が終わらなかった場合は接続失敗とする
	 * @param name 接続するEV3の名前（IPアドレス）
	 * @param sensorPort 利用するセンサーのポート
	 * @see ConnectThread
	 */
	public void connect_thread(String name, String sensorPort){
		ConnectThread connect = new ConnectThread(name, sensorPort);
		connect.start();
		Timer timer = new Timer();
		timer.start();
		while ( timer.get() < connect.MAX_TIME){
			if ( connect.ev3.isConnected() ) break;
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if ( connect.ev3.isConnected() ){
			this.rev3 = connect.ev3.rev3;
			this.colorSensor = connect.ev3.colorSensor;
			this.motors = connect.ev3.motors;
			this.isConnected = connect.ev3.isConnected;
		}
		else{
			System.out.println("failed to connect to " + name + ", timed out.");
		}
	}
	
	
	/**
	 * EV3が接続されている場合true,そうでない場合falseを返す
	 * @return EV3が接続されている場合：true, そうでない場合：false
	 */
	public boolean isConnected(){
		return isConnected;
	}
	
	/**
	 * move
	 * @param motorName
	 * @param speed
	 */
	public void move(String motorName, float speed){
		if (rev3 == null) return;
		RMIRegulatedMotor motor = motors.get(motorName);
		
		try {
			float maxSpeed = motor.getMaxSpeed();
			motor.setSpeed( (int)(Math.abs(speed) * maxSpeed) );
			if ( speed >= 0 ) motor.forward();
			else motor.backward();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void rotate(String motorName, float speed, int angle){
		if (rev3 == null) return;
		RMIRegulatedMotor motor = motors.get(motorName);
		
		try {
			float maxSpeed = motor.getMaxSpeed();
			motor.setSpeed( (int)(Math.abs(speed) * maxSpeed) );
			
			motor.rotate(angle);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * EV3を走らせる
	 * @param LMotorName	左のモータのポート
	 * @param RMotorName	右のモータのポート
	 * @param speed 		スピード（-1 <= speed <= 1)
	 * @param steering 		曲がる方向（ (left)-1 <= steering <= 1(right) )
	 */
	public void forward(String LMotorName, String RMotorName, float speed, float steering){
		if (rev3 == null) return;
		//System.out.println("forward("+ speed+ ", " + steering + ")");
		RMIRegulatedMotor lmotor = motors.get(LMotorName);
		RMIRegulatedMotor rmotor = motors.get(RMotorName);
		
		
		float left, right;
		// みぎにまがる
		if (steering > 0){
			left = speed;
			right = speed * (1 - steering);
		}
		// ひだりにまがる
		else{
			right = speed;
			left = speed * (1 - Math.abs(steering) );
		}
		
		try {
			float maxSpeed = lmotor.getMaxSpeed();
			lmotor.setSpeed( (int)(Math.abs(left) * maxSpeed) );
			maxSpeed = rmotor.getMaxSpeed();
			rmotor.setSpeed((int)(Math.abs(right) * maxSpeed) );
			
			if ( speed >= 0 ){
				lmotor.forward();
				rmotor.forward();
			}
			else {
				lmotor.backward();
				rmotor.backward();
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *
	 * EV3を走らせる
	 * @param LMotorName	左のモータのポート
	 * @param LMotorSpeed	左のモータのスピード（-1 <= speed <= 1)
	 * @param RMotorName	右のモータのポート
	 * @param RMotorSpeed	右のモータのスピード（-1 <= speed <= 1)
	 */
	public void forward(String LMotorName, float LMotorSpeed, String RMotorName, float RMotorSpeed){
		if (rev3 == null) return;
		//System.out.println("forward(LSpeed: "+ LMotorSpeed+ ", RSpeed: " + RMotorSpeed + ")");
		RMIRegulatedMotor lmotor = motors.get(LMotorName);
		RMIRegulatedMotor rmotor = motors.get(RMotorName);
		
		
		try {
			float maxSpeed = lmotor.getMaxSpeed();
			lmotor.setSpeed( (int)(Math.abs(LMotorSpeed) * maxSpeed) );
			maxSpeed = rmotor.getMaxSpeed();
			rmotor.setSpeed((int)(Math.abs(RMotorSpeed) * maxSpeed) );
			
			if ( LMotorSpeed >= 0 )
				lmotor.forward();
			else 
				lmotor.backward();
			
			if ( RMotorSpeed >= 0 )
				rmotor.forward();
			else
				rmotor.backward();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * シュートする
	 */
	public synchronized void shoot(){
		int angle = 90;
		float speed = (float)1.0;
		this.rotate("B", speed, -angle);
		this.rotate("B", speed, angle);
	}
	
	public void createRegulatedMotors(){
		if (rev3 == null) return;
		
		for (String name: motorNames){
			motors.put(name, 	rev3.createRegulatedMotor(name, 'L') );
		}
	}
	
	public String getSensorValue(){
		return colorSensor != null ? colorSensor.getValue() 	: null;
	}
	
	public void closeSensor(){
		colorSensor.close();
	}
	
	public void stopMotors(){
		
		try{
			for (String name: motorNames){
				RMIRegulatedMotor motor = motors.get(name);
				if (motor != null){
					motor.stop(true);
					motor.close();
				}
			}
			motors.clear();
			System.out.println("stopMotors");
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * EV3への接続処理を行う
	 * @author motoki
	 *
	 */
	private class ConnectThread extends Thread{
		public EV3 ev3;
		private String name;
		private String sensorPort = null;
		
		public final long MAX_TIME = 10000; // 最大10,000ms
		
		public ConnectThread(String name){
			this.name = name;
			this.ev3 = new EV3();
		}
		
		public ConnectThread(String name, String sensorPort){
			this.name= name;
			this.sensorPort = sensorPort;
			this.ev3 = new EV3();
		}
		
		@Override
		public void run(){
			if (this.sensorPort != null)
				this.ev3.connect(name, sensorPort);
			else
				this.ev3.connect(name);
			
		}
	}

}
