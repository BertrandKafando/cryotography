package es.kf.signapp.security.user.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "T_ROLE")
public class AppRole {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@ManyToMany
	@JoinTable(name = "ROLE_PERMISSION", joinColumns = {@JoinColumn(name = "ROLE_ID")}, inverseJoinColumns = {
			@JoinColumn(name = "PERMISSION_ID")})
	private Collection<AppPermission> permissions = new ArrayList<AppPermission>();

	public void addPermission(AppPermission permission) {
		if(!this.permissions.contains(permission)) {
			this.permissions.add(permission);
		}
	}
	
	public void removePermission(AppPermission permission) {
		this.permissions.remove(permission);
	}
}
