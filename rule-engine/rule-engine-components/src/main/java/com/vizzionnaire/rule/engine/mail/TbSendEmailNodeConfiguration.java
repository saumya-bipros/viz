package com.vizzionnaire.rule.engine.mail;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

@Data
public class TbSendEmailNodeConfiguration implements NodeConfiguration {

    private boolean useSystemSmtpSettings;
    private String smtpHost;
    private int smtpPort;
    private String username;
    private String password;
    private String smtpProtocol;
    private int timeout;
    private boolean enableTls;
    private String tlsVersion;
    private boolean enableProxy;
    private String proxyHost;
    private String proxyPort;
    private String proxyUser;
    private String proxyPassword;

    @Override
    public TbSendEmailNodeConfiguration defaultConfiguration() {
        TbSendEmailNodeConfiguration configuration = new TbSendEmailNodeConfiguration();
        configuration.setUseSystemSmtpSettings(true);
        configuration.setSmtpHost("localhost");
        configuration.setSmtpProtocol("smtp");
        configuration.setSmtpPort(25);
        configuration.setTimeout(10000);
        configuration.setEnableTls(false);
        configuration.setTlsVersion("TLSv1.2");
        configuration.setEnableProxy(false);
        return configuration;
    }
}
