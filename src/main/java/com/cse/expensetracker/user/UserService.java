package com.cse.expensetracker.user;

import com.cse.expensetracker.exception.ResourceNotFoundException;
import com.cse.expensetracker.exception.BadRequestException;
import com.cse.expensetracker.user.dto.ChangePasswordRequest;
import com.cse.expensetracker.user.dto.UpdateProfileRequest;
import com.cse.expensetracker.user.dto.UserProfileResponse;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String name, String email, String rawPassword) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.USER)
                .enabled(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserProfileResponse getProfile(String email) {
        User user = getByEmail(email);
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = getByEmail(email);
        user.setName(request.getName());
        user.setUpdatedAt(Instant.now());
        User saved = userRepository.save(user);
        return UserProfileResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .build();
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }
}
