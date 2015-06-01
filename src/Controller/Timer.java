package Controller;

public class Timer {
	private long start = 0;
	private long stop = 0;
	
	public Timer(){
		this.start();
	}

	public void start(){
		start = System.currentTimeMillis();	
	}
	
	public long stop(){
		stop = System.currentTimeMillis();
		return stop - start;
	}
	
	public void set(long time){
		this.start = time + System.currentTimeMillis();
	}
	
	public long get(){
		return System.currentTimeMillis() - start;
	}
}