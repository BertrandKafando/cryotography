package es.kf.signapp.security.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSummary {
	private long id;
	private String username;
	private String firstName;
	private String lastName;
	private String phone;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	private String  email;
	private String cin;
	private  boolean enabled;
	private List<AppRoleDTO> roles;
	private String name;
}
