package com.exithere.rain;

import com.exithere.rain.entity.Region;
import com.exithere.rain.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
@EnableJpaAuditing
public class RainApplication {

    public static void main(String[] args) {

        SpringApplication.run(RainApplication.class, args);

    }

}
