package es.kf.signapp.security.user.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_USER")
@DynamicUpdate
public class AppUser implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "USERNAME", unique = true)
	private String username;

	@Column(name = "PASSWORD")
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

	@Column(name = "FISRT_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "PHONE", unique = true)
	private String phone;

	@Column(name = "EMAIL",unique=true)
	private String email;

	@Column(name = "CIN")
	private String cin;
	
	@Column(name = "ENABLED")
	private boolean enabled;


	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "USER_ROLE", joinColumns = {
			@JoinColumn(name = "USER_ID")}, inverseJoinColumns = {
					@JoinColumn(name = "ROLE_ID")})
	private Collection<AppRole> roles = new ArrayList<AppRole>();

	
	@JsonProperty(access = Access.READ_ONLY)
	public String getName() {
		return lastName + " " + firstName;
	}
	
	public String getPhone() {
		return StringUtils.defaultIfBlank(phone, null);
	}

	@JsonProperty(access = Access.READ_ONLY)
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		roles.forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		});
		return authorities;
	}
}
