package com.onesait.edge.engine.zigbee.frame;

import com.onesait.edge.engine.zigbee.util.DoubleByte;
import com.onesait.edge.engine.zigbee.util.ZDOCommand;
import com.onesait.edge.engine.zigbee.util.ZToolCMD;

/*
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
 * @author <a href="mailto:alfiva@aaa.upv.es">Alvaro Fides Valero</a>
 * @version $LastChangedRevision: 799 $ ($LastChangedDate: 2013-08-06 19:00:05
 *          +0300 (Tue, 06 Aug 2013) $)
 */
public class ZdoMgmtPermitJoinRsp extends ZFrame implements InputZdoZFrame {

	private DoubleByte srcAddress;
	private int status;

	public ZdoMgmtPermitJoinRsp() {
	}

	public ZdoMgmtPermitJoinRsp(int[] framedata) {
		this.srcAddress = new DoubleByte(framedata[1], framedata[0]);
		this.status = framedata[2];
		super.buildPacket(new DoubleByte(ZToolCMD.ZDO_MGMT_PERMIT_JOIN_RSP), framedata);
	}

	@Override
	public String toString() {
		return "ZDO_MGMT_PERMIT_JOIN_RSP{" + "SrcAddress=" + srcAddress + ", Status=" + status + '}';
	}

	public DoubleByte getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(DoubleByte srcAddress) {
		this.srcAddress = srcAddress;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public DoubleByte getZdoNwkAddr() {
		return this.srcAddress;
	}

	@Override
	public DoubleByte getZdoCmdId() {
		return ZDOCommand.ManagementPermitJoinResponse;
	}

	@Override
	public int getStatus() {
		return this.status;
	}
}