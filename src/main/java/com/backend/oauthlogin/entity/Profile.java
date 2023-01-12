package com.backend.oauthlogin.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "PROFILE")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile extends BaseTimeEntity {

    @Id
    @Column(name = "profile_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "comment", length = 100)
    private String comment;

    @Column(name = "picture")
    private String picture;

    @Column(name = "is_represent")
    private boolean isRepresent;

    @Column(name = "activated")
    private boolean activated;

}
