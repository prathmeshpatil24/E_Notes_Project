package example.com;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class RecentView {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;

	    @ManyToOne
	    private UserEntity user;

	    @ManyToOne
	    private NotesEntity note;

	    private LocalDateTime viewedAt = LocalDateTime.now();

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public UserEntity getUser() {
			return user;
		}

		public void setUser(UserEntity user) {
			this.user = user;
		}

		public NotesEntity getNote() {
			return note;
		}

		public void setNote(NotesEntity note) {
			this.note = note;
		}

		public LocalDateTime getViewedAt() {
			return viewedAt;
		}

		public void setViewedAt(LocalDateTime viewedAt) {
			this.viewedAt = viewedAt;
		}

	    

}
