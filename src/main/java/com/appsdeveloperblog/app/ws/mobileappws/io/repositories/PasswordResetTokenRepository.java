package com.appsdeveloperblog.app.ws.mobileappws.io.repositories;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.PasswordResetTokenEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// need a repository to persist an entity into the database (spring jpa)
@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
	public PasswordResetTokenEntity findByToken(String token);	
}