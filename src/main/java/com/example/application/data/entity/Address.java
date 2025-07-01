package com.example.application.data.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    @Column(name="address", nullable = false, length = 50)
    private String address1;

    @Column(name="address2", nullable = false, length = 50)
    private String address2;

    @Column(name="district", nullable = false, length = 20)
    private String district;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", nullable = false)
    private City cityId;

    @Column(name="postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(name="phone", nullable = false, length = 20)
    private String phone;

    @Column(name="last_update", nullable = false)
    private LocalDateTime lastUpdate;

    public Address() {}

    public Address(String address1,  String address2, String district, City cityId, String postalCode, String phone) {
        this.address1 = address1;
        this.address2 = address2;
        this.district = district;
        this.cityId = cityId;
        this.postalCode = postalCode;
        this.phone = phone;
        this.lastUpdate = LocalDateTime.now();

    }
    public Integer getAddressId() {
        return addressId;
    }
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address) {
        this.address1 = address1;
    }
    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }
    public City getCityId() {
        return cityId;
    }
    public void setCityId(City cityId) {
        this.cityId = cityId;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    @Override
    public String toString() {
        return "Address{"+
                "addressId=" + addressId +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", district='" + district + '\'' +
                ", city=" + (cityId != null ? cityId.getCityName() : "null") +
                ", postalCode='" + postalCode + '\'' +
                ", phone='" + phone + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
