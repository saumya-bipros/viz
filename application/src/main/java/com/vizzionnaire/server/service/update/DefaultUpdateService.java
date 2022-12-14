package com.vizzionnaire.server.service.update;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vizzionnaire.common.util.VizzionnaireThreadFactory;
import com.vizzionnaire.server.common.data.UpdateMessage;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@TbCoreComponent
@Slf4j
public class DefaultUpdateService implements UpdateService {

    private static final String INSTANCE_ID_FILE = ".instance_id";
    private static final String UPDATE_SERVER_BASE_URL = "https://updates.vizzionnaire.io";

    private static final String PLATFORM_PARAM = "platform";
    private static final String VERSION_PARAM = "version";
    private static final String INSTANCE_ID_PARAM = "instanceId";

    @Value("${updates.enabled}")
    private boolean updatesEnabled;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, VizzionnaireThreadFactory.forName("tb-update-service"));

    private ScheduledFuture checkUpdatesFuture = null;
    private RestTemplate restClient = new RestTemplate();

    private UpdateMessage updateMessage;

    private String platform;
    private String version;
    private UUID instanceId = null;

    @PostConstruct
    private void init() {
        updateMessage = new UpdateMessage("", false);
        if (updatesEnabled) {
            try {
                platform = System.getProperty("platform", "unknown");
                version = getClass().getPackage().getImplementationVersion();
                if (version == null) {
                    version = "unknown";
                }
                instanceId = parseInstanceId();
                checkUpdatesFuture = scheduler.scheduleAtFixedRate(checkUpdatesRunnable, 0, 1, TimeUnit.HOURS);
            } catch (Exception e) {
                //Do nothing
            }
        }
    }

    private UUID parseInstanceId() throws IOException {
        UUID result = null;
        Path instanceIdPath = Paths.get(INSTANCE_ID_FILE);
        if (instanceIdPath.toFile().exists()) {
            byte[] data = Files.readAllBytes(instanceIdPath);
            if (data != null && data.length > 0) {
                try {
                    result = UUID.fromString(new String(data));
                } catch (IllegalArgumentException e) {
                    //Do nothing
                }
            }
        }
        if (result == null) {
            result = UUID.randomUUID();
            Files.write(instanceIdPath, result.toString().getBytes());
        }
        return result;
    }

    @PreDestroy
    private void destroy() {
        try {
            if (checkUpdatesFuture != null) {
                checkUpdatesFuture.cancel(true);
            }
            scheduler.shutdownNow();
        } catch (Exception e) {
            //Do nothing
        }
    }

    Runnable checkUpdatesRunnable = () -> {
        try {
            log.trace("Executing check update method for instanceId [{}], platform [{}] and version [{}]", instanceId, platform, version);
            ObjectNode request = new ObjectMapper().createObjectNode();
            request.put(PLATFORM_PARAM, platform);
            request.put(VERSION_PARAM, version);
            request.put(INSTANCE_ID_PARAM, instanceId.toString());
            JsonNode response = restClient.postForObject(UPDATE_SERVER_BASE_URL+"/api/vizzionnaire/updates", request, JsonNode.class);
            updateMessage = new UpdateMessage(
                    response.get("message").asText(),
                    response.get("updateAvailable").asBoolean()
            );
        } catch (Exception e) {
            log.trace(e.getMessage());
        }
    };

    @Override
    public UpdateMessage checkUpdates() {
        return updateMessage;
    }

}
