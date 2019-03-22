package org.akj.springboot.authorization.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.akj.springboot.authorization.beans.OAuth2AccessToken;
import org.akj.springboot.authorization.beans.User;
import org.akj.springboot.authorization.config.AuthorizationCodeConfiguration;
import org.akj.springboot.authorization.config.DefaultTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

	private static final String ACCESS_TOKEN = "access_token";

	@Autowired
	private DefaultTokenService tokenService;

	@Autowired
	private AuthorizationCodeConfiguration configuration;

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/callback")
	public ModelAndView callback(String code, String state, HttpServletRequest request, HttpSession session) {
		OAuth2AccessToken token = tokenService.getToken(code);

//		Cookie cookie = new Cookie("access_token", token.getAccessToken());
		session.setAttribute(ACCESS_TOKEN, token.getAccessToken());

		return new ModelAndView("redirect:/home");
	}

	@GetMapping("/home")
	public ModelAndView home(HttpSession session) {
		String token = null;

		if (session.getAttribute(ACCESS_TOKEN) != null)
			token = (String) session.getAttribute(ACCESS_TOKEN);

		if (token == null) {
			String authEndpoint = tokenService.getAuthorizationEndpoint();
			return new ModelAndView("redirect:" + authEndpoint);
		}

		ModelAndView mv = new ModelAndView("home");
		mv.addObject("user", getUserInfo(token));

		return mv;
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		if (session.getAttribute(ACCESS_TOKEN) != null)
			session.removeAttribute(ACCESS_TOKEN);

		return "index";
	}

	private User getUserInfo(String token) {
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Authorization", "Bearer " + token);
		String endpoint = configuration.getUserInfoUrl();

		try {
			RequestEntity<Object> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(endpoint));

			ResponseEntity<User> userInfo = restTemplate.exchange(request, User.class);

			if (userInfo.getStatusCode().is2xxSuccessful()) {
				return userInfo.getBody();
			} else {
				throw new RuntimeException("it was not possible to retrieve user profile");
			}
		} catch (HttpClientErrorException e) {
			throw new RuntimeException("it was not possible to retrieve user profile");
		}
	}
}
