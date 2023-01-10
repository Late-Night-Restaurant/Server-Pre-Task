package com.backend.oauthlogin.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "authority")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    // TODO 삭제할 엔티티
    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;
}
