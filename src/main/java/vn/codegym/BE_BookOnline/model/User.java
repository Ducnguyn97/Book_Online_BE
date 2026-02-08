package vn.codegym.BE_BookOnline.model;

import jakarta.persistence.*;
import lombok.*;
import vn.codegym.BE_BookOnline.model.Enum.AuthProvider;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"cart", "reviews", "wishLists", "addresses", "roles"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "reject_reason", columnDefinition = "TEXT", length = 500)
    private String rejectReason;

    private LocalDateTime lockedAt;

    @Builder.Default// thêm cái này để mặc định giá trị khi dùng builder
    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;

    @Builder.Default
    private boolean emailVerified =false;

    private String verificationCode;

    private LocalDateTime expiredAt;

    private String resetPasswordToken;

    private LocalDateTime resetPasswordExpiredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthProvider authProvider;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Wishlist> wishLists;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Address> addresses;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_role"))
    private List<Role> roles;

    public void lockUser(String reason) {
        if (!this.enabled) {
            throw new IllegalStateException("User đã bị khóa rồi");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Lý do khóa không được để trống");
        }
        this.rejectReason = reason;
        this.enabled = false;
        this.lockedAt = LocalDateTime.now();
    }
    public void unlockUser() {
        if (this.enabled) {
            throw new IllegalStateException("User chưa bị khóa");
        }
        this.rejectReason = null;
        this.enabled = true;
        this.lockedAt = null;
    }
}
