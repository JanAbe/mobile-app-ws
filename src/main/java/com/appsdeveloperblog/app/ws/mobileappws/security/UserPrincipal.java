package com.appsdeveloperblog.app.ws.mobileappws.security;

import java.util.Collection;
import java.util.HashSet;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.AuthorityEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = -5959864244697728577L;
	private UserEntity userEntity;
	private String id;

	public UserPrincipal(UserEntity userEntity) {
		this.userEntity = userEntity;
		this.id = userEntity.getUserId();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new HashSet<>(); // shouldn't this actually be called roles? i guess not because it contains both roles and authorities. maybe it should be called role_and_authorities?
		Collection<AuthorityEntity> authorityEntities = new HashSet<>(); // and this authorities?

		Collection<RoleEntity> roles = this.userEntity.getRoles();
		if (roles == null) {
			return authorities;
		}

		// get all roles the user has (e.g ROLE_USER + ROLE_ADMIN)
		roles.forEach((role) -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			authorityEntities.addAll(role.getAuthorities());
		});

		// get all authorities that come with the roles the user has (e.g. WRITE_AUTHORITY + READ_AUTHORITY + DELETE_AUTHORITY)
		authorityEntities.forEach((authority) -> {
			authorities.add(new SimpleGrantedAuthority(authority.getName()));
		});

		return authorities;
	}

	@Override
	public String getPassword() {
		return this.userEntity.getEncryptedPassword();
	}

	@Override
	public String getUsername() {
		return this.userEntity.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.userEntity.getEmailVerificationStatus();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}