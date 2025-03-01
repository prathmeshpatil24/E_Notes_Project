package example.com;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NotesRepository extends JpaRepository<NotesEntity, Integer> {
	
	//public List<NotesEntity>findByUser(UserEnity user);
	
	Page<NotesEntity> findByUser(UserEntity user, Pageable pageable);
	
	List<NotesEntity> findByFilePath(String filePath);
		

}
