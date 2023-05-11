package com.vivy.shortener.util;

public class UrlUtil {

    public static String createBaseUrl(String host, int port, boolean isSecure) {
        StringBuilder builder = new StringBuilder();
        if (isSecure) {
            builder.append("https://").append(host);
            if (port != 443) {
                //not default port
                builder.append(":").append(port);
            }
        } else {
            builder.append("http://").append(host);
            if (port != 80) {
                //not default port
                builder.append(":").append(port);
            }
        }
        return builder.toString();
    }
}
