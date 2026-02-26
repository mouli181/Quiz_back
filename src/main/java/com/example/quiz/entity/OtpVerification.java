package com.example.quiz.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_verification")
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String otp;

    private LocalDateTime expiryTime;

    public OtpVerification() {}

    public OtpVerification(String email, String otp, LocalDateTime expiryTime) {
        this.email = email;
        this.otp = otp;
        this.expiryTime = expiryTime;
    }

    public Long getId() { return id; }

    public String getEmail() { return email; }

    public String getOtp() { return otp; }

    public LocalDateTime getExpiryTime() { return expiryTime; }

    public void setEmail(String email) { this.email = email; }

    public void setOtp(String otp) { this.otp = otp; }

    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }
}
