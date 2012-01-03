package com.TileRummy.Service;

/**
 * Created by IntelliJ IDEA.
 * User: Dested
 * Date: 1/1/12
 * Time: 2:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameInformation {
    public static String UserName;
    public static String Service = "gameservice";
    public static String HostName = "lamplightonline.com";
    public static String IP = "lamplightonline.com";

    public static String getXMPPInfo() {
        return Service + "." + HostName;
    }
}
