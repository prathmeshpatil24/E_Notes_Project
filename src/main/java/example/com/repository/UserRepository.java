package example.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import example.com.entity.UserEntity;



@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>{
	
	public boolean existsByEmail(String email);

	public UserEntity findByEmail(String email);
	
	public UserEntity findByVerificationCode(String verificationCode);

//    Optional<UserEntity> findById(int id);
}
