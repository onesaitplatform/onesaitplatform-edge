package com.onesait.edge.engine.zigbee.ota;

import com.onesait.edge.engine.zigbee.util.FourByte;

public class OtaManager {

	
	private boolean isOta;
	//private HashMap<OctaByte, FourByte> versions2upgrade= new HashMap<OctaByte, FourByte>();
	//version que tiene el dispositivo antes de actualizarse
	private FourByte currentVersion; 
	//version que tiene el dispositivo descargada. Se suele cheakear esta version despues de haber concluido 
	//envio de todo el fichero OTA
	private FourByte downloadversion;  
	private FourByte version2upgrade;
	private FourByte newImageSize;
	private String filePath;
	private boolean isInstaling;
	private boolean isOtaRequest;
	private int numberOfAttempts;
	private long porcentaje=0;
	private boolean abort=false;
	private boolean infoRequested=false;
	
	public OtaManager(){
		this.isOta=false;
		this.isInstaling=false;
		this.isOtaRequest=false;	
		this.numberOfAttempts=0;
	}
	
	public boolean isInstalling() {
		return isInstaling;
	}

	public void setInstalling(boolean isInstalling) {
		this.isInstaling = isInstalling;
	}

	public boolean isOta() {
		return isOta;
	}

	public void setOta(boolean isOta) {
		this.isOta = isOta;
	}

	public FourByte getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(FourByte currentVersion) {
		this.currentVersion = currentVersion;
	}

	public FourByte getDownloadversion() {
		return downloadversion;
	}

	public void setDownloadversion(FourByte downloadversion) {
		this.downloadversion = downloadversion;
	}

	public FourByte getVersion2upgrade() {
		return version2upgrade;
	}

	public void setVersion2upgrade(FourByte version2upgrade) {
		this.version2upgrade = version2upgrade;
	}

	public FourByte getNewImageSize() {
		return newImageSize;
	}


	public void setNewImageSize(FourByte newImageSize) {
		this.newImageSize = newImageSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getNumberOfAttempts() {
		return numberOfAttempts;
	}
	
	public void setNumberOfAttempts(int numberOfAttempts) {
		this.numberOfAttempts = numberOfAttempts;
	}

	public boolean isOtaRequest() {
		return isOtaRequest;
	}

	public void setOtaRequest(boolean isOtaRequest) {
		this.isOtaRequest = isOtaRequest;
	}

	public long getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(long porcentaje) {
		this.porcentaje = porcentaje;
	}

	public boolean isAbort() {
		return abort;
	}

	public void setAbort(boolean abort) {
		this.abort = abort;
	}

	public boolean isInfoRequested() {
		return infoRequested;
	}

	public void setInfoRequested(boolean infoRequested) {
		this.infoRequested = infoRequested;
	}
	
	

	
}
