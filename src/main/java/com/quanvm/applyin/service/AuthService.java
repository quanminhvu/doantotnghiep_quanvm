package com.quanvm.applyin.service;

import com.quanvm.applyin.dto.AuthDtos.*;
import com.quanvm.applyin.entity.Device;
import com.quanvm.applyin.entity.User;
import com.quanvm.applyin.exception.*;
import com.quanvm.applyin.repository.DeviceRepository;
import com.quanvm.applyin.repository.UserRepository;
import com.quanvm.applyin.security.JwtService;
import com.quanvm.applyin.util.constant.UserEnum;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final DeviceRepository deviceRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final JavaMailSender mailSender;

  @Transactional
  public void register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyExistsException("Email đã tồn tại trong hệ thống");
    }
    Instant now = Instant.now();
    UserEnum.Role roleToAssign = UserEnum.Role.CANDIDATE;
    if (request.role() != null && !request.role().isBlank()) {
      String requestedRole = request.role().trim().toUpperCase();
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      boolean isAdmin = auth != null && auth.isAuthenticated() && auth.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .anyMatch(a -> a.equals("ROLE_ADMIN"));
      try {
        UserEnum.Role parsed = UserEnum.Role.valueOf(requestedRole);
        if (isAdmin) {
          roleToAssign = parsed;
        } else if (parsed == UserEnum.Role.RECRUITER) {
          roleToAssign = UserEnum.Role.RECRUITER;
        }
      } catch (IllegalArgumentException ignored) {
      }
    }
    User user = User.builder()
        .fullName(request.fullName())
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .role(roleToAssign)
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
    
    User user = userRepository.findByEmail(request.email()).orElseThrow();
    String token = jwtService.generateToken(request.email(), Map.of());
    
    // Xử lý device information
    Device device = handleDeviceLogin(user, request);
    
    // Tạo DeviceInfo cho response
    DeviceInfo deviceInfo = new DeviceInfo(
        device.getId(),
        device.getDeviceId(),
        device.getDeviceName(),
        device.getPlatform().name(),
        device.getOsVersion(),
        device.getAppVersion(),
        device.isActive()
    );
    
    return new JwtResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), deviceInfo);
  }

  @Transactional
  private Device handleDeviceLogin(User user, LoginRequest request) {
    Instant now = Instant.now();
    
    Device device = deviceRepository.findByUserAndDeviceId(user, request.deviceId())
        .orElse(createNewDevice(user, request, now));
    
    updateDeviceInfo(device, request, now);
    return deviceRepository.save(device);
  }

  private Device createNewDevice(User user, LoginRequest request, Instant now) {
    return Device.builder()
        .user(user)
        .deviceId(request.deviceId())
        .deviceToken(request.deviceToken())
        .platform(parsePlatform(request.platform()))
        .deviceName(request.deviceName())
        .osVersion(request.osVersion())
        .appVersion(request.appVersion())
        .isActive(true)
        .lastLoginAt(now)
        .createdAt(now)
        .updatedAt(now)
        .build();
  }

  private void updateDeviceInfo(Device device, LoginRequest request, Instant now) {
    device.setDeviceToken(request.deviceToken());
    device.setPlatform(parsePlatform(request.platform()));
    device.setDeviceName(request.deviceName());
    device.setOsVersion(request.osVersion());
    device.setAppVersion(request.appVersion());
    device.setActive(true);
    device.setLastLoginAt(now);
    device.setUpdatedAt(now);
  }

  private Device.Platform parsePlatform(String platform) {
    if (platform == null || platform.isBlank()) {
      return Device.Platform.WEB; // Default platform
    }
    
    try {
      return Device.Platform.valueOf(platform.toUpperCase());
    } catch (IllegalArgumentException e) {
      return Device.Platform.WEB; // Fallback to WEB
    }
  }

  @Transactional
  public void forgotPassword(ForgotPasswordRequest request) {
    User user = getUserByEmail(request.email());
    String otpCode = generateOtpCode();
    updateUserOtp(user, otpCode, Instant.now().plusSeconds(5 * 60));
    sendOtpEmail(user.getEmail(), user.getFullName(), otpCode);
  }

  private String generateOtpCode() {
    Random random = new Random();
    int otp = 100000 + random.nextInt(900000);
    return String.valueOf(otp);
  }

  private void updateUserOtp(User user, String otpCode, Instant otpExpiry) {
    user.setOtpCode(otpCode);
    user.setOtpExpiry(otpExpiry);
    user.setUpdatedAt(Instant.now());
    userRepository.save(user);
  }

  private void sendOtpEmail(String email, String fullName, String otpCode) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(email);
      helper.setSubject("🔐 Mã OTP đặt lại mật khẩu - ApplyIn");
      helper.setText(buildOtpEmailHtmlContent(fullName, otpCode), true);

      mailSender.send(message);
    } catch (Exception e) {

      System.err.println("Failed to send OTP email: " + e.getMessage());
    }
  }

  private String buildOtpEmailHtmlContent(String fullName, String otpCode) {
    return String.format("""
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Mã OTP - ApplyIn</title>
            <style>%s</style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🔐 ApplyIn</h1>
                    <p>Mã xác thực OTP</p>
                </div>
                <div class="content">
                    <div class="greeting">Xin chào <strong>%s</strong>,</div>
                    <div class="message">
                        Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản ApplyIn của mình. 
                        Để hoàn tất quá trình này, vui lòng sử dụng mã OTP bên dưới:
                    </div>
                    <div class="otp-container">
                        <div class="otp-label">Mã OTP của bạn:</div>
                        <div class="otp-code">%s</div>
                    </div>
                    <div class="warning">
                        <span class="warning-icon">⚠️</span>
                        <strong>Lưu ý quan trọng:</strong> Mã này sẽ hết hạn sau <strong>5 phút</strong> 
                        và chỉ có thể sử dụng <strong>một lần duy nhất</strong>.
                    </div>
                    <div class="security-note">
                        <p><strong>🛡️ Bảo mật:</strong> Nếu bạn không yêu cầu đặt lại mật khẩu, 
                        vui lòng bỏ qua email này và kiểm tra tài khoản của bạn.</p>
                    </div>
                </div>
                <div class="footer">
                    <p><strong>Đội ngũ ApplyIn</strong></p>
                    <p>Hệ thống quản lý ứng tuyển việc làm</p>
                    <p>Email này được gửi tự động, vui lòng không trả lời.</p>
                </div>
            </div>
        </body>
        </html>
        """, getEmailStyles(), fullName, otpCode);
  }

  private String getEmailStyles() {
    return """
        body{font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;line-height:1.6;color:#333;max-width:600px;margin:0 auto;padding:20px;background-color:#f8f9fa}
        .container{background:white;border-radius:12px;box-shadow:0 4px 6px rgba(0,0,0,0.1);overflow:hidden}
        .header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;text-align:center}
        .header h1{margin:0;font-size:28px;font-weight:600}
        .header p{margin:10px 0 0 0;opacity:0.9;font-size:16px}
        .content{padding:40px 30px}
        .greeting{font-size:18px;margin-bottom:20px;color:#2c3e50}
        .message{font-size:16px;margin-bottom:30px;color:#555}
        .otp-container{text-align:center;margin:30px 0}
        .otp-code{display:inline-block;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;font-size:32px;font-weight:bold;padding:20px 30px;border-radius:8px;letter-spacing:8px;box-shadow:0 4px 15px rgba(102,126,234,0.3);margin:20px 0}
        .otp-label{font-size:14px;color:#666;margin-bottom:10px}
        .warning{background:#fff3cd;border:1px solid #ffeaa7;border-radius:6px;padding:15px;margin:20px 0;color:#856404}
        .warning-icon{font-size:18px;margin-right:8px}
        .footer{background:#f8f9fa;padding:20px 30px;text-align:center;border-top:1px solid #e9ecef}
        .footer p{margin:5px 0;color:#666;font-size:14px}
        .security-note{background:#e8f4fd;border-left:4px solid #2196F3;padding:15px;margin:20px 0;border-radius:0 6px 6px 0}
        .security-note p{margin:0;color:#1976D2;font-size:14px}
        """;
  }

  @Transactional
  public void verifyOtp(VerifyOtpRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new UserNotFoundException("Email không tồn tại trong hệ thống"));

    if (user.getOtpCode() == null || !user.getOtpCode().equals(request.otpCode())) {
      throw new InvalidTokenException("Mã OTP không đúng");
    }

    if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(Instant.now())) {
      throw new OtpExpiredException("Mã OTP đã hết hạn, vui lòng yêu cầu mã mới");
    }


    user.setOtpCode(null);
    user.setOtpExpiry(null);
    user.setUpdatedAt(Instant.now());
    userRepository.save(user);
  }

  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    User user = getUserByEmail(request.email());
    
    if (user.getOtpCode() != null) {
      throw new OtpNotVerifiedException("Vui lòng xác thực OTP trước khi đặt lại mật khẩu");
    }

    user.setPassword(passwordEncoder.encode(request.newPassword()));
    user.setUpdatedAt(Instant.now());
    userRepository.save(user);
  }

  @Transactional
  public void logout(LogoutRequest request, String userEmail) {
    User user = getUserByEmail(userEmail);
    Device device = deviceRepository.findByUserAndDeviceId(user, request.deviceId())
        .orElseThrow(() -> new IllegalArgumentException("Thiết bị không tồn tại"));
    
    // Cập nhật deviceToken nếu được cung cấp
    if (request.deviceToken() != null && !request.deviceToken().isEmpty()) {
      device.setDeviceToken(request.deviceToken());
      device.setUpdatedAt(Instant.now());
    }
    
    deactivateDevice(device);
    deviceRepository.save(device);
  }

  @Transactional
  public void logoutAllDevices(String userEmail) {
    User user = getUserByEmail(userEmail);
    deviceRepository.findByUser(user).forEach(this::deactivateDevice);
    deviceRepository.saveAll(deviceRepository.findByUser(user));
  }

  private User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("Email không tồn tại trong hệ thống"));
  }

  private void deactivateDevice(Device device) {
    device.setActive(false);
    device.setUpdatedAt(Instant.now());
  }
}


