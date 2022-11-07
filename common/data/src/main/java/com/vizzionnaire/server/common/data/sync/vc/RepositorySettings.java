package com.vizzionnaire.server.common.data.sync.vc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
public class RepositorySettings implements Serializable {
    private static final long serialVersionUID = -3211552851889198721L;

    private String repositoryUri;
    private RepositoryAuthMethod authMethod;
    private String username;
    private String password;
    private String privateKeyFileName;
    private String privateKey;
    private String privateKeyPassword;
    private String defaultBranch;

    public RepositorySettings() {
    }

    public RepositorySettings(RepositorySettings settings) {
        this.repositoryUri = settings.getRepositoryUri();
        this.authMethod = settings.getAuthMethod();
        this.username = settings.getUsername();
        this.password = settings.getPassword();
        this.privateKeyFileName = settings.getPrivateKeyFileName();
        this.privateKey = settings.getPrivateKey();
        this.privateKeyPassword = settings.getPrivateKeyPassword();
        this.defaultBranch = settings.getDefaultBranch();
    }
}
