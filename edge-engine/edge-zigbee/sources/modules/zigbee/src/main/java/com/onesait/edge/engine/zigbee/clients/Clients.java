package com.onesait.edge.engine.zigbee.clients;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.service.MqttConnection;
import com.onesait.edge.engine.zigbee.util.DoubleByte;

public class Clients {

	private ConcurrentHashMap<DoubleByte, ClientCluster> clientsList = new ConcurrentHashMap<>();
	

	public ZFrame[] init(ZclDevice dev) {
		ArrayList<ZFrame> framesList = new ArrayList<>();
		for (ClientCluster client : this.clientsList.values()) {
			ZFrame[] clientInitFrames = client.init(dev);
			for (int i = 0; i < clientInitFrames.length; i++) {
				framesList.add(clientInitFrames[i]);
			}
		}
		ZFrame[] framesToSend = new ZFrame[framesList.size()];
		return framesList.toArray(framesToSend);
	}

	public ZFrame[] init(DoubleByte clusterId, ZclDevice dev) {
		ClientCluster client = this.clientsList.get(clusterId);
		ZFrame[] framesToSend = new ZFrame[0];
		if (client != null) {
			framesToSend = client.init(dev);
		}
		return framesToSend;
	}

	public void addClient(ClientCluster client) {
		this.clientsList.put(client.getClusterId(), client);
	}
	
	public void addClients(ClientCluster... clients) {
		for (int i = 0; i < clients.length; i++) {
			this.addClient(clients[i]);
		}
	}

	public ZFrame[] manageFrame(AfIncomingMsg afIncoming, ZclDevice dev, MqttConnection mqttConnection) {
		ZFrame[] framesToSend = new ZFrame[0];
		if (afIncoming == null) {
			return framesToSend;
		}
		ClientCluster client = this.clientsList.get(afIncoming.getClusterID());
		if (client != null) {
			framesToSend = client.manageFrame(afIncoming, dev,mqttConnection);
		}
		return framesToSend;
	}
	
	public ConcurrentMap<DoubleByte, ClientCluster> getClients() {
		return clientsList;
	}
	
	public void deleteClient(int clusterId) {
		this.clientsList.remove(new DoubleByte(clusterId));
	}
}
