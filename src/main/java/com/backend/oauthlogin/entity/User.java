package com.backend.oauthlogin.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.*;


import javax.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static javax.persistence.CascadeType.ALL;

@Entity    // @Entity 어노테이션: 자동으로 JPA 연동
@Table(name = "`USER`")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialLoginType socialLoginType;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "activated")
    private boolean activated;

    // 연관관계 메서드
    @Builder.Default
    @OneToMany(mappedBy = "profile", cascade = ALL, orphanRemoval = true)
    private List<Profile> profileList = new ArrayList<>();

//    @ManyToMany
//    @JoinTable(
//            name = "user_authority",
//            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
//            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
//    private Set<Authority> authorities;



    //== Spring Security 사용자 인증 필드 ==//

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> {
            return getRole().value();
        });
        return authorities;
    }

    /**
     * 계정 만료 여부
     * true : 만료 X
     * false : 만료 O
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부
     * true : 잠기지 않음
     * false : 잠김
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     * true : 만료 안됨
     * false : 만료
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자 활성화 여부
     * true : 활성화
     * false :
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}

// Profile -> 연관관계 매핑 고려해서 클래스 생성할 예정!