package example.com.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import example.com.entity.NotesEntity;
import example.com.entity.UserEntity;
import example.com.repository.NotesRepository;



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
	
	
	public Page<NotesEntity> getNotesByUser(UserEntity user, int pageNo, int pageSize,String sort) {
		 Sort sorting = sort.equalsIgnoreCase("desc") 
				      ? Sort.by("title").descending()
				      : Sort.by("title").ascending();
		Pageable pageable = PageRequest.of(pageNo, pageSize,sorting); // Specify page number and size
        return notesRepository.findByUser(user, pageable);
    }
	
	public Page<NotesEntity>searchNotesByKeyword(UserEntity user, String keyword, int page, int size, String sort){
		 Sort sorting = sort.equalsIgnoreCase("desc")
				      ? Sort.by("title").descending()
				      : Sort.by("title").ascending();
		Pageable pageable = PageRequest.of(page, size, sorting);
		return notesRepository.findByUserAndKeyword(user.getId(), keyword.toLowerCase(), pageable);
	}
	
	
	
//	public Page<NotesEntity> findRecentNotes(UserEntity userEntity,int page, String keyword ){
//		Pageable pageable = PageRequest.of(page, 5, Sort.by("lastAccessed").descending());
//		
//		if (keyword != null && !keyword.isEmpty()) {
//            return notesRepository.findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByLastAccessedDesc(
//                userEntity, keyword, keyword, pageable);
//        }
//		 return notesRepository.findByUserOrderByLastAccessedDesc(userEntity, pageable);
//	}
	
	
//	public Page<NotesEntity> findFavoriteNotes(UserEntity user, int page, String keyword) {
//        Pageable pageable = PageRequest.of(page, 5);
//        if (keyword != null && !keyword.isEmpty()) {
//            return notesRepository.findByUserAndIsFavoriteTrueAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
//                user, keyword, keyword, pageable);
//        }
//        return notesRepository.findByUserAndIsFavoriteTrueOrderByCreatedAtDesc(user, pageable);
//    }
//    
//    @Transactional
//    public void toggleFavorite(int noteId) throws Exception {
//        NotesEntity note = notesRepository.findById(noteId)
//        		.orElseThrow(() -> new Exception("Notes not found"));
//            
//        note.setFavorite(!note.isFavorite());
//        notesRepository.save(note);
//    }
//    
//    @Transactional
//    public void updateLastAccessed(int noteId) throws Exception {
//        NotesEntity note = notesRepository.findById(noteId)
//            .orElseThrow(() -> new Exception("Note not found"));
//        note.setLastAccessed(LocalDateTime.now());
//        notesRepository.save(note);
//    }
//	
	
	

}
