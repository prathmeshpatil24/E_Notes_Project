package example.com;

import java.io.File;

import org.hibernate.validator.constraints.ISBN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NotesService {
	@Autowired
	private NotesRepository notesRepository;
	
	
	public NotesEntity saveNotes(NotesEntity notes) {
		return notesRepository.save(notes);
	}
	
	
	public NotesEntity getNotesById(int id) {
		return notesRepository.findById(id).get();
	}
	
	
	public NotesEntity updatNotes(NotesEntity notes) {
		return notesRepository.save(notes);
	}
	
	public Boolean deleteNotesById(int id) {
		NotesEntity notesEntity = notesRepository.findById(id).get();
		
		if (notesEntity != null) {
			if (notesEntity.getFilePath()!= null) {
				File file = new File(notesEntity.getFilePath());
				if (file.exists()) {
                    boolean deleted = file.delete();
                    System.out.println("is deleted:-" + deleted);
                    if (!deleted) {
                        // Log or handle the failure (optional)
                        System.err.println("Failed to delete file: " + notesEntity.getFilePath());
                    }
                }
			}
			notesRepository.delete(notesEntity);
			return true;	
		}
		return false;
		
	}
	
//	public List<NotesEntity>getNotesByUser(UserEnity user){
//	
//		return notesRepository.findByUser(user);
//	}
	
	
	public Page<NotesEntity> getNotesByUser(UserEntity user, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize); // Specify page number and size
        return notesRepository.findByUser(user, pageable);
    }
	
	
	

}
