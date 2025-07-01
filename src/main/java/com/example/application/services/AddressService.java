package com.example.application.services;

import com.example.application.data.entity.Address;
import com.example.application.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Page<Address> list(Pageable pageable) {
        return addressRepository.findAllWithCityAndCountry(pageable);
    }

    public long count() {
        return addressRepository.count();
    }

    public Optional<Address> getAddressById(Integer id) {
        return addressRepository.findByIdWithCityAndCountry(id);
    }

    public Address saveAddress(Address address) {
        if (address.getLastUpdate() == null) {
            address.setLastUpdate(LocalDateTime.now());
        } else {
            address.setLastUpdate(address.getLastUpdate());
        }
        return addressRepository.save(address);
    }

    public void deleteAddress(Integer addressId) {
        addressRepository.deleteById(addressId);
    }


}
