package com.woojin.userdemo.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Spring Actuator의 /health에 응답할 정보
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        int hour = 1;

        // 외부 서비스를 사용 가능한 지 확인한다

        return Health.outOfService().withDetail("reason", "External service is out of service")
                .withDetail("hour", hour)
                .build();
    }
}
