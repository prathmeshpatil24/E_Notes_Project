package example.com.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

//entity class
@Entity
@Table(name = "users")
public class UserEntity {

	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;

	    @NotBlank(message = "Name is mandatory")
	    @Size(max = 100, message = "Name cannot exceed 100 characters")
	    private String name;

	    @NotBlank(message = "Email is mandatory")
	    @Email(message = "Invalid email format")
	    private String email;

	    @Size(max = 255, message = "Address cannot exceed 255 characters")
	    private String address;

	    @NotBlank(message = "Gender is mandatory")
	    private String gender;

	    @NotBlank(message = "Password is mandatory")
	    @Size(min = 6, message = "Password must be at least 6 characters")
	    private String password;

	    private String role;

	    private Boolean isEnable;

	    @Column(unique = true, length = 64, nullable = false, updatable = false)
	    private String verificationCode;

	    
	    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	    private List<RecentView> viewedNotes = new ArrayList<>();

	    
	    public List<RecentView> getViewedNotes() {
			return viewedNotes;
		}

		public void setViewedNotes(List<RecentView> viewedNotes) {
			this.viewedNotes = viewedNotes;
		}

		public UserEntity() {
	        // Default constructor
	    }

	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getAddress() {
	        return address;
	    }

	    public void setAddress(String address) {
	        this.address = address;
	    }

	    public String getGender() {
	        return gender;
	    }

	    public void setGender(String gender) {
	        this.gender = gender;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public String getRole() {
	        return role;
	    }

	    public void setRole(String role) {
	        this.role = role;
	    }

	    public Boolean getEnable() {
	        return isEnable;
	    }

	    public void setEnable(Boolean isEnable) {
	        this.isEnable = isEnable;
	    }

	    public String getVerificationCode() {
	        return verificationCode;
	    }

	    public void setVerificationCode(String verificationCode) {
	        this.verificationCode = verificationCode;
	    }

//	    @Override
//	    public String toString() {
//	        return "UserEntity [id=" + id + ", name=" + name + ", email=" + email + ", address=" + address + ", gender="
//	                + gender + ", role=" + role + ", enable=" + enable + "]";
//	    }
	}