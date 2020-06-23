package com.appsdeveloperblog.app.ws.mobileappws.io.repositories;

import java.util.List;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
	public UserEntity findByEmail(String email);
	public UserEntity findByUserId(String userId);
	public UserEntity findUserByEmailVerificationToken(String token);

	@Query(
		value = "SELECT * FROM Users u WHERE u.email_verification_status = 1", 
		countQuery = "SELECT COUNT(*) FROM Users u WHERE u.email_verification_status = 1;", // count query wouldn't be used if the return value would not be pageable.
		nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);

	@Query(value = "SELECT * FROM Users u WHERE u.first_name = ?1", nativeQuery = true)
	List<UserEntity> findUserByFirstName(String firstName);
	
	@Query(value = "SELECT * FROM Users u WHERE u.last_name = :lastName", nativeQuery = true)
	List<UserEntity> findUserByLastName(@Param("lastName") String lastName);

	@Query(value="select * from Users u where first_name LIKE %:keyword% or last_name LIKE %:keyword%",nativeQuery=true)
	List<UserEntity> findUsersByKeyword(@Param("keyword") String keyword);

	@Query(value="select u.first_name, u.last_name from Users u where u.first_name LIKE %:keyword% or u.last_name LIKE %:keyword%",nativeQuery=true)
	List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword);

	@Transactional // will automatically roll back changes if an error has occured during the execution of this query
	@Modifying // because the query modifies data in the database
	@Query(value = "UPDATE users u SET u.email_verification_status = :emailVerificationStatus WHERE u.user_id = :userId", nativeQuery = true) // can't place a semicolon after a namedparameter becaues then it thinks it is part of that names parameter instead of indicating end of line;
	void updateUserEmailVerificationStatus(
		@Param("emailVerificationStatus") boolean emailVerificationStatus, 
		@Param("userId") String userId
	);

	// jpql queries use the entity class name and fields to query, and not the database table details.
	@Query("SELECT User FROM UserEntity User WHERE User.userId = :userId")
	UserEntity findUserEntityByUserId(@Param("userId") String userId);

	@Query("SELECT user.firstName, user.lastName FROM UserEntity user WHERE user.userId = :userId")
	List<Object[]> getUserEntityFullNamebyId(@Param("userId") String userId);

	@Transactional
	@Modifying
	@Query("UPDATE UserEntity user SET user.emailVerificationStatus = :emailVerificationStatus WHERE user.userId = :userId")
	void updateUserEntityEmailVerificationStatus(
		@Param("emailVerificationStatus") boolean emailVerificationStatus,
		@Param("userId") String userId
	);
}