package example.com.controller;


import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import example.com.entity.NotesEntity;
import example.com.entity.UserEntity;
import example.com.repository.NotesRepository;
import example.com.repository.UserRepository;
import example.com.service.FileService;
import example.com.service.NotesService;
import example.com.service.RecentViewedNotesService;
import example.com.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private NotesRepository notesRepo;

	@Autowired
	private NotesService notesService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private FileService fileService;
	
	@Autowired
	private RecentViewedNotesService recentViewedNotesService;

	@ModelAttribute
	public UserEntity getUser(Principal p, Model m) {
		String email = p.getName();
		UserEntity user = userRepo.findByEmail(email);
		m.addAttribute("user", user);
		return user;
	}

	// 1
	@GetMapping("/homePage")
	public String homePage(Model m, Principal p) {
		if (p == null) {
			return "redirect:/signin"; // Redirect to login if user is not authenticated
		}
		UserEntity user = getUser(p, m);
		m.addAttribute("name", user.getName().toUpperCase());
		m.addAttribute("userID", user.getId());
		
		
		Page<NotesEntity> paginatedNotes;
		Page<NotesEntity> notesByUser = notesService.getNotesByUser(user, 1, 1, "asc");
//		long totalElements = notesByUser.getTotalElements();
		m.addAttribute("totalNotes", notesByUser.getTotalElements());
		
		
		//logic for recent view 
			
		List<NotesEntity> recentNotesForUser = recentViewedNotesService.getRecentNotesForUser(user);
		m.addAttribute("recentNotes", recentNotesForUser);
	
		return "homePage";
	}

	// 2
	@GetMapping("/addNotes")
	public String addNotes() {
		System.out.println("controller come in add notes");
		return "addNotes";
	}

	// for only text save data
//	@PostMapping("/saveNotes")
//	public String saveNotes(@ModelAttribute NotesEntity notes, HttpSession session, Principal p, Model m) {
//		notes.setLocalDate(LocalDate.now());
//		notes.setUser(getUser(p, m));
//		
//		NotesEntity saveNotes = notesService.saveNotes(notes);
//
//		if (saveNotes != null) {
//			session.setAttribute("msg", "Notes save successfully");
//		} else {
//			session.setAttribute("msg", "something went wrong");
//		}
//		return "redirect:/user/addNotes";
//	}

	// for text as well as file uploading
	@PostMapping("/saveNotes")
	public String saveNotes(@ModelAttribute NotesEntity notes,
			@RequestParam(value = "file", required = false) MultipartFile file, HttpSession session, Principal p,
			Model m) {
		try {
			notes.setLocalDate(LocalDate.now());
			notes.setUser(getUser(p, m));

			if (file != null && !file.isEmpty()) {
				String userEmail = p.getName();
				String filePath = fileService.uploadFile(file, userEmail);// get full path

				notes.setFilePath(filePath);

			}

			NotesEntity savedNotes = notesService.saveNotes(notes);
			if (savedNotes != null) {
				session.setAttribute("msg", "Notes save successfully");
			} else {
				session.setAttribute("msg", "something went wrong");
			}

		} catch (IllegalArgumentException e) {
			session.setAttribute("msg", "Invalid input: " + e.getMessage());
		} catch (IOException e) {
			session.setAttribute("msg", "File upload failed: " + e.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.setAttribute("msg", "Error saving note: " + e.getMessage().toString());
		}
		return "redirect:/user/addNotes";
	}

	// 3
//	@GetMapping("/viewNotes")
//	public String viewNotes(Model m, Principal p) {
//		UserEnity user = getUser(p, m);
//		List<NotesEntity> allNotesByUser = notesService.getNotesByUser(user);
//		m.addAttribute("notesList", allNotesByUser);
//		return "viewNotes";
//	}

	//logic for file view
	@GetMapping("/viewNotes/downloadNoteFile/{notesId}")
	public ResponseEntity<?>DownloadNotesFile(@PathVariable int notesId, Principal p, Model m){
		try {
			UserEntity userEntity = getUser(p, m);
			NotesEntity note = notesService.getNotesById(notesId);
            if (note == null || note.getFilePath() == null) {
                return new ResponseEntity<>("No file associated with this note", HttpStatus.NOT_FOUND);
            }
            if (!note.getUser().getEmail().equals(userEntity.getEmail())) {
                return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
            }
            
            byte[] fileData = fileService.downloadFile(note.getFilePath());
            String fileName = FilenameUtils.getName(note.getFilePath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(getContentType(fileName)));
            headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());

            return ResponseEntity.ok().headers(headers).body(fileData);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>("Error downloading file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	private String getContentType(String fileName) {
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        switch (extension) {
            case "pdf": return "application/pdf";
            case "xlsx": return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt": return "text/plain";
            case "png": return "image/png";
            case "jpeg": return "image/jpeg";
            default: return "application/octet-stream";
        }
    }
	
	// 3
	// pagination
	@GetMapping("/viewNotes")
	public String viewNotes(Model m, Principal p, 
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(value = "sort", required = false, defaultValue = "asc") String sort
			) {
		
		UserEntity user = getUser(p, m);
		
		// from notesRepo
		Page<NotesEntity> paginatedNotes;
		
		if (keyword != null & !keyword.trim().isBlank()) {
			 paginatedNotes = notesService.searchNotesByKeyword(user, keyword, page, size,sort);
		        m.addAttribute("keyword", keyword);
		}
        else {
	        paginatedNotes = notesService.getNotesByUser(user, page, size,sort);
	    }
		

		// Create a map of note IDs to filenames
        Map<Integer, String> fileNames = new HashMap<>();
        for (NotesEntity note : paginatedNotes.getContent()) {
            if (note.getFilePath() != null) {
                fileNames.put(note.getId(), FilenameUtils.getName(note.getFilePath()));
            }
        }
        
        
        m.addAttribute("fileNames", fileNames); // map of filenames
        //System.out.println(fileNames+ "file name");
		m.addAttribute("notesList", paginatedNotes.getContent());// data
		m.addAttribute("currentPage", page); // current page
		m.addAttribute("totalPages", paginatedNotes.getTotalPages());// total page
		m.addAttribute("totalItems", paginatedNotes.getTotalElements());// total count of data
		m.addAttribute("sort", sort);
		//m.addAttribute("filter", filter);
		
		// Optionally add a message if no notes are available
		if (paginatedNotes.getTotalElements() == 0) {
			m.addAttribute("noNotesMessage", "No notes available.");
		}

		return "viewNotes";
	}
	
//	@PostMapping("/toggleFavorite/{id}")
//    @ResponseBody
//    public ResponseEntity<?> toggleFavorite(@PathVariable int id) throws Exception {
//        notesService.toggleFavorite(id);
//        return ResponseEntity.ok().build();
//    }
//    
//    @PostMapping("/updateAccess/{id}")
//    @ResponseBody
//    public ResponseEntity<?> updateLastAccessed(@PathVariable int id) throws Exception {
//        notesService.updateLastAccessed(id);
//        return ResponseEntity.ok().build();
//    }

	// 4
	@GetMapping("/editNotes/{id}")
	public String editNotes(@PathVariable int id, Model model, HttpSession session) {
		NotesEntity note = notesService.getNotesById(id);
		model.addAttribute("notes", note);
		// session.setAttribute("msg","Notes updated successfully");
		return "editNotes";
	}

	// 5
	@GetMapping("/deleteNotes/{id}")
	public String deleteNotes(@PathVariable int id, HttpSession session,
			RedirectAttributes redirectAttributes) {
		notesService.deleteNotesById(id);
		String stringMsg = "Note with ID" +  id  + "deleted successfully!";
		redirectAttributes.addFlashAttribute("message", stringMsg);

		//session.setAttribute("msg", "Note deleted successfully.");
		return "redirect:/user/viewNotes"; // Redirect to the view notes page after deletion
	}

	// 6
	@GetMapping("/profile")
	public String profilePage(Model m, Principal p, RedirectAttributes redirectAttributes) {
		if (p == null) {
			return "redirect:/signin"; // Redirect to login if user is not authenticated
		}
		UserEntity user = getUser(p, m);
		m.addAttribute("name", user.getName().toUpperCase());
		m.addAttribute("userID", user.getId());
		m.addAttribute("email", user.getEmail());
		m.addAttribute("gender", user.getGender());
		m.addAttribute("address", user.getAddress());
		
		Page<NotesEntity> notesByUser = notesService.getNotesByUser(user, 1, 1, "asc");
		m.addAttribute("totalNotes", notesByUser.getTotalElements());

		// Retrieve flash attributes and add them to the model
		if (redirectAttributes.getFlashAttributes().containsKey("success")) {
			m.addAttribute("success", redirectAttributes.getFlashAttributes().get("success"));
		}
		if (redirectAttributes.getFlashAttributes().containsKey("error")) {
			m.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
		}

		return "profilePage";
	}

	@PostMapping("/update-profile")
	public String updateProfile(@RequestParam("name") String name, @RequestParam("gender") String gender,
			@RequestParam("address") String address, @RequestParam(value = "id", required = false) Integer id, Model m,
			Principal p, RedirectAttributes redirectAttributes) {

		UserEntity user = getUser(p, m);

		try {
			Optional<UserEntity> byId = userRepo.findById(user.getId());

			if (!byId.isPresent()) {
				return "redirect:/login";
			}

			UserEntity existingUser = byId.get();
			existingUser.setName(name);
			existingUser.setGender(gender);
			existingUser.setAddress(address);

			userRepo.save(existingUser);
			redirectAttributes.addFlashAttribute("success", "Profile updated successfully!"); // Use flash attribute

//            m.addAttribute("name", existingUser.getName());
//            m.addAttribute("gender", existingUser.getGender());
//            m.addAttribute("address", existingUser.getAddress());
//            m.addAttribute("email", existingUser.getEmail());
//            m.addAttribute("userID", existingUser.getId());
//            m.addAttribute("success", "Profile updated successfully!");

		} catch (DataAccessException e) {
			e.printStackTrace();
			// m.addAttribute("error", "An error occurred while updating your profile.
			// Please try again.");
			redirectAttributes.addFlashAttribute("error",
					"An error occurred while updating your profile. Please try again.");
		} catch (Exception e) {
			e.printStackTrace();
			// m.addAttribute("error", "An unexpected error occurred. Please try again.");
			redirectAttributes.addFlashAttribute("error", "An unexpected error occurred. Please try again.");
		}

		return "redirect:/user/profile";
	}

	// 7
	@GetMapping("/setting")
	public String setting() {
		return "settingPage";
	}

	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword,
			@RequestParam("confirmPassword") String confirmPassword,
			Principal p, Model m,
			RedirectAttributes redirectAttributes) {
		System.out.println("old pass:- " + oldPassword);
		System.out.println("new pass:- " + newPassword);
		System.out.println("confirm Password:- " + confirmPassword);

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
		
		// Check if new password and confirm password match
	    if (!newPassword.equals(confirmPassword)) {
	        redirectAttributes.addFlashAttribute("error", "New password and confirm password do not match");
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
	
	
	
//	delete-account
	@PostMapping("/delete-account")
	public String deleteAccount(@RequestParam("password") String password, Principal principal, Model model, HttpSession session) {

	    String userEmail = principal.getName();
	    UserEntity user = userRepo.findByEmail(userEmail);

	    if (user == null) {
	        model.addAttribute("error", "User not found!");
	        return "settings";
	    }

	    // ✅ If passwords are stored as hashed (e.g., BCrypt), use passwordEncoder
	    if (passwordEncoder.matches(password, user.getPassword())) {

	        // Delete associated notes
	        notesRepo.deleteByUser(user);

	        // Delete user account
	        userRepo.delete(user);

	        // Invalidate session
	        session.invalidate();

	        return "redirect:/?accountDeleted";

	    } else {
	        model.addAttribute("error", "Incorrect password. Please try again.");
	        return "settings"; // Return to settings page with error
	    }
	}

	

}
