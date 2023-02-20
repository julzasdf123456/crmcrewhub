package com.lopez.julz.crmcrewhub.api;

public class BaseURL {
    public static String baseUrl() {
//        return "http://192.168.10.161/crm-noneco/public/api/";
        return "http://192.168.2.12/crm-noneco/public/api/";
    }

    public static String baseUrl(String ip) {
        return "http://" + ip + "/crm-boheco1/public/api/";
    }
}
