package com.backend.oauthlogin.service;

import com.backend.oauthlogin.dto.ProfileDeleteDto;
import com.backend.oauthlogin.dto.ProfileRequestDto;
import com.backend.oauthlogin.dto.ProfileResponseDto;
import com.backend.oauthlogin.dto.ProfileUpdateDto;
import com.backend.oauthlogin.entity.Profile;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.exception.BaseException;
import com.backend.oauthlogin.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.oauthlogin.exception.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
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
        Profile profile = getProfileInfo(profileUpdateDto.getProfileId());
        profile.update(profileUpdateDto);
    }

    // 대표 프로필은 하나만 지정 가능
    public void setMainProfile(Long profileId) {
        Profile profile = getProfileInfo(profileId);
        User user = profile.getUser();

        int idx = user.getMainProfile();  // 이미 대표 프로필이 존재한다면 해제 후 선택
        log.info("현재 유저의 main profile index: {}", idx);

        if (idx == -1) {  // 대표 프로필로 지정된 것이 없다면 바로 선택
            profile.selectMainProfile();
            log.info("변경 후 main profile index: {}", user.getMainProfile());
        } else {
            user.getProfileList().get(idx).cancelMainProfile();
            profile.selectMainProfile();
        }
    }

    @Transactional
    public void deleteProfile(Long profileId) {
        Profile profile = getProfileInfo(profileId);
        profile.delete(profileId);
    }

    public Profile getProfileInfo(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow();
    }
}
