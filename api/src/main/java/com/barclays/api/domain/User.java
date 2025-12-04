package com.barclays.api.domain;

import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Valid
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @NotBlank
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$")
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Calendar createdTimestamp;

    @CreationTimestamp
    @Column(nullable = false)
    private Calendar updatedTimestamp;

    public User() {
    }

    public User(String name, Address address, String phoneNumber, String email) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // // ---- lifecycle callbacks ----

    // @PrePersist
    // protected void onCreate() {
    //     OffsetDateTime now = OffsetDateTime.now();
    //     if (this.createdTimestamp == null) {
    //         this.createdTimestamp = now;       // set once
    //     }
    //     this.updatedTimestamp = now;           // also set on create
    // }

    // @PreUpdate
    // protected void onUpdate() {
    //     this.updatedTimestamp = OffsetDateTime.now(); // update on each change
    // }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Calendar getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Calendar createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Calendar getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(Calendar updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    // -------- Builder --------

    public static final class Builder {
        private String name;
        private Address address;
        private String phoneNumber;
        private String email;

        public static Builder create() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAddress(Address address) {
            this.address = address;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public User build() {
            return new User(name, address, phoneNumber, email);
        }
    }
}
