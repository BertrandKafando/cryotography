package es.kf.signapp.security.user.service;


import es.kf.signapp.exceptions.RecordAlreadyExistException;
import es.kf.signapp.security.user.dao.UserDAO;
import es.kf.signapp.security.user.dto.UserSummary;
import es.kf.signapp.security.user.mapper.UserSummaryMapper;
import es.kf.signapp.security.user.model.AppUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

	private  final UserDAO dao;
	private final PasswordEncoder passwordEncoder;
	private final UserSummaryMapper mapper;


	public UserSummary add(UserSummary userDTO)  {
         		AppUser user = mapper.toModel(userDTO);
		if (user == null) {
			throw new IllegalArgumentException("User obect connot be null");
		}
		if (StringUtils.isNotBlank(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		if (StringUtils.isNotBlank(user.getPhone())) {
			if (user.getId() == null && dao.existsByPhoneAndIdNot(user.getPhone(), 0L)) {
				throw new RecordAlreadyExistException(
						String.format("Le numéro de téléphone %s appartient déjà à un utilisateur", user.getPhone()));
			} else if (user.getId() != null && dao.existsByPhoneAndIdNot(user.getPhone(), user.getId())) {
				throw new RecordAlreadyExistException(
						String.format("Le numéro de téléphone %s appartient déjà à un utilisateur", user.getPhone()));
			}
		}
		dao.save(user);
		return mapper.toDto(user);
	}


	public Page<UserSummary> findAll(int page, int size) {
		return dao.findAll(PageRequest.of(page, size, Sort.by("id").descending())).map(mapper::toDto);
	}

	public AppUser findById(Long id) {
		return dao.findById(id).orElse(null);
	}

	public void deleteBy(Long id) {
		dao.deleteById(id);
	}

	public List<AppUser> findByKeyword(String keyword) {
		return dao.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);

	}

	public Page<AppUser> findStaff(int page, int size) {
		return dao.findDistinctByRolesNameNotIn(Arrays.asList("ROLE_STUDENT", "ROLE_PARENT", "ROLE_TEACHER"),
				PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
	}

	public Page<AppUser> findByRoles(List<String> roles, int page, int size) {
		return dao.findDistinctByRolesNameIn(roles, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
	}

	public UserSummary update(UserSummary user) {
		Optional<AppUser> u = dao.findById(user.getId());
		u.get().setFirstName(user.getFirstName());
		u.get().setLastName(user.getLastName());
		u.get().setEmail(user.getEmail());
		u.get().setPhone(user.getPhone());
		if(StringUtils.isNotBlank(user.getPassword())) {
			u.get().setPassword(passwordEncoder.encode(user.getPassword()));
		}
		return mapper.toDto(dao.save(u.get()));
	}

	@Transactional
	public void updatePassword(Long userId, String password) {

		if (StringUtils.isBlank(password)) {
			return;
		}
		Optional<AppUser> user = dao.findById(userId);

		if (user.isEmpty()) {
			return;
		}
		user.get().setPassword(passwordEncoder.encode(password));
	}

	public AppUser findByUsername(String username) {
		return dao.findByUsername(username);
	}

}
