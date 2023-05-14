package es.kf.signapp.security.user.dao;

import es.kf.signapp.security.user.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDAO extends JpaRepository<AppRole, Long> {

}
