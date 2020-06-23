package com.appsdeveloperblog.app.ws.mobileappws;

import java.util.Arrays;
import java.util.Collection;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.AuthorityEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.RoleEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.repositories.AuthorityRepository;
import com.appsdeveloperblog.app.ws.mobileappws.io.repositories.RoleRepository;
import com.appsdeveloperblog.app.ws.mobileappws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.mobileappws.shared.Utils;
import com.appsdeveloperblog.app.ws.mobileappws.shared.Roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InitialUserSetup {

	@Autowired
	AuthorityRepository authorityRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Transactional // because the method contains multiple modifying queries
	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		AuthorityEntity readAuthority = this.createAuthority("READ_AUTHORITY");
		AuthorityEntity writeAuthority = this.createAuthority("WRITE_AUTHORITY");
		AuthorityEntity deleteAuthority = this.createAuthority("DELETE_AUTHORITY");

		createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
		RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));

		if (roleAdmin == null) {
			return;
		}

		UserEntity adminUser = new UserEntity();
		adminUser.setFirstName("admin");
		adminUser.setLastName("admin");
		adminUser.setEmail("admin");
		adminUser.setEmailVerificationStatus(true);
		adminUser.setUserId(utils.generateUserId(30));
		adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("admin"));
		adminUser.setRoles(Arrays.asList(roleAdmin));

		userRepository.save(adminUser);
	}

	@Transactional
	private AuthorityEntity createAuthority(String name) {
		AuthorityEntity authority = authorityRepository.findByName(name);
		if (authority == null) {
			authority = new AuthorityEntity(name);
			authorityRepository.save(authority);
		}

		return authority;
	}

	@Transactional
	private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
		RoleEntity role = roleRepository.findByName(name);
		if (role == null) {
			role = new RoleEntity(name);
			role.setAuthorities(authorities);
			roleRepository.save(role);
		}

		return role;
	}
}