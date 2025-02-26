package example.com;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepo;

//	@Autowired
//	private NotesRepository notesRepo;

	@Autowired
	private NotesService notesService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserServiceImpl userServiceImpl;

	@ModelAttribute
	public UserEntity getUser(Principal p, Model m) {
		String email = p.getName();
		UserEntity user = userRepo.findByEmail(email);
		m.addAttribute("user", user);
		return user;
	}
	
	//1
	@GetMapping("/homePage")
	public String homePage(Model m, Principal p) {
		 if (p == null) {
		        return "redirect:/signin"; // Redirect to login if user is not authenticated
		    }
		UserEntity user = getUser(p, m);
		m.addAttribute("name", user.getName().toUpperCase());
		m.addAttribute("userID", user.getId());
		return "homePage";
	}

	//2
	@GetMapping("/addNotes")
	public String addNotes() {
		System.out.println("controller come in add notes");
		return "addNotes";
	}
	
	@PostMapping("/saveNotes")
	public String saveNotes(@ModelAttribute NotesEntity notes, HttpSession session, Principal p, Model m) {
		notes.setLocalDate(LocalDate.now());
		notes.setUser(getUser(p, m));
		
		NotesEntity saveNotes = notesService.saveNotes(notes);

		if (saveNotes != null) {
			session.setAttribute("msg", "Notes save successfully");
		} else {
			session.setAttribute("msg", "something went wrong");
		}
		return "redirect:/user/addNotes";
	}

	//3
//	@GetMapping("/viewNotes")
//	public String viewNotes(Model m, Principal p) {
//		UserEnity user = getUser(p, m);
//		List<NotesEntity> allNotesByUser = notesService.getNotesByUser(user);
//		m.addAttribute("notesList", allNotesByUser);
//		return "viewNotes";
//	}

	//3
	// pagination
	@GetMapping("/viewNotes")
	public String viewNotes(Model m, Principal p, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		UserEntity user = getUser(p, m);
		// from notesRepo
		Page<NotesEntity> paginatedNotes = notesService.getNotesByUser(user, page, size);

		m.addAttribute("notesList", paginatedNotes.getContent());//data
		m.addAttribute("currentPage", page); //current page
		m.addAttribute("totalPages", paginatedNotes.getTotalPages());// total page
		m.addAttribute("totalItems", paginatedNotes.getTotalElements());// total count of data
		
		// Optionally add a message if no notes are available
	    if (paginatedNotes.getTotalElements() == 0) {
	        m.addAttribute("noNotesMessage", "No notes available.");
	    }
		
		return "viewNotes";
	}

	
	//4
	@GetMapping("/editNotes/{id}")
	public String editNotes(@PathVariable int id, Model model, HttpSession session) {
		NotesEntity note = notesService.getNotesById(id);
		model.addAttribute("notes", note);
		// session.setAttribute("msg","Notes updated successfully");
		return "editNotes";
	}

	//5
	@GetMapping("/deleteNotes/{id}")
	public String deleteNotes(@PathVariable int id, HttpSession session) {
		notesService.deleteNotesById(id);
		session.setAttribute("msg", "Note deleted successfully.");
		return "redirect:/user/viewNotes"; // Redirect to the view notes page after deletion
	}
	
	//6 
	@GetMapping("/profile")
	public String profilePage(Model m, Principal p) {
		 if (p == null) {
		        return "redirect:/signin"; // Redirect to login if user is not authenticated
		    }
		UserEntity user = getUser(p, m);
		m.addAttribute("name", user.getName().toUpperCase());
		m.addAttribute("userID", user.getId());
		m.addAttribute("email", user.getEmail());
		m.addAttribute("gender", user.getGender());
		m.addAttribute("address", user.getAddress());
		return "profilePage";
	}
	
	
//	@PostMapping("/update-profile")
//	public String updateProfile(  
//	        @RequestParam("name") String name,
//	        @RequestParam("gender") String gender,
//	        @RequestParam("address") String address,
//	        @RequestParam(value = "id", required = false) Integer id,
//	        Model model) {
//		if (id == null) {
//			
//			 return "/user/profilePage";
//		}
//
//	    Optional<UserEntity> optionalUser = userRepo.findById(id); // Fix: Use Optional
//
//	    if (optionalUser.isPresent()) { // Fix: Handle Optional properly
//	        UserEntity user = optionalUser.get();
//	        user.setName(name);
//	        user.setGender(gender);
//	        user.setAddress(address);
//
//	        userRepo.save(user); // Fix: Save after modification
//
//	        // Add a success message
//	        model.addAttribute("success", "Profile updated successfully!");
//	    } else {
//	        model.addAttribute("error", "User not found!");
//	    }
//
//	    return "/user/profilePage"; // Fix: Return a valid view name (change 'profile' as per your template)
//	}

	
	
	//7
	@GetMapping("/setting")
	public String setting() {
		return "settingPage";
	}
	
	
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, 
	                             @RequestParam("newPassword") String newPassword,
	                              Principal p, Model m, RedirectAttributes redirectAttributes) {
	    System.out.println("old pass:- " + oldPassword);
	    System.out.println("new pass:- " + newPassword);

	    UserEntity user = getUser(p, m);

	    Optional<UserEntity> byId = userRepo.findById(user.getId());

	    if (!byId.isPresent()) {
	        redirectAttributes.addFlashAttribute("error", "User not authenticated");
	        return "redirect:/login";
	    }

	    UserEntity existingUser = byId.get();
	    
	    // Verify old password matches
	    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
	        redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
	        return "redirect:/user/setting"; // Stay on settings page
	    }

	    // Validate new password length
	    if (newPassword.length() < 6) {
	        redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters");
	        return "redirect:/user/setting";
	    }

	    // Update password
	    String encodedNewPassword = passwordEncoder.encode(newPassword);
	    user.setPassword(encodedNewPassword);

	    try {
	        userRepo.save(existingUser);
	        redirectAttributes.addFlashAttribute("success", "Password changed successfully");
	        return "redirect:/user/setting"; // Stay on settings page with success message
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Error updating password");
	        return "redirect:/user/setting";
	    }
	}




}
