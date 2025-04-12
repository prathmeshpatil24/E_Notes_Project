package example.com;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;


@Repository
public interface RecentViewRepository
extends JpaRepository<RecentView, Integer>{
	List<RecentView> findByViewedAt(LocalDateTime viewedAt);
	
	List<RecentView> findTop5ByUserOrderByViewedAtDesc(UserEntity user);

}
