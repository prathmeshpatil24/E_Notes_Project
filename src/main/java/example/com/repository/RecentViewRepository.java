package example.com.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import example.com.entity.RecentView;
import example.com.entity.UserEntity;


@Repository
public interface RecentViewRepository
extends JpaRepository<RecentView, Integer>{
	List<RecentView> findByViewedAt(LocalDateTime viewedAt);
	
	List<RecentView> findTop5ByUserOrderByViewedAtDesc(UserEntity user);

}
