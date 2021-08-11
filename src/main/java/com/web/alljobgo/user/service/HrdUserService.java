package com.web.alljobgo.user.service;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.alljobgo.user.domain.userVO;
import com.web.alljobgo.user.persistance.UserDAO;

@Service("HrdUserService")
public class HrdUserService implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(HrdUserService.class);
	private final UserDAO userDAO;
	private final PasswordEncoder passwordEncoder;
	
	public HrdUserService(
			UserDAO userDAO,
			@Qualifier("passwordEncoder") PasswordEncoder passwordEncoder
			) {
		this.userDAO = userDAO;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public boolean joinUser(userVO vo) throws Exception {
		logger.info("hrdUserService ==> joinUser");
		
		encodePassword(vo);
		
		if(!userDAO.joinUser(vo)) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isExistByEmail(String Email) throws Exception {
		userVO vo = userDAO.findByEmail(Email);
		return vo == null ? false : true;
	}
	
	@Override
	public JSONObject isExistByEmailJson(String Email) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject getEmail = (JSONObject) parser.parse(Email);
		
		Map<String, Boolean> obj = new HashMap<String, Boolean>();
		obj.put("exist", isExistByEmail((String) getEmail.get("userEmail")));
		
		return new JSONObject(obj);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			userVO vo = userDAO.findByEmail(username);
			if(vo == null) {
				throw new UsernameNotFoundException(username);
			}
			return vo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void encodePassword(userVO vo) {
		vo.setPass(passwordEncoder.encode(vo.getPass())); 
	}
}
