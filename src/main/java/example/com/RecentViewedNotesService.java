package example.com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecentViewedNotesService {
	
	@Autowired
	private RecentViewRepository recentViewRepository;

	public List<NotesEntity> getRecentNotesForUser(UserEntity user) {
        List<RecentView> recentViews = recentViewRepository.findTop5ByUserOrderByViewedAtDesc(user);
        return recentViews.stream()
        		.map(RecentView::getNote)
        		.toList();
	
   }
}
