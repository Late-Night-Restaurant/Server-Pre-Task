package com.backend.oauthlogin.service;

import com.backend.oauthlogin.dto.ProfileRequestDto;
import com.backend.oauthlogin.dto.ProfileResponseDto;
import com.backend.oauthlogin.entity.Profile;
import com.backend.oauthlogin.exception.BaseException;
import com.backend.oauthlogin.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional
    public ProfileResponseDto createProfile(ProfileRequestDto profileRequestDto) throws BaseException {
        Profile profile = profileRequestDto.toEntity();
        profile.getUser().addProfile(profile);
        Long profileId = profileRepository.save(profile).getProfileId();

        return new ProfileResponseDto(profileId, profile.getNickname());
    }
}
