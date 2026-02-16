package com.greenko.gatewayservice.auth;

public record TokenResponse(String token, String tokenType, long expiresInSeconds) {
}

