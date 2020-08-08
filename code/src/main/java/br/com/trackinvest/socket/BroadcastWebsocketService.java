package br.com.trackinvest.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BroadcastWebsocketService {

	@Autowired
	SimpMessagingTemplate messagingTemplate;

	public void broadcastProgressUpdate(String destination, Object payload)
	{
		messagingTemplate.convertAndSend(destination, payload);
	}
}
