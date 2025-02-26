package example.com;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	public AppConfig appConfig;

	@GetMapping("/")
	public String home() {
		System.out.println("controller come to home page");
		return "index";

	}

	@GetMapping("/register")
	public String register() {
		System.out.println("controller come to register page");
		return "register";

	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserEntity user, HttpSession session, Model model,
			HttpServletRequest request) {

		boolean emailExists = userService.existEmailCheck(user.getEmail());

		if (emailExists) {
			session.setAttribute("msg", "Email already exist");
		} else {

			UserEntity saveUser = userService.saveUser(user, request);
			System.out.println("controller is in register page and data is saved successfully");
			// System.out.println(saveUser);

			if (saveUser != null) {
				String siteURL = Utility.getSiteURL(request);
				System.out.println("user data from Home Controller:- " + user + "/tstite URL:- " + siteURL);
				userService.sendVerificationEmail(user, siteURL);
				session.setAttribute("msg", "Registered successfully Please Verify Email");

			} else {
				session.setAttribute("msg", "Something wrong on server");
			}
		}

		return "redirect:/register";
	}

	@GetMapping("/verify")
	public String verifyUser(@RequestParam("code") String code, Model model) {
		String result = userService.verifyUser(code);

		if (result.equals("Verification successful")) {
			model.addAttribute("success", true);
			model.addAttribute("msg", "Your account has been verified successfully!");
		} else {
			model.addAttribute("success", false);
			model.addAttribute("msg", "Invalid or expired verification code.");
		}

		return "verificationResult"; // Point this to a Thymeleaf template
	}

	@GetMapping("/signin")
	public String login() {
		System.out.println("controller come to login page");
		return "login";

	}

	@GetMapping("/forgot-password")
	public String showForgotPasswordForm() {
		return "forgotPassword"; //forgotPassword.html page
	}

	@PostMapping("/sendOTP")
	public String sendOTP(@RequestParam("email") String email, Model m, HttpSession session) throws MailException {
		System.out.println("entered email:- " + email);

		boolean existEmailCheck = userService.existEmailCheck(email);
		if (!existEmailCheck) {
			m.addAttribute("error", "Email does not exist!");
			return "register";
		}
		// Generating a 4 to 5 digit random OTP
		Random random = new Random();
		// we can now send the OTP to the user's email or save it for verification
		int otp = 1000 + random.nextInt(99999); // Generates a number between 1000 and 9999

		System.out.println("Generated OTP: " + otp);

		try {
			// Send OTP to the user's email
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(email);
			message.setSubject("OTP for Password Reset");
			message.setText("Your OTP is: " + otp);
			mailSender.send(message);

			// Save the OTP in the session for later verification
			session.setAttribute("otp", otp);
			session.setAttribute("email", email);
			m.addAttribute("success", "OTP sent successfully. Please check your email.");
			return "verifyOTP";//html page
			
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Error sending OTP: " + e.getMessage());
			m.addAttribute("error", "Failed to send OTP. Please try again later."); // Error message
			return "forgotPassword"; // or some error page
		}

	}
	
	@PostMapping("/verifyOTP")
	public String veryifyOTP(@RequestParam("otp") int opt, HttpSession session, Model model) {
		int generatedOTP = (int)session.getAttribute("otp");
		String email = (String) session.getAttribute("email");
		
		if (generatedOTP == opt) {
			System.out.println("opt matched");
			model.addAttribute(email);
			return "resetPasswordPage";//html page
		}else {
			model.addAttribute("error", "OTP not matched, Please try again");
			return "verifyOTP";// html page
		}
		
	}
	
	
	@PostMapping("/resetPassword")
	public String resetPassword( 
	        @RequestParam("newPassword") String newPassword, 
	        @RequestParam("confirmPassword") String confirmPassword, 
	        Model model, HttpSession session) {
		String userEmail = (String) session.getAttribute("email");
		
		// Check if newPassword and confirmPassword match
	    if (!newPassword.equals(confirmPassword)) {
	        model.addAttribute("error", "Passwords do not match. Please try again.");
	        return "resetPasswordPage"; // Return to the reset password page
	    }
	    
	    UserEntity user = userRepository.findByEmail(userEmail);
	    System.out.println(user);
	    
	    if (user != null) {
	    	String encodePasswordString = appConfig.passwordEncoder().encode(confirmPassword);
			user.setPassword(encodePasswordString);
			userRepository.save(user);
			model.addAttribute("success", "Password has been reset successfully!");
	        return "resetPasswordPage"; // Redirect to the login page
		}
	    else {
	        model.addAttribute("error", "An error occurred while resetting the password.");
	        return "resetPasswordPage";
	    }
		
	}

}
