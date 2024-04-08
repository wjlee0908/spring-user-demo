package com.woojin.userdemo.actuator;

import com.woojin.userdemo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Actuactor - /info 에서 응답할 데이터
 */
@Component
@RequiredArgsConstructor
public class UserCountInfoContributor implements InfoContributor {
    private final UserRepository userRepository;

    @Override
    public void contribute(Info.Builder builder) {
        long userCount = userRepository.count();
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("count", userCount);
        builder.withDetail("user", userMap);
    }
}
