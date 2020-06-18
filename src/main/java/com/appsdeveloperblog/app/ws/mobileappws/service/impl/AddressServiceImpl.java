package com.appsdeveloperblog.app.ws.mobileappws.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.appsdeveloperblog.app.ws.mobileappws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.mobileappws.io.repositories.AddressRepository;
import com.appsdeveloperblog.app.ws.mobileappws.io.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.mobileappws.service.AddressService;
import com.appsdeveloperblog.app.ws.mobileappws.shared.dto.AddressDto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AddressRepository addressRepository;

	@Override
	public List<AddressDto> getAddresses(String userId) {
		List<AddressDto> addresses = new ArrayList<>();

		UserEntity userEntity = this.userRepository.findByUserId(userId);
		if (userEntity == null) {
			return addresses;
		}

		Iterable<AddressEntity> foundAddresses = this.addressRepository.findAllByUserDetails(userEntity);
		ModelMapper modelMapper = new ModelMapper();
		for (AddressEntity address : foundAddresses) {
			addresses.add(modelMapper.map(address, AddressDto.class));
		}

		return addresses;
	}

	@Override
	public AddressDto getAddress(String addressId) {
		AddressDto address = new AddressDto();

		AddressEntity addressEntity = this.addressRepository.findByAddressId(addressId);
		if (addressEntity != null) {
			address = new ModelMapper().map(addressEntity, AddressDto.class);
		}

		return address;
	}
		
}