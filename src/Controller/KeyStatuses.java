package Controller;

import java.util.HashMap;

/**
 * キー
 * @author motoki
 *
 */
public class KeyStatuses{
	private HashMap<Integer, KeyStatus> key = new HashMap<Integer, KeyStatus> ();
	
	public void pushed(int keyCode){
		
		if ( key.get(keyCode) == null){
			key.put(keyCode, new KeyStatus());
			key.get(keyCode).pushed();
		}
		else{
			key.get(keyCode).pushed();
		}
	}
	
	public void released(int keyCode){
		if ( key.get(keyCode) == null){
			key.put(keyCode, new KeyStatus());
			key.get(keyCode).released();
		}
		else{
			key.get(keyCode).released();
		}
	}
	
	public boolean pushed (int keyCode, double keyValue){
		if (key.get(keyCode) == null){
			key.put(keyCode, new KeyStatus());
			return key.get(keyCode).J_pushed(keyValue);
		}
		else{
			return key.get(keyCode).J_pushed(keyValue);
		}
	}
	
	public boolean isPushed(int keyCode){
		if (key.get(keyCode) == null)
			return false;
		
		else
			return key.get(keyCode).isPushed();
	}
	
	public double getValue(int keyCode){
		if (key.get(keyCode) == null)
			return 0.0;
		else
			return key.get(keyCode).getValue();
	}
	
	public long getTime(int keyCode){
		if (key.get(keyCode) == null)
			return 0;
		else 
			return key.get(keyCode).getTime();
	}
	
	

	class KeyStatus {
		private boolean ispushed = false;
		private long time; 	// time [ms]
		private double keyvalue = 0.0;
		private final float threshold = (float)0.3;
	
		public KeyStatus(){
			time = System.currentTimeMillis();
		}
		
		public void pushed(){
			ispushed = true;
			time = System.currentTimeMillis();
		}
		
		public void released(){
			ispushed = false;
			time = System.currentTimeMillis();
		}
		
		public boolean J_pushed(double value){
			if(Math.abs(value) < threshold){
				ispushed = false;
				
				if ( Math.abs(this.keyvalue) >= threshold ){
					this.setTime();
					this.keyvalue = (double)0;
					return true;
				}
				return false;
			}
			else{
				ispushed = true;
				if ( Math.abs(this.keyvalue - value) > 0.02 ){
					this.setTime();
					this.keyvalue = value;
					
					return true;
				}
				else
					return false;
			}
		}
	
		public boolean isPushed(){
			return ispushed;
		}
		
		public double getValue(){
			return this.keyvalue;
		}
	
		public long getTime(){
			return System.currentTimeMillis() - time;
		}
		
		private void setTime(){
			this.time = System.currentTimeMillis();
		}
	}
}

