package example.com.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import example.com.entity.NotesEntity;
import example.com.entity.UserEntity;

@Repository
public interface NotesRepository extends JpaRepository<NotesEntity, Integer> {
	
	//public List<NotesEntity>findByUser(UserEnity user);
	
	Page<NotesEntity> findByUser(UserEntity user, Pageable pageable);
	
	List<NotesEntity> findByFilePath(String filePath);
	
	void deleteByUser(UserEntity user);
	
	
	
	//for search  For all notes (with search)
	@Query("SELECT n FROM NotesEntity n WHERE n.user.id = :userId AND "
			+ "(LOWER(n.title) LIKE %:keyword% OR LOWER(n.description) LIKE %:keyword%)")
	Page<NotesEntity>findByUserAndKeyword(@Param("userId")int userId,
			                              @Param("keyword")String keyword,
			                              Pageable pageable);
	
	
	  // For recent notes
//    Page<NotesEntity> findByUserOrderByLastAccessedDesc(UserEntity user, Pageable pageable);
//    
//    // For favorite notes
//    Page<NotesEntity> findByUserAndIsFavoriteTrueOrderByCreatedAtDesc(UserEntity user, Pageable pageable);
//
//    // For recent notes with search
//	Page<NotesEntity> findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByLastAccessedDesc(
//			UserEntity userEntity, String keyword, String keyword2, Pageable pageable);
//
//	// For favorite notes with search
//	Page<NotesEntity> findByUserAndIsFavoriteTrueAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
//			UserEntity user, String keyword, String keyword2, Pageable pageable);
	
		

}
