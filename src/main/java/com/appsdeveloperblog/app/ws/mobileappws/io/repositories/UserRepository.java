package com.appsdeveloperblog.app.ws.mobileappws.io.repositories;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
	public UserEntity findByEmail(String email);
	public UserEntity findByUserId(String userId);
}