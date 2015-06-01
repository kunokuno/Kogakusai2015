package Kogakusai2015.EV3;
/**
 * シュートのリスナー
 * @author motoki
 *
 */
public interface ShootingListener {
	
	/**
	 * シュートの動作が終了したとき
	 */
	public void shooted();
	
	/**
	 * シュートの動作を開始したとき
	 */
	public void shoot_start();

}
