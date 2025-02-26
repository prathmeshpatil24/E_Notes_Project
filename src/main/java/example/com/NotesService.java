package example.com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
