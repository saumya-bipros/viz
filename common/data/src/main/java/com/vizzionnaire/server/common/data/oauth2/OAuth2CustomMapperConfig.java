package com.vizzionnaire.server.common.data.oauth2;

import com.vizzionnaire.server.common.data.validation.Length;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder(toBuilder = true)
@EqualsAndHashCode
@Data
@ToString(exclude = {"password"})
public class OAuth2CustomMapperConfig {
    @Length(fieldName = "url")
    private final String url;
    @Length(fieldName = "username")
    private final String username;
    @Length(fieldName = "password")
    private final String password;
    private final boolean sendToken;
}
