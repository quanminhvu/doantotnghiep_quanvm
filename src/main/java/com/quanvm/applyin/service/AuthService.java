package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.AuthDtos.ForgotPasswordRequest;
import com.quanvm.applyin.dto.AuthDtos.JwtResponse;
import com.quanvm.applyin.dto.AuthDtos.LoginRequest;
import com.quanvm.applyin.dto.AuthDtos.RegisterRequest;
import com.quanvm.applyin.dto.AuthDtos.ResetPasswordRequest;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.entity.User.Role;
import com.quanvm.applyin.repository.UserRepository;
import com.quanvm.applyin.security.JwtService;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final JavaMailSender mailSender;

  @Transactional
  public void register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Email đã tồn tại");
    }
    Instant now = Instant.now();
    User user = User.builder()
        .fullName(request.fullName())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .role(Role.USER)
        .enabled(true)
        .createdAt(now)
        .updatedAt(now)
        .build();
    userRepository.save(user);
  }

  public JwtResponse login(LoginRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    } catch (BadCredentialsException ex) {
      throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
    }
    String token = jwtService.generateToken(request.email(), Map.of());
    return new JwtResponse(token);
  }

  @Transactional
  public void forgotPassword(ForgotPasswordRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
    user.setResetToken(UUID.randomUUID().toString());
    user.setResetTokenExpiry(Instant.now().plusSeconds(15 * 60));
    user.setUpdatedAt(Instant.now());
    userRepository.save(user);

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(user.getEmail());
    msg.setSubject("Reset mật khẩu");
    msg.setText("Token reset: " + user.getResetToken());
    try {
      mailSender.send(msg);
    } catch (Exception ignored) {
      // In development, mail may not be configured. We ignore and rely on logs.
    }
  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    User user = userRepository.findByResetToken(request.token())
        .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));
    if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(Instant.now())) {
      throw new IllegalArgumentException("Token đã hết hạn");
    }
    user.setPassword(passwordEncoder.encode(request.newPassword()));
    user.setResetToken(null);
    user.setResetTokenExpiry(null);
    user.setUpdatedAt(Instant.now());
    userRepository.save(user);
  }
}


