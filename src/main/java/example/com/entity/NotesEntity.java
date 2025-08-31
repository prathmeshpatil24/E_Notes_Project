package example.com.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes")
public class NotesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String title;

	private String description;

	private LocalDate localDate;

	private String filePath; // Store file path (Recommended)

	@ManyToOne
	private UserEntity user;

	@Column(name = "is_favorite")
	private boolean isFavorite = false;
	
	private LocalDateTime lastAccessed = LocalDateTime.now();

	@OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
	private List<RecentView> viewers = new ArrayList<>();

	public int getId() {
		return id;
	}

	public List<RecentView> getViewers() {
		return viewers;
	}

	public void setViewers(List<RecentView> viewers) {
		this.viewers = viewers;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getLocalDate() {
		return localDate;
	}

	public void setLocalDate(LocalDate localDate) {
		this.localDate = localDate;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean favorite) {
		isFavorite = favorite;
	}

	public LocalDateTime getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(LocalDateTime lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	@Override
	public String toString() {
		return "NotesEntity [id=" + id + ", title=" + title + ", description=" + description + ", localDate="
				+ localDate + ", user=" + user + "]";
	}

}
