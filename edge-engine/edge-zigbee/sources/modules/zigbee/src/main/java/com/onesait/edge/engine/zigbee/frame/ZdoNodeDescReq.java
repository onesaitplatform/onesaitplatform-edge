package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

/*
Copyright 2008-2013 CNR-ISTI, http://isti.cnr.it
Institute of Information Science and Technologies 
of the Italian National Research Council 
Copyright 2008-2013 ITACA-TSB, http://www.tsb.upv.es/
Instituto Tecnologico de Aplicaciones de Comunicacion 
Avanzadas - Grupo Tecnologias para la Salud y el 
Bienestar (TSB)
See the NOTICE file distributed with this work for additional 
information regarding copyright ownership
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
 * Requests the Node Descriptor.
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano "Kismet" Lenzi</a>
 * @author <a href="mailto:alfiva@aaa.upv.es">Alvaro Fides Valero</a>
 * @version $LastChangedRevision: 799 $ ($LastChangedDate: 2013-08-06 19:00:05
 *          +0300 (Tue, 06 Aug 2013) $)
 * @since 0.1.0
 */
public class ZdoNodeDescReq extends ZFrame implements ZdoZFrame {

	private DoubleByte dstAddr;
	private DoubleByte nwkAddrOfInterest;

	public ZdoNodeDescReq() {
	}

	public ZdoNodeDescReq(DoubleByte destination) {
		int[] framedata = new int[4];
		framedata[0] = destination.getLsb();
		framedata[1] = destination.getMsb();
		framedata[2] = framedata[0];
		framedata[3] = framedata[1];
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_NODE_DESC_REQ), framedata);
	}

	public ZdoNodeDescReq(DoubleByte num1, DoubleByte num2) {
		this.dstAddr = num1;
		this.nwkAddrOfInterest = num2;

		int[] framedata = new int[4];
		framedata[0] = this.dstAddr.getLsb();
		framedata[1] = this.dstAddr.getMsb();
		framedata[2] = this.nwkAddrOfInterest.getLsb();
		framedata[3] = this.nwkAddrOfInterest.getMsb();
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_NODE_DESC_REQ), framedata);
	}

	public DoubleByte getDstAddr() {
		return dstAddr;
	}

	public void setDstAddr(DoubleByte dstAddr) {
		this.dstAddr = dstAddr;
	}

	public DoubleByte getNwkAddrOfInterest() {
		return nwkAddrOfInterest;
	}

	public void setNwkAddrOfInterest(DoubleByte nwkAddrOfInterest) {
		this.nwkAddrOfInterest = nwkAddrOfInterest;
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return dstAddr;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.NodeDescriptorRequest;
	}

}