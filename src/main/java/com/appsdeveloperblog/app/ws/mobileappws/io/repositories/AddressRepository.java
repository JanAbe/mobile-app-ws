package com.appsdeveloperblog.app.ws.mobileappws.io.repositories;

import java.util.List;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
	public List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
	public AddressEntity findByAddressId(String addressId);
}