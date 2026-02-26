package com.example.quiz.service;

import com.example.quiz.dto.AuthResponse;
import com.example.quiz.dto.LoginRequest;
import com.example.quiz.dto.RegisterRequest;
import com.example.quiz.entity.OtpVerification;
import com.example.quiz.entity.User;
import com.example.quiz.repository.OtpRepository;
import com.example.quiz.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpRepository otpRepository;
    private final EmailService emailService;


    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService, OtpRepository otpRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    // ✅ REGISTER
    public String register(RegisterRequest request) {

        User existingUser = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (existingUser != null) {

            if (existingUser.isVerified()) {
                throw new RuntimeException("Email already registered");
            }

            // If user exists but NOT verified → resend OTP
            String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

            OtpVerification otpEntity = new OtpVerification(
                    existingUser.getEmail(),
                    otp,
                    java.time.LocalDateTime.now().plusMinutes(5)
            );

            otpRepository.deleteByEmail(existingUser.getEmail());
            otpRepository.save(otpEntity);

            emailService.sendOtp(existingUser.getEmail(), otp);

            return "OTP resent to your email";
        }


        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setVerified(false);

        userRepository.save(user);

        // Generate OTP
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        OtpVerification otpEntity = new OtpVerification(
                user.getEmail(),
                otp,
                java.time.LocalDateTime.now().plusMinutes(5)
        );

        otpRepository.deleteByEmail(user.getEmail());
        otpRepository.save(otpEntity);

        emailService.sendOtp(user.getEmail(), otp);

        return "OTP sent to your email";
    }


    // ✅ LOGIN
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your email first");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token, user.getUsername());
    }

    public String verifyOtp(String email, String otp) {

        OtpVerification otpEntity = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!otpEntity.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (otpEntity.getExpiryTime().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        otpRepository.deleteByEmail(email);

        return "Email verified successfully";
    }

    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified");
        }

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        OtpVerification otpEntity = new OtpVerification(
                email,
                otp,
                java.time.LocalDateTime.now().plusMinutes(5)
        );

        otpRepository.deleteByEmail(email);
        otpRepository.save(otpEntity);

        emailService.sendOtp(email, otp);

        return "Reset OTP sent to email";
    }

    public String resetPassword(String email, String otp, String newPassword) {

        OtpVerification otpEntity = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!otpEntity.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (otpEntity.getExpiryTime().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRepository.deleteByEmail(email);

        return "Password reset successful";
    }




}
