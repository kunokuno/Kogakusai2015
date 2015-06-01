////////////////////
///20150501 
///////////////////
package Controller;

import java.text.DecimalFormat;

import javax.swing.JLabel;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.BaseSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.remote.ev3.RemoteEV3;
import lejos.utility.Delay;
//////////////////////////////////////////////
//これで呼んでスレッドを回す（センサーの値を取得し続ける）
//private Sensor s = new Sensors(EV3);
//s.start();
//s.getVale();//値の取得
//////////////////////////////////////////////
public class Sensor extends Thread {
	private boolean update;//アップデートフラグ
	private EV3ColorSensor colorSensor;
	private String sensorValue;
	
	private final long interval = 20; //interval 20[ms]
	
	
	public Sensor(RemoteEV3 ev3, String port) {
		System.out.println("ev3.getPort("+ port + "):" + ev3.getPort(port) );
		colorSensor = new EV3ColorSensor(ev3.getPort(port));
		
		update=true;
		//setDaemon(false);
	}
	
	@Override
	public void run() {
		while(update) {
			update();
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void update() {	
		if (colorSensor != null) {
			SensorMode m = colorSensor.getColorIDMode();
			float[] sample = new float[m.sampleSize()];
			m.fetchSample(sample, 0);
			
			StringBuilder sb = new StringBuilder();
			DecimalFormat df= new DecimalFormat( "#,###,###,##0.00" );
			for(int j=0;j<m.sampleSize();j++) {
				sb.append(df.format(sample[j]));
				if (j<m.sampleSize()-1) sb.append(',');
			}
			
			sensorValue=sb.toString();		
			//テスト用System.out.println("****sb is "+sb.toString()+"****");
			
		}
	}
	public void close() {
		try {
			if (colorSensor != null) {
				colorSensor.close();
				update=false;
			}
		} catch (Exception e) {
			// Ignore errors
		}
	}
	
	public String getValue(){
		return sensorValue;
	}
}

