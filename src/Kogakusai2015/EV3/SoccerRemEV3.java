package Kogakusai2015.EV3;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import Controller.EV3;
import lejos.remote.ev3.RemoteEV3;

public class SoccerRemEV3 extends RemoteEV3{

	public SoccerRemEV3(String host) throws RemoteException,
			MalformedURLException, NotBoundException {
		super(host);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * EV3への接続処理を行う
	 * @author motoki
	 *
	 */
	private class ConnectThread extends Thread{
		public SoccerRemEV3 ev3;
		private String name;
		private String sensorPort = null;
		
		public final long MAX_TIME = 10000; // 最大10,000ms
		
		public ConnectThread(String name){
			this.name = name;
		}
		
		public ConnectThread(String name, String sensorPort){
			this.name= name;
			this.sensorPort = sensorPort;
		}
		
		@Override
		public void run(){
			/*
			if (this.sensorPort != null)
				//this.ev3.connect(name, sensorPort);
			else
				this.ev3.connect(name);
			*/
		}
	}

	
}
