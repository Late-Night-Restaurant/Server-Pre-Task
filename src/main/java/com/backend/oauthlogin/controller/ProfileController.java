package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.dto.ProfileRequestDto;
import com.backend.oauthlogin.dto.ProfileResponseDto;
import com.backend.oauthlogin.dto.ProfileUpdateDto;
import com.backend.oauthlogin.entity.Profile;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.exception.BaseException;
import com.backend.oauthlogin.exception.BaseResponse;
import com.backend.oauthlogin.service.ProfileService;
import com.backend.oauthlogin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.backend.oauthlogin.exception.BaseResponseStatus.USERS_NOT_AUTHORIZED;



@Slf4j
@RestController
@RequestMapping("/users/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
//    private final AuthService authService;
    private final UserService userService;

    @PostMapping("")
    public BaseResponse<ProfileResponseDto> createProfile(@Valid @RequestBody ProfileRequestDto profileRequestDto) throws BaseException {

//        User user = authService.authenticateUser();  // 현재 접속한 유저
        User user = userService.getMyUserWithAuthorities().orElseThrow(
                () -> new BaseException(USERS_NOT_AUTHORIZED)
        );
        log.info("ProfileController - user: {}", user);
        if (user == null) {
            return new BaseResponse("존재하지 않는 사용자입니다.");   // TODO Custom Status ENUM 으로 만들어서 관리
        }

        try {
            profileRequestDto.setUserProfile(user);
            ProfileResponseDto profileResponseDto = profileService.createProfile(profileRequestDto);

            return new BaseResponse<>(profileResponseDto);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @PatchMapping("/{profileId}")
    public BaseResponse<String> updateProfile(@PathVariable("profileId") Long profileId, @RequestBody ProfileUpdateDto profileUpdateDto) {

        try {
            profileUpdateDto.setProfileId(profileId);
            profileService.updateProfile(profileUpdateDto);

            String result = "프로필 수정이 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @PatchMapping("/{profileId}/delete")
    public BaseResponse<String> deleteProfile(@PathVariable("profileId") Long profileId) {

        try {
            profileService.deleteProfile(profileId);
            String result = "프로필이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @PatchMapping("/{profileId}/main")
    public BaseResponse<String> setMainProfile(@PathVariable("profileId") Long profileId) {
        try {
            profileService.setMainProfile(profileId);
            String result = "메인 프로필이 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/{profileId}")
    public BaseResponse<Profile> getProfileInfo(@PathVariable("profileId") Long profileId) {
        try {
        return new BaseResponse<>(profileService.getProfileInfo(profileId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
