package com.barclays.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id; 

    @NotBlank
    private String line1;

    private String line2;

    private String line3;

    @NotBlank
    private String town;

    @NotBlank
    private String county;

    @NotBlank
    private String postcode;

    public Address() {
    }

    public Address(String line1,
                   String line2,
                   String line3,
                   String town,
                   String county,
                   String postcode) {
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
    }

    // getters / setters

    public Long getId() {
        return id;
    }

    // Usually you don't set id manually, but we keep the setter for JPA / tests
    public void setId(Long id) {
        this.id = id;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    // -------- Builder --------

    public static final class Builder {
        private String line1;
        private String line2;
        private String line3;
        private String town;
        private String county;
        private String postcode;

        public static Builder create() {
            return new Builder();
        }

        public Builder withLine1(String line1) {
            this.line1 = line1;
            return this;
        }

        public Builder withLine2(String line2) {
            this.line2 = line2;
            return this;
        }

        public Builder withLine3(String line3) {
            this.line3 = line3;
            return this;
        }

        public Builder withTown(String town) {
            this.town = town;
            return this;
        }

        public Builder withCounty(String county) {
            this.county = county;
            return this;
        }

        public Builder withPostcode(String postcode) {
            this.postcode = postcode;
            return this;
        }

        public Address build() {
            return new Address(line1, line2, line3, town, county, postcode);
        }
    }
}
