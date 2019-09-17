package com.onesait.edge.engine.zigbee.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.InputZdoZFrame;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.frame.ZdoZFrame;

public class RequestMaps {

	private static final long REQUEST_IDS_TIMEOUT_MS = 8000;
	protected static final Map<AfRequest, String> afRequestIds = new ConcurrentHashMap<>();
	protected static final List<ZdoRequest> zdoRequests = new ArrayList<>();
	private static Object zdoRequestsLock = new Object();
	
	public static void addAfRequestId(ZFrame msg, String reqId) {
		if (reqId != null) {
			AfRequest req = new AfRequest(msg);
			afRequestIds.put(req, reqId);
		}
	}
	
	public static String getAfRequestId(ZFrame msg) {
		AfRequest req = new AfRequest(msg);
		return afRequestIds.get(req);
	}

	public static void cleanRequestIds() {
		long actualTimeMs = new Date().getTime();
		Iterator<AfRequest> it = afRequestIds.keySet().iterator();
		while (it.hasNext()) {
			AfRequest request = it.next();
			if ((actualTimeMs - request.getRequestTimeMs()) > REQUEST_IDS_TIMEOUT_MS) {
				afRequestIds.remove(request);
			}
		}
		
		Iterator<ZdoRequest> zit = zdoRequests.iterator();
		while (zit.hasNext()) {
			ZdoRequest request = zit.next();
			if ((actualTimeMs - request.getRequestTimeMs()) > REQUEST_IDS_TIMEOUT_MS) {
				zdoRequests.remove(request);
			}
		}
	}

	public static String getZdoRequestId(ZdoZFrame frame) {
		ZdoRequest zdoreq = new ZdoRequest(frame, "");
		int idx = zdoRequests.indexOf(zdoreq);
		if (idx == -1) {
			return "";
		}
		return zdoRequests.get(idx).getRequestId();
	}
	
	public static void removeZdoRequestId(ZdoZFrame frame) {
		ZdoRequest zdoreq = new ZdoRequest(frame, "");
		int idx = zdoRequests.indexOf(zdoreq);
		if (idx == -1) {
			return;
		}
		zdoRequests.remove(idx);
	}

	public static void removeAfRequestId(ZFrame frame) {
		AfRequest req = new AfRequest(frame);
		afRequestIds.remove(req);
	}
	
	public static boolean areAllZdoTemporarilyFailed(InputZdoZFrame frame) {
		synchronized (zdoRequestsLock) {
			ZdoRequest newZdoRequest = new ZdoRequest(frame, "");
			for (ZdoRequest zdoRequest : zdoRequests) {
				if (zdoRequest.equals(newZdoRequest)) {
					if (!zdoRequest.isTemporarilyFailed()) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public static void markAsTemporarilyFailed(InputZdoZFrame frame) {
		synchronized (zdoRequestsLock) {
			ZdoRequest newZdoRequest = new ZdoRequest(frame, "");
			for (ZdoRequest zdoRequest : zdoRequests) {
				if (zdoRequest.equals(newZdoRequest) && !zdoRequest.isTemporarilyFailed()) {
					zdoRequest.setTemporarilyFailed();
					return;
				}
			}
		}
	}
	public static AfRequest getAfRequest(AfIncomingMsg af) {
		AfRequest req = new AfRequest(af);
		for (AfRequest afreq : afRequestIds.keySet()) {
			if (afreq.equals(req)) {
				return afreq;
			}
		}
		return null;
	}
	
	private RequestMaps() {}

	public static Map<AfRequest, String> getAfrequestids() {
		return afRequestIds;
	}

	public static List<ZdoRequest> getZdorequests() {
		return zdoRequests;
	}	
}
