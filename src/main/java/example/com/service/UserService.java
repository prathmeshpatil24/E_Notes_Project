package example.com.service;

import example.com.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

	public UserEntity saveUser(UserEntity user, HttpServletRequest request);

	public boolean existEmailCheck(String email);

	public void sendVerificationEmail(UserEntity user, String siteURL);
	
	public String verifyUser(String code);
	
	//added

}
