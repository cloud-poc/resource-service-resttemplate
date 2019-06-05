package org.akj.springboot.authorization.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
//@ConditionalOnProperty(prefix = "security", value = "oauth2.client.auth-code.enabled")
public class AuthorizationCodeConfiguration {
	private static final String GRANT_TYPE = "authorization_code";

	@Value("${security.oauth2.client.auth-code.redirect-url}")
	private String redirectUrl;

	@Value("${security.oauth2.client.auth-code.authorize-url}")
	private String authorizeEndpoint;

	@Value("${security.oauth2.client.auth-code.access-token-url}")
	private String acccessTokenEndpoint;

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;

	@Value("${security.oauth2.client.auth-code.scope}")
	private Set<String> scope;

	@Value("${security.oauth2.client.user-info-url}")
	private String userInfoUrl;

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getAuthorizeEndpoint() {
		return authorizeEndpoint;
	}

	public void setAuthorizeEndpoint(String authorizeEndpoint) {
		this.authorizeEndpoint = authorizeEndpoint;
	}

	public String getAcccessTokenEndpoint() {
		return acccessTokenEndpoint;
	}

	public void setAcccessTokenEndpoint(String acccessTokenEndpoint) {
		this.acccessTokenEndpoint = acccessTokenEndpoint;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public Set<String> getScope() {
		return scope;
	}

	public void setScope(Set<String> scope) {
		this.scope = scope;
	}

	public String getUserInfoUrl() {
		return userInfoUrl;
	}

	public void setUserInfoUrl(String userInfoUrl) {
		this.userInfoUrl = userInfoUrl;
	}

}
