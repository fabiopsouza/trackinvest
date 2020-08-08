package br.com.trackinvest.socket;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class ProgressMonitor {

	@Autowired
	private BroadcastWebsocketService socketService;
	
	protected void updateProgress(String endpoint) {
		
		socketService.broadcastProgressUpdate(endpoint, 100);
	}
	
	protected void updateProgress(String endpoint, int element, int total) {
		
		double progress = (double) element / total * 100;
		socketService.broadcastProgressUpdate(endpoint, (int) progress);
	}
}
