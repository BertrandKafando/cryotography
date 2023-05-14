package es.kf.signapp.security.user.controller;


import es.kf.signapp.security.user.dto.UserSummary;
import es.kf.signapp.security.user.model.AppUser;
import es.kf.signapp.security.user.service.AppUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// @RestController
@RequiredArgsConstructor
//@RequestMapping("/v1/users")
public class  UserController {
	private final AppUserService service;

	//@PostMapping()
	public UserSummary add(@RequestBody UserSummary user) {
		return service.add(user);
	}

	@PutMapping()
	public UserSummary update(@RequestBody UserSummary user) {
		return service.update(user);
	}

	@GetMapping("/{id}")
	public AppUser findById(@PathVariable(name = "id") Long id) {
		return service.findById(id);
	}
	
	
	@PostMapping("/roles")
	public Page<AppUser> findByRoles(@RequestBody List<String> roles,
									 @RequestParam(name = "page", defaultValue = "0") int page,
									 @RequestParam(name = "size", defaultValue = "20") int size) {
		return service.findByRoles(roles, page, size);
	}


	@DeleteMapping("/{id}")
	public void delete(@PathVariable(name = "id") Long id) {
		service.deleteBy(id);
	}

	@GetMapping()
	public Page<UserSummary> findAll(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "20") int size) {
		return service.findAll(page, size);
	}

	@GetMapping("/search")
	public List<AppUser> findByKeyword(
			@RequestParam(name = "keyword") String keyword) {
		return service.findByKeyword(keyword);
	}
	
	@PostMapping("/update-password/{userId}")
	public void updatePassword(@PathVariable(name = "userId") Long userId, @RequestBody String password) throws Exception {
		service.updatePassword(userId, password);
	}
	
}
