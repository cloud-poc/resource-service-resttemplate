package org.akj.springboot.authorization.config;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akj.springboot.authorization.beans.OAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class DefaultTokenService {

	@Autowired
	private AuthorizationCodeConfiguration configuration;

	public String getAuthorizationEndpoint() {
		Map<String, String> authParameters = new HashMap<>();
		authParameters.put("client_id", configuration.getClientId());
		authParameters.put("response_type", "code");
		authParameters.put("redirect_uri", getEncodedUrl(configuration.getRedirectUrl()));

		StringBuffer scopes = new StringBuffer();
		for (String scope : configuration.getScope()) {
			scopes.append(scope).append(",");
		}

		authParameters.put("scope", getEncodedUrl(scopes.toString().substring(0, scopes.length() - 1)));

		return buildUrl(configuration.getAuthorizeEndpoint(), authParameters);
	}

	private String buildUrl(String endpoint, Map<String, String> parameters) {
		List<String> paramList = new ArrayList<>(parameters.size());

		parameters.forEach((name, value) -> {
			paramList.add(name + "=" + value);
		});

		return endpoint + "?" + paramList.stream().reduce((a, b) -> a + "&" + b).get();
	}

	private String getEncodedUrl(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public OAuth2AccessToken getToken(String authorizationCode) {
		RestTemplate rest = new RestTemplate();
		String authBase64 = encodeCredentials(configuration.getClientId(), configuration.getClientSecret());

		RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
				contructAuthorizeReqBody(authorizationCode), contrucAuthorizeReqHeader(authBase64), HttpMethod.POST,
				URI.create(configuration.getAcccessTokenEndpoint()));

		ResponseEntity<OAuth2AccessToken> responseEntity = rest.exchange(requestEntity, OAuth2AccessToken.class);

		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}

		throw new RuntimeException("error trying to retrieve access token");
	}

	public String encodeCredentials(String username, String password) {
		String credentials = username + ":" + password;
		String encoded = new String(Base64.getEncoder().encode(credentials.getBytes()));

		return encoded;
	}

	public MultiValueMap<String, String> contructAuthorizeReqBody(String authorizationCode) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "authorization_code");

		StringBuffer scopes = new StringBuffer();
		for (String scope : configuration.getScope()) {
			scopes.append(scope).append(",");
		}
		formData.add("scope", scopes.toString().substring(0, scopes.length() - 1));
		formData.add("code", authorizationCode);
		formData.add("redirect_uri", configuration.getRedirectUrl());
		return formData;
	}

	public HttpHeaders contrucAuthorizeReqHeader(String clientAuthentication) {
		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		httpHeaders.add("Authorization", "Basic " + clientAuthentication);

		return httpHeaders;
	}

}
