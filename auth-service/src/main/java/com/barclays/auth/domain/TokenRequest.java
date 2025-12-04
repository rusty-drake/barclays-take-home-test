package com.barclays.auth.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class TokenRequest {

    @NotBlank
    @Email
    private String email;

    public TokenRequest() {
    }

    public TokenRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static final class Builder {
        private String email;

        public static Builder create() {
            return new Builder();
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public TokenRequest build() {
            return new TokenRequest(email);
        }
    }
}