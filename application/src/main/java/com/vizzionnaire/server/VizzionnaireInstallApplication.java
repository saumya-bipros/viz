package com.vizzionnaire.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.vizzionnaire.server.install.VizzionnaireInstallService;

import java.util.Arrays;

@Slf4j
@SpringBootConfiguration
@ComponentScan({"com.vizzionnaire.server.install",
        "com.vizzionnaire.server.service.component",
        "com.vizzionnaire.server.service.install",
        "com.vizzionnaire.server.dao",
        "com.vizzionnaire.server.common.stats",
        "com.vizzionnaire.server.common.transport.config.ssl",
        "com.vizzionnaire.server.cache",
        "com.vizzionnaire.server.springfox"
})
public class VizzionnaireInstallApplication {

    private static final String SPRING_CONFIG_NAME_KEY = "--spring.config.name";
    private static final String DEFAULT_SPRING_CONFIG_PARAM = SPRING_CONFIG_NAME_KEY + "=" + "vizzionnaire";

    public static void main(String[] args) {
        try {
            SpringApplication application = new SpringApplication(VizzionnaireInstallApplication.class);
            application.setAdditionalProfiles("install");
            ConfigurableApplicationContext context = application.run(updateArguments(args));
            context.getBean(VizzionnaireInstallService.class).performInstall();
        } catch (Exception e) {
            log.error(e.getMessage());
            System.exit(1);
        }
    }

    private static String[] updateArguments(String[] args) {
        if (Arrays.stream(args).noneMatch(arg -> arg.startsWith(SPRING_CONFIG_NAME_KEY))) {
            String[] modifiedArgs = new String[args.length + 1];
            System.arraycopy(args, 0, modifiedArgs, 0, args.length);
            modifiedArgs[args.length] = DEFAULT_SPRING_CONFIG_PARAM;
            return modifiedArgs;
        }
        return args;
    }
}
