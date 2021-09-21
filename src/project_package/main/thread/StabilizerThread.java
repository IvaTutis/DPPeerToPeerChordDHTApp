package project_package.main.thread;

import project_package.main.network.PeerNode;
import project_package.main.network.config.Config;
import project_package.main.service.LoggerService;

/**
 * Ovo je stabilizer thread
 *
 * @author Iva
 */
public class StabilizerThread extends Thread{
	
	private PeerNode p;

	//konstruktor
	public StabilizerThread(PeerNode p){
		this.p = p;
	}

	//run thread
	public void run(){
		while(true){
			try {
				Thread.sleep(Config.interval);
			} catch (InterruptedException e) {
				LoggerService.error(StabilizerThread.class, e.getMessage());
			}
		}
	}
}
