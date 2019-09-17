package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

public class ZbGetDeviceInfoRsp extends ZFrame {
	
	private int itemId;
	private int [] itemValue;
	protected static final String [] ITEM_IDS = {
			"STATE",
			"IEEE_ADDR",
			"SHORT_ADDR",
			"PARENT_SHORT_ADDR",
			"PARENT_IEEE_ADDR",
			"CHANNEL",
			"PAN_ID",
			"EXT_PAN_ID"
	};
	
	public ZbGetDeviceInfoRsp(int[] frameData) {
		this.itemId = frameData[0];
		if (frameData.length > 8)
			this.itemValue = new int[frameData.length - 8];
		else
			this.itemValue = new int[1];
		for (int i = 0; i < itemValue.length; i++) {
			this.itemValue[i] = frameData[i + 1];
		}
		this.buildPacket(new DoubleByte(ZToolCMD.ZB_GET_DEVICE_INFO_RSP), frameData);
	}

	@Override
	public String toString() {
		return "ZB_GET_DEVICE_INFO_RSP [itemId=" + itemId + ", itemValue=" + itemValue + "]";
	}

	
	public static String[] getItemIds() {
		return ITEM_IDS;
	}

	public int[] getItemValue() {
		return itemValue;
	}

	public int getItemId() {
		return itemId;
	}
}
