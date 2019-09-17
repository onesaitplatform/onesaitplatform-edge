package com.onesait.edge.engine.zigbee.io;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onesait.edge.engine.zigbee.frame.AfDataRequest;
import com.onesait.edge.engine.zigbee.frame.AfIncomingMsg;
import com.onesait.edge.engine.zigbee.frame.Acknowledgeable;
import com.onesait.edge.engine.zigbee.frame.ZdoIeeeAddrReq;
import com.onesait.edge.engine.zigbee.frame.ZdoIeeeAddrRsp;
import com.onesait.edge.engine.zigbee.frame.ZFrame;
import com.onesait.edge.engine.zigbee.frame.ZdoZFrame;
import com.onesait.edge.engine.zigbee.model.ZclAttribute;
import com.onesait.edge.engine.zigbee.model.ZclCluster;
import com.onesait.edge.engine.zigbee.model.ZclDevice;
import com.onesait.edge.engine.zigbee.service.DeviceManager;
import com.onesait.edge.engine.zigbee.util.AfRequest;
import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.RequestMaps;
import com.onesait.edge.engine.zigbee.util.ResponseChecker;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZdoRequest;
import com.onesait.edge.engine.zigbee.util.ZigbeeConstants;

public class OutputSerialZigbee{

	private static final Logger LOG = LoggerFactory.getLogger(OutputSerialZigbee.class);
	private static final float IEEE_ADDR_REQ_TIMEOUT_SEC = 10;
	
	private OutputStream out = null;
	private HashMap<AfRequest, AfDataRequest> afRequestFrames = new HashMap<>();
	private HashMap<DoubleByte, Timestamp> ieeeAddrReqSent = new HashMap<>();
	private Long bytesSent = 0l;
	private final Object bytesSentLock = new Object();
	private DeviceManager deviceManager;
		
	
	public OutputSerialZigbee(OutputStream out,DeviceManager deviceManager) {
		this.out = out;
		this.deviceManager=deviceManager;
	}

	public synchronized void setOut(OutputStream out) {
		if (out != null) {
			this.out = out;
		}
	}
	
	public synchronized void writePort(byte[] b) throws IOException {
		if (this.out != null) {
			synchronized (bytesSentLock) {
				bytesSent += b.length;
			}
			this.out.write(b);
		} else {
			throw new IOException("Zigbee serial port output null");
		}
	}

	public void writeZFrames(ZFrame[] frames) {
		if (frames != null) {
			for (int j = 0; j < frames.length; j++) {
				this.writeZFrame(frames[j]);
			}
		}
	}

	public synchronized void writeZFrame(ZFrame frame) {
		this.writeZFrame(frame, null);
	}
	
	public synchronized void writeZFrame(ZFrame frame, String requestId) {
		if (frame != null) {
			scheduleRetries(frame);
			saveRequestId(frame, requestId);
			try {
				this.writePort(frame.getBytePacket());
				if(LOG.isDebugEnabled()) {
				LOG.debug("[WRITEPORT ZFRAME: {} ]",serializer(frame.getBytePacket()));
				}
			} catch (Exception e) {
					LOG.error("ERROR: writeZFrame(): {}",e.getCause());
				}
			}
		}
	
	
	private void saveRequestId(ZFrame zf, String requestId) {
		if (!(requestId != null && !"".equals(requestId))) {
			return;
		}
		if (zf instanceof AfDataRequest) {
			AfDataRequest af = (AfDataRequest) zf;
			AfRequest req = new AfRequest(af);
			RequestMaps.getAfrequestids().put(req, requestId);
		} else if (zf instanceof ZdoZFrame && zf instanceof Acknowledgeable) {
			ZdoZFrame zdoreqzf = (ZdoZFrame)zf;
			ZdoRequest zdoreq = new ZdoRequest(zdoreqzf.getZdoNwkAddr(),
					zdoreqzf.getZdoCmdId(), requestId);
			RequestMaps.getZdorequests().add(zdoreq);
		}
	}

	private void scheduleRetries(ZFrame zf) {
		if (zf instanceof AfDataRequest) {
			AfDataRequest af = (AfDataRequest) zf;
			if (af.getNwkAddr().equals(new DoubleByte(0)) ||
					af.isClusterSpecific() ||
					af.getZclCmd() == ZClusterLibrary.ZCL_CMD_DEFAULT_RSP) {
				return; // Don't need to retry this message
			}
			synchronized (this.afRequestFrames) {
				if (!this.isRetrying(af)) {
					AfRequest req = new AfRequest(af);
					ResponseChecker rc = new ResponseChecker(this, af, req, this.afRequestFrames);
					this.afRequestFrames.put(req, af);
					
					rc.start();
				}
			}
		}
	}
	
	private boolean isRetrying(ZFrame zf) {
		AfRequest req = new AfRequest(zf);
		synchronized (this.afRequestFrames) {
			return this.afRequestFrames.get(req) != null;
		}
	}
	
	public boolean sendAndWriteLog(ZFrame frame, String message, String className) {
		return sendAndWriteLog("", frame, message, className);
	}
	
	public boolean sendAndWriteLog(String requestId, ZFrame frame, String message, String className) {
		boolean success;
		Logger auxLog = LoggerFactory.getLogger(className);
		if (this.out == null) {
			if (message != null && !message.equals("")) {
				auxLog.error("####CANNOT SEND MESSAGE####: {}",message);
			}
			success = false;
		} else {
			this.writeZFrame(frame, requestId);
			if (message != null && !message.equals("")) {
				auxLog.info(message);
			}
			success = true;
		}
		return success;
	}

	public boolean sendAndWriteLog(ZFrame[] frames, String message, String className) {
		boolean success;
		Logger auxLog = LoggerFactory.getLogger(className);
		if (this.out == null) {
			if (message != null && !message.equals("")) {
				auxLog.error("####CANNOT SEND MESSAGE####: {}",message);
			}
			success = false;
		} else {
			this.writeZFrames(frames);
			if (message != null && !message.equals("")) {
				auxLog.info(message);
			}
			success = true;
		}
		return success;
	}
	
	public static String serializer(byte[] data) {
		
		  StringBuilder bld = new StringBuilder();
		  for (int i = 0; i < data.length; i++) {
				bld.append(String.format("%02X", data[i]).toUpperCase());
				if (i < data.length - 1) {
					bld.append(" ");
				}
			}
		  return bld.toString();
	}

	public static String serializer(int[] data) {
		  StringBuilder bld = new StringBuilder();
		  for (int i = 0; i < data.length; i++) {
				bld.append(String.format("%02X", data[i]).toUpperCase());
				if (i < data.length - 1) {
					bld.append(" ");
				}
			}
		  return bld.toString();
	}
	
	public void stopRetries(AfIncomingMsg afInc) {
		AfRequest req = new AfRequest(afInc);
		
		AfDataRequest afReq = this.afRequestFrames.get(req);
		if (afReq != null) {
			int incCmd = afInc.getZclCmd();
			if (req.getCmdId() == ZClusterLibrary.ZCL_CMD_CONFIG_REPORT &&
					req.isProfileWide()) {
					DoubleByte attId = new DoubleByte(
							afReq.getData()[AfDataRequest.ZCL_OFFSET + afReq.getZclPayloadOffset() + 2],
							afReq.getData()[AfDataRequest.ZCL_OFFSET + afReq.getZclPayloadOffset() + 1]);
					ZclDevice dev = deviceManager.devices.get(afReq.getNwkAddr());
					ZclAttribute att = dev.getZclCluster(afReq.getClusterID()).getAttribute(attId);
				if (incCmd == ZClusterLibrary.ZCL_CMD_DEFAULT_RSP &&
						!afInc.isClusterSpecific()) {
					if (att != null) {
						synchronized (att.getAttributeLock()) {
							att.setUnsupported(true);
							ZclCluster.markConfiguredAttribute(att, afInc.getClusterID(), dev,afInc.getSrcEndpoint());
						}
					}
					synchronized (afRequestFrames) {
						this.afRequestFrames.remove(req);
					}
				} else if (incCmd == ZClusterLibrary.ZCL_CMD_CONFIG_REPORT_RSP &&
						!afInc.isClusterSpecific()) {
					if (att != null) {
						synchronized (att.getAttributeLock()) {
							ZclCluster.markConfiguredAttribute(att, afInc.getClusterID(), dev,afInc.getSrcEndpoint());
						}
					}
				}
			}
			if (afReq.getZclCmd() == req.getCmdId()) {
				synchronized (afRequestFrames) {
					this.afRequestFrames.remove(req);
				}
			}
		}
	}
	
	public boolean isExpectedIeeeAddressResponse(ZdoIeeeAddrRsp zf) {
		DoubleByte srcAddr = zf.getNwkAddr();
		Timestamp t = this.ieeeAddrReqSent.remove(srcAddr);
		return (t != null &&
				(new Date().getTime() - t.getTime() < IEEE_ADDR_REQ_TIMEOUT_SEC * 1000));
	}
	
	public void manageZclUnknownCoordinatorMacException() {
		this.ieeeAddrReqSent.put(ZigbeeConstants.COORDINATOR_SHORT_ADDRESS, new Timestamp(new Date().getTime()));
		this.sendAndWriteLog(new ZdoIeeeAddrReq(new DoubleByte(0), false),
				"Coordinator IEEE address unknown. Sending IEEE Address request.", this.getClass().getName());
	}
	
	public long getAndResetBytesSent() {
		long bytesSent;
		synchronized (this.bytesSentLock) {
			bytesSent = this.bytesSent.longValue();
			this.bytesSent = 0l;
		}
		return bytesSent;
	}

	public HashMap<DoubleByte, Timestamp> getIeeeAddrReqSent() {
		return ieeeAddrReqSent;
	}

	public void setIeeeAddrReqSent(HashMap<DoubleByte, Timestamp> ieeeAddrReqSent) {
		this.ieeeAddrReqSent = ieeeAddrReqSent;
	}
	
	
}