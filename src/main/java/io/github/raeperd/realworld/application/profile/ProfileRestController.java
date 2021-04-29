package io.github.raeperd.realworld.application.profile;

import io.github.raeperd.realworld.domain.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/profiles")
@RestController
public class ProfileRestController {

    private final ProfileService profileService;

    public ProfileRestController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{username}")
    public ProfileResponseDTO getProfile(@PathVariable String username) {
        return ProfileResponseDTO.fromProfile(
                profileService.viewProfileByUsername(username));
    }
}
