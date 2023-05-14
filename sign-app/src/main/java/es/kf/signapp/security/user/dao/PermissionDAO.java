package es.kf.signapp.security.user.dao;

import es.kf.signapp.security.user.model.AppPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionDAO extends JpaRepository<AppPermission, Long> {

}
