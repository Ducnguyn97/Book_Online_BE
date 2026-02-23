package vn.codegym.BE_BookOnline.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.codegym.BE_BookOnline.dto.request.LockUserRequest;
import vn.codegym.BE_BookOnline.dto.request.UnlockUserRequest;
import vn.codegym.BE_BookOnline.dto.response.CustomerListResponse;
import vn.codegym.BE_BookOnline.dto.response.UserProfile;
import vn.codegym.BE_BookOnline.service.UserService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor

public class AdminController {
    private final UserService userService;

    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerListResponse>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<CustomerListResponse> customers = userService.getAllCustomers(pageable);
        return ResponseEntity.ok(customers);
    }
    @PutMapping("/{userId}/lock")
    public ResponseEntity<UserProfile> lockUserAccount(@PathVariable Long userId,
                                                       @Valid @RequestBody LockUserRequest request) {
        UserProfile response = userService.lockUserAccount(userId, request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{userId}/unlock")
    public ResponseEntity<UserProfile> unlockUserAccount(@PathVariable Long userId,
                                                         @Valid @RequestBody UnlockUserRequest request) {
        UserProfile response = userService.unlockUserAccount(userId, request);
        return ResponseEntity.ok(response);
    }

}
