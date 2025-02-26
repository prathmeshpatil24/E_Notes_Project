package example.com;

import org.springframework.beans.factory.annotation.Autowired;
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

			UserEntity saveUser = userService.saveUser(user,request);
			System.out.println("controller is in register page and data is saved successfully");
			// System.out.println(saveUser);
			
			if (saveUser != null) {
				String siteURL = Utility.getSiteURL(request);
				System.out.println("user data from Home Controller:- " + user + "/tstite URL:- " + siteURL  );
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
}
