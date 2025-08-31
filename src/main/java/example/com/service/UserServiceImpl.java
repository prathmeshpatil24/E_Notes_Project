package example.com.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import example.com.configuration.AppConfig;
import example.com.entity.UserEntity;
import example.com.repository.UserRepository;
import example.com.utility.Utility;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	public AppConfig appConfig;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	public void sendVerificationEmail(UserEntity user, String siteURL) {
		// TODO Auto-generated method stub
		 // Create the email content
	    String subject = "Please Verify your registration";
	    String senderName = "e_notes Team";
	    String mailContent = "<p> Dear " + user.getName() + ",</p>";
	      
	    mailContent += "<p>Please verify your email by clicking the link below:</p>";
	    
	    String verificationUrl = siteURL.trim() + "/verify?code=" + user.getVerificationCode();
	    
	    mailContent += "<h3><a href=\"" + verificationUrl + "\"><strong>Verify Email</strong></a></h3>";
	    mailContent += "<p>Thank you,<br>e_notes Team</p>";
	    
	 // Print mailContent for debugging
	    System.out.println("Generated Email Content: " + mailContent);
	    try {
	    	
	        MimeMessage message = mailSender.createMimeMessage();
	        
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);
	        
	        helper.setFrom("pppatil7227@gmail.com", senderName);
	        helper.setTo(user.getEmail());
	        helper.setSubject(subject);
	        helper.setText(mailContent, true);

	        mailSender.send(message);
	        
	        System.out.println("Verification email sent successfully");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public UserEntity saveUser(UserEntity user, HttpServletRequest request) {
		user.setRole("ROLE_USER");
		user.setEnable(false);// Set to false because the user needs to verify their email
		user.setPassword(appConfig.passwordEncoder().encode(user.getPassword()));
		
		// Generate a verification code
	    String verificationCode = UUID.randomUUID().toString();
	    user.setVerificationCode(verificationCode);
		
		try {
		    UserEntity newUser = userRepo.save(user);
		    
		    String siteURL = Utility.getSiteURL(request);
		   sendVerificationEmail(user,siteURL);
		   System.out.println("user data from UserServiceImpl:- " + user + "/tstite URL:- " + siteURL  );
		    return newUser;
		    
		} catch (Exception e) {
		   // System.out.println("Error saving user or sending email");
		    e.printStackTrace();
		    return null; // Or throw a custom exception
		}
		
	}
	

	@Override
	public String verifyUser(String code) {
		 // Find the user by the verification code
	    UserEntity user = userRepo.findByVerificationCode(code);
	    if (user == null) {
	    	 // If no user is found, return an error message
	        return "Invalid verification code";
		}
	    // Enable the user and clear the verification code
	    user.setEnable(true);
	    //user.setVerificationCode(null);
	    userRepo.save(user);
	    
	 // Return a success message
	    return "Verification successful";
	}

	@Override
	public boolean existEmailCheck(String email) {
		return userRepo.existsByEmail(email);
	}

	//remove success error msg  method
	public void removeSessionMessage() {
		HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest()
				.getSession();

		session.removeAttribute("msg");
	}
	
	
//	private String getSiteURL(HttpServletRequest request) {
//	    StringBuilder url = new StringBuilder();
//	    url.append(request.getScheme()) // http
//	       .append("://")
//	       .append(request.getServerName()) // www.example.com
//	       .append(":")
//	       .append(request.getServerPort()) // 8080
//	       .append(request.getContextPath()); // /context-path (if applicable)
//	    return url.toString();
//	}

	


}
