package com.example.application.data.reportbean;

import com.example.application.data.entity.Address;

import java.time.LocalDateTime;

public class AddressReportBean {
    private Integer addressId;
    private String address1;
    private String address2;
    private String district;
    private String cityName;
    private String postalCode;
    private String phoneNumber;
    private LocalDateTime lastUpdate;

    public AddressReportBean(Address address) {
        this.addressId = address.getAddressId();
        this.address1 = address.getAddress1();
        this.address2 = address.getAddress2();
        this.district = address.getDistrict();
        this.cityName = address.getAddress1() != null ? address.getCityId().getCityName(): "N/A";
        this.postalCode = address.getPostalCode();
        this.phoneNumber = address.getPhone();
        this.lastUpdate = address.getLastUpdate();


    }

    public Integer getAddressId() {
        return addressId;
    }
    public String getAddress1() {
        return address1;

    }
    public String getCityName() {
        return cityName;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
}
