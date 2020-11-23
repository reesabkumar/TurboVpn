package com.rvtech.turbovpn.vpn;

public class Config {
    //hydra SKD keys
    public static final String carrierID = "rv_vpnmaster";
    public static final String baseURL = "https://d2isj403unfbyl.cloudfront.net";


    //Remove Ads subscription id
    public static final String remove_ads_one_month = "bs.remove_ads.one_month";
    public static final String remove_ads_three_month = "bs.remove_ads.three_month";
    public static final String remove_ads_six_month = "bs.remove_ads.six_month";
    public static final String remove_ads_one_year = "bs.remove_ads.one_year";

    //Unlock VIP servers subscription id
    public static final String unlock_servers_one_month = "bs.unlock.server_one_month";
    public static final String unlock_servers_three_month = "bs.unlock.server_three_month";
    public static final String unlock_servers_six_month = "bs.unlock.server_six_month";
    public static final String unlock_servers_one_year = "bs.unlock.server_one_year";

    /*settings parameters (don't change them these are auto controlled by application flow)*/
    public static boolean ads_subscription = false;
    public static boolean servers_subscription = false;
}