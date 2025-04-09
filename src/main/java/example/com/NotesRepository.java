package example.com;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NotesRepository extends JpaRepository<NotesEntity, Integer> {
	
	//public List<NotesEntity>findByUser(UserEnity user);
	
	Page<NotesEntity> findByUser(UserEntity user, Pageable pageable);
	
	List<NotesEntity> findByFilePath(String filePath);
	
	
	@Query("SELECT n FROM NotesEntity n WHERE n.user.id = :userId AND "
			+ "(LOWER(n.title) LIKE %:keyword% OR LOWER(n.description) LIKE %:keyword%)")
	Page<NotesEntity>findByUserAndKeyword(@Param("userId")int userId,
			                              @Param("keyword")String keyword,
			                              Pageable pageable);
		

}
