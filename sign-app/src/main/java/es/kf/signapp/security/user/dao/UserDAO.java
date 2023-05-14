package es.kf.signapp.security.user.dao;

import es.kf.signapp.security.user.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<AppUser, Long> {
	
	AppUser findByUsername(String username);

	boolean existsByPhoneAndIdNot(String phone, Long id);

	List<AppUser> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String keyword, String keyword1);
	
	Page<AppUser> findDistinctByRolesNameNotIn(List<String> roles, Pageable pageable);
	
	Page<AppUser> findDistinctByRolesNameIn(List<String> roles, Pageable pageable);
}
