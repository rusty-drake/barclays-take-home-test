package com.barclays.auth.domain;


public class TokenResponse {

    private String accessToken;
    private final String tokenType = "Bearer";

    public TokenResponse() {
    }
    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public static final class Builder {
        private String accessToken;

        public Builder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }


        public TokenResponse build() {
            return new TokenResponse(accessToken);
        }
    }

}