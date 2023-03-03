package com.chatbot.service.impl;

import com.chatbot.service.TrustManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class DefaultTrustManagerServiceImpl implements TrustManagerService {
    private static DefaultTrustManagerServiceImpl instance;

    private final Logger LOG = LoggerFactory.getLogger(DefaultTrustManagerServiceImpl.class);

    private DefaultTrustManagerServiceImpl() {
    }

    public static synchronized DefaultTrustManagerServiceImpl getInstance() {
        if (instance == null) {
            instance = new DefaultTrustManagerServiceImpl();
        }
        return instance;
    }

    @Override
    public void trustAllCertificates() throws Exception {
        // configure the SSLContext with a dummy TrustManager
        final SSLContext ctx = SSLContext.getInstance("SSL");
        ctx.init(null, getTrustManager(), new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        final HostnameVerifier hostnameVerifier = (urlHostName, session) -> {
            if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                LOG.warn("Warning: URL host '" + urlHostName + "' is different to SSLSession host '" + session.getPeerHost() + "'.");
            }
            return true;
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }

    private TrustManager[] getTrustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                    }
                    @Override
                    public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                    }
                }
        };
    }
}
