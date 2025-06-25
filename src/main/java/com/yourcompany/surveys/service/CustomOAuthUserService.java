package com.yourcompany.surveys.service;

import com.yourcompany.surveys.dto.user.OAuthUserInfo;
import com.yourcompany.surveys.entity.AuthProvider;
import com.yourcompany.surveys.entity.CustomOAuth2User;
import com.yourcompany.surveys.entity.Roles;
import com.yourcompany.surveys.entity.User;
import com.yourcompany.surveys.repository.RoleRepository;
import com.yourcompany.surveys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuthUserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider.fromProviderId(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no soportado: " + registrationId));

        OAuthUserInfo userInfo;

        if ("google".equals(registrationId)) {
            userInfo = new OAuthUserInfo(
                    oAuth2User.getAttribute("email"),
                    oAuth2User.getAttribute("given_name"),
                    oAuth2User.getAttribute("family_name"),
                    oAuth2User.getAttribute("picture")
            );
        } else if ("facebook".equals(registrationId)) {
            String name = oAuth2User.getAttribute("name");
            String givenName = name != null ? name.split(" ")[0] : "";
            String familyName = name != null && name.contains(" ") ? name.split(" ", 2)[1] : "";
            String email = oAuth2User.getAttribute("email");
            String picture = extractFacebookPicture(oAuth2User);

            userInfo = new OAuthUserInfo(email, givenName, familyName, picture);
        } else {
            throw new OAuth2AuthenticationException("Proveedor no soportado: " + registrationId);
        }

        final String regIdFinal = registrationId;
        User user = userRepository.findByEmail(userInfo.email())
                .orElseGet(() -> createdNewUser(userInfo, regIdFinal));

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private String extractFacebookPicture(OAuth2User oAuth2User) {
        Object pictureObj = oAuth2User.getAttribute("picture");
        if (!(pictureObj instanceof Map<?, ?> pictureMap)) return null;

        Object dataObj = pictureMap.get("data");
        if (!(dataObj instanceof Map<?, ?> dataMap)) return null;

        Object url = dataMap.get("url");
        return url != null ? url.toString() : null;
    }

    private User createdNewUser(OAuthUserInfo info, String registrationId) {
        var userRole = roleRepository.findByName(Roles.USER.name())
                .orElseThrow(() -> new IllegalStateException("ROL " + Roles.USER.name() + " no encontrado"));

        return userRepository.save(User.builder()
                .username(info.email().split("@")[0])
                .email(info.email())
                .firstName(info.givenName())
                .lastName(info.familyName())
                .profilePictureUrl(info.picture())
                .enabled(true)
                .accountLocked(false)
                .provider(AuthProvider.valueOf(registrationId.toUpperCase()))
                .roles(List.of(userRole))
                .build());
    }

}
