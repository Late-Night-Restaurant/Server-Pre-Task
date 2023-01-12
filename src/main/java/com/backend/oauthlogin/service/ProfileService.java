package com.backend.oauthlogin.service;

import com.backend.oauthlogin.dto.ProfileDeleteDto;
import com.backend.oauthlogin.dto.ProfileRequestDto;
import com.backend.oauthlogin.dto.ProfileResponseDto;
import com.backend.oauthlogin.dto.ProfileUpdateDto;
import com.backend.oauthlogin.entity.Profile;
import com.backend.oauthlogin.exception.BaseException;
import com.backend.oauthlogin.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.oauthlogin.exception.BaseResponseStatus.DATABASE_ERROR;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional
    public ProfileResponseDto createProfile(ProfileRequestDto profileRequestDto) throws BaseException {
        Profile profile = profileRequestDto.toEntity();
        profile.getUser().addProfile(profile);
        Long profileId = profileRepository.save(profile).getProfileId();

        return new ProfileResponseDto(profileId, profile.getNickname(), profile.getUser());
    }

    @Transactional
    public void updateProfile(ProfileUpdateDto profileUpdateDto) {
        Profile profile = profileRepository.findById(profileUpdateDto.getProfileId()).orElseThrow();
        profile.update(profileUpdateDto);
    }

    // 대표 프로필은 하나만 지정 가능
    public void setMainProfile(Profile profile) {
        if (profile.isMainProfile() == -1) {  // 대표 프로필로 지정된 것이 없다면 바로 선택
            profile.selectMainProfile();
        } else {
            int idx = profile.isMainProfile();  // 이미 대표 프로필이 존재한다면 해제 후 선택
            profile.getUser().getProfileList().get(idx).cancelMainProfile();
            profile.selectMainProfile();;
        }
    }

    @Transactional
    public void deleteProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId).orElseThrow();
        profile.delete(profileId);
    }
}
