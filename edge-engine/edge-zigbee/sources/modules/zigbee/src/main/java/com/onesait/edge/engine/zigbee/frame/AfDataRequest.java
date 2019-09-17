package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZClusterLibrary;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;


public class AfDataRequest extends ZFrame implements Acknowledgeable {

        private DoubleByte clusterID;
        private int destEndpoint;
        private DoubleByte nwkAddr;
        private int len;
        private int options;
        private int radius;
        private int srcEndpoint;
        private int transID;
        public static final int ZCL_OFFSET= 10;
        
		public AfDataRequest(DoubleByte nwkDstAddress, int dstEndPoint,
				int srcEndPoint, DoubleByte clusterId, byte transId, int bitmapOpt, int radius,
				int[] msg) {
			
			this.setParameters(nwkDstAddress, dstEndPoint, srcEndPoint, clusterId, transId, bitmapOpt, radius, msg.length);
			int[] framedata = this.buildFrameData();
            for (int i = 0; i < msg.length; i++) {
                framedata[10 + i] = msg[i];
            }
            super.buildPacket(new DoubleByte(ZToolCMD.AF_DATA_REQUEST), framedata);
		}
		
		public AfDataRequest(DoubleByte nwkDstAddress, int dstEndPoint,
				int srcEndPoint, DoubleByte clusterId, byte transId, int bitmapOpt, int radius,
				Integer[] msg) {
			
			this.setParameters(nwkDstAddress, dstEndPoint, srcEndPoint, clusterId, transId, bitmapOpt, radius, msg.length);
			int[] framedata = this.buildFrameData();
			for (int i = 0; i < msg.length; i++) {
				framedata[10 + i] = msg[i];
			}
			super.buildPacket(new DoubleByte(ZToolCMD.AF_DATA_REQUEST), framedata);
		}
		
		public AfDataRequest(DoubleByte nwkDstAddress, int dstEndPoint,
				int srcEndPoint, DoubleByte clusterId, byte transId, int bitmapOpt, int radius,
				byte[] msg) {
			
			this.setParameters(nwkDstAddress, dstEndPoint, srcEndPoint, clusterId, transId, bitmapOpt, radius, msg.length);
            int[] framedata = this.buildFrameData();
            
            for (int i = 0; i < msg.length; i++) {
                framedata[10 + i] = msg[i];
            }
            super.buildPacket(new DoubleByte(ZToolCMD.AF_DATA_REQUEST), framedata);
		}
		
		private void setParameters(DoubleByte nwkDstAddress, int dstEndPoint,
				int srcEndPoint, DoubleByte clusterId, byte transId, int bitmapOpt, int radius, int msgLength) {
			
			if(msgLength > 128){
				throw new IllegalArgumentException("Payload is too big, maxium is 128");
			}
			if(dstEndPoint > 255 ||srcEndPoint> 255 || transId> 255 ||bitmapOpt> 255 ||radius> 255) {
				throw new IllegalArgumentException("parameters not valid: dstEndPoint > 255 " +
						"||srcEndPoint> 255  ||transId> 255 ||bitmapOpt> 255 ||radiu0s> 255");
			}
			
			this.clusterID = clusterId;
			this.nwkAddr = nwkDstAddress;
			this.destEndpoint = dstEndPoint;
			this.srcEndpoint = srcEndPoint;
			this.transID = transId;
			this.radius = radius;
			this.len = msgLength;
			this.options = bitmapOpt;
		}
		
		private int[] buildFrameData() {
			
            int[] framedata=new int[this.len + 10];
            framedata[0] = this.nwkAddr.getLsb();            
            framedata[1] = this.nwkAddr.getMsb();
            framedata[2] = this.destEndpoint & 0xFF;
            framedata[3] = this.srcEndpoint & 0xFF;
            framedata[4] = this.clusterID.getLsb();
            framedata[5] = this.clusterID.getMsb();
            framedata[6] = this.transID & 0xFF;
            framedata[7] = this.options & 0xFF;
            framedata[8] = this.radius & 0xFF;
            framedata[9] = this.len;
            
            return framedata;
		}
		
		public int getZclCmd() {
			int cmd;
			if (this.isManufacturerSpecific())
				cmd = super.Data[14];
			else
				cmd = super.Data[12];
			return cmd;
		}
		
		public boolean isManufacturerSpecific() {
			return ((super.Data[ZCL_OFFSET] & ZClusterLibrary.ZCL_FRAME_CONTROL_MANU_SPECIFIC) > 0);
		}
		
		public int getZclPayloadOffset() {
			if (this.isManufacturerSpecific()) {
				return 5;
			} else {
				return 3;
			}
		}
		
	    public Integer getSequenceNumber() {
	    	Integer seq = null;
	    	if (this.isManufacturerSpecific())
	    		seq = super.Data[13];
	    	else
	    		seq = super.Data[11];
	    	return seq;
	    }
	    
	    public boolean isClusterSpecific() {
	    	return ((super.Data[ZCL_OFFSET] & ZClusterLibrary.ZCL_FRAME_CONTROL_TYPE) > 0);
	    }
	    public DoubleByte getClusterID() {
			return clusterID;
		}

		public void setClusterID(DoubleByte clusterID) {
			this.clusterID = clusterID;
		}

		public int getDestEndpoint() {
			return destEndpoint;
		}

		public void setDestEndpoint(int destEndpoint) {
			this.destEndpoint = destEndpoint;
		}

		public DoubleByte getNwkAddr() {
			return nwkAddr;
		}

		public void setNwkAddr(DoubleByte nwkAddr) {
			this.nwkAddr = nwkAddr;
		}

		public int getLen() {
			return len;
		}

		public void setLen(int len) {
			this.len = len;
		}

		public int getOptions() {
			return options;
		}

		public void setOptions(int options) {
			this.options = options;
		}

		public int getRadius() {
			return radius;
		}

		public void setRadius(int radius) {
			this.radius = radius;
		}

		public int getSrcEndpoint() {
			return srcEndpoint;
		}

		public void setSrcEndpoint(int srcEndpoint) {
			this.srcEndpoint = srcEndpoint;
		}

		public int getTransID() {
			return transID;
		}

		public void setTransID(int transID) {
			this.transID = transID;
		}

}
