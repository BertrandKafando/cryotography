package es.kf.signapp;
import es.kf.signapp.model.User;
import es.kf.signapp.repositories.UserRepository;
import es.kf.signapp.security.user.dao.RoleDAO;
import es.kf.signapp.security.user.model.AppRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartUpRunner implements ApplicationRunner {

private final UserRepository userDAO;

private final 	RoleDAO roleDAO;

private final 	PasswordEncoder encoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		User u = userDAO.findByUsername("berto");

		if (u != null) {
			return;
		}
		AppRole role = new AppRole();
		role.setName("ROLE_ADMIN");
		roleDAO.save(role);

		u = new User();
		u.setUsername("berto");
		u.setCin("KATEST");
		u.setLogin("berto");
		u.setPassword(encoder.encode("test1234"));
		u.setFirstName("");
		u.setLastName("Admin");
		u.setEmail("test@gamil.com");
		u.setPhone("06xxxxxxxxx");
		u.getRoles().add(role);
		u.setEnabled(true);
		userDAO.save(u);
	}
}
