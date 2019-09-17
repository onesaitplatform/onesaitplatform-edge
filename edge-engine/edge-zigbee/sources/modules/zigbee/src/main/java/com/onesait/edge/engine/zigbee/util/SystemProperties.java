package com.onesait.edge.engine.zigbee.util;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author ahumanes zigbee
 *
 */
public class SystemProperties {

//    private String hostName;
//    private String ipAddress;
    private String osName;
    private String osType;
    private String osVersion;

//    public SystemProperties() throws UnknownHostException{
    public SystemProperties(){

//        InetAddress ip;
        
        this.osName = SystemUtils.getOsName().toLowerCase();
//        this.osType = System.getOsArch().toLowerCase();
        this.osVersion = SystemUtils.getOsVersion();
        
        if (this.osName.toLowerCase().startsWith("windows")) {
            this.osName = "win64";
        }
            
//            this.hostName = ip.getHostName();
//            this.ipAddress = ip.getHostAddress();
    }

    public boolean isWindows() {
        return (this.osName.toLowerCase().startsWith("win"));
    }
    

    /**
     * @return the osName
     */
    public String getOsName() {
        return osName;
    }

    /**
     * @return the osType
     */
    public String getOsType() {
        return osType;
    }

    /**
     * @return the osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    public static String getExtIpAddress() {
        try {
            URL connection = new URL("http://checkip.amazonaws.com/");
            URLConnection con = connection.openConnection();
            String ip = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            ip = reader.readLine();
            return ip;
        } catch (Exception e) {
            return "NO EXTERNAL IP DETECTED";
        }
    }


}
