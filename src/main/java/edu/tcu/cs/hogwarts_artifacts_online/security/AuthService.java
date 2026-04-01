package edu.tcu.cs.hogwarts_artifacts_online.security;

import edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.HogwartsUser;
import edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.MyUserPrinciple;
import edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.converter.UserToUserDtoConverter;
import edu.tcu.cs.hogwarts_artifacts_online.hogwartsuser.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserToUserDtoConverter userToUserDtoConverter;
    private final JwtProvider jwtProvider;

    public AuthService(UserToUserDtoConverter userToUserDtoConverter, JwtProvider jwtProvider) {
        this.userToUserDtoConverter = userToUserDtoConverter;
        this.jwtProvider = jwtProvider;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        MyUserPrinciple principle = (MyUserPrinciple) authentication.getPrincipal();
        HogwartsUser hogwartsUser = principle.getUser();
        UserDto userDto = this.userToUserDtoConverter.convert(hogwartsUser);

        String token = this.jwtProvider.createToken(authentication);
        Map<String, Object> loginResultMap = new HashMap<>();

        loginResultMap.put("userInfo", userDto);
        loginResultMap.put("token",token);
        return loginResultMap;
    }
}
