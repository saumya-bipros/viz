package com.vizzionnaire.rule.engine.gcp.pubsub;

import lombok.Data;

import java.util.Collections;
import java.util.Map;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

@Data
public class TbPubSubNodeConfiguration implements NodeConfiguration<TbPubSubNodeConfiguration> {

    private String projectId;
    private String topicName;
    private Map<String, String> messageAttributes;
    private String serviceAccountKey;
    private String serviceAccountKeyFileName;

    @Override
    public TbPubSubNodeConfiguration defaultConfiguration() {
        TbPubSubNodeConfiguration configuration = new TbPubSubNodeConfiguration();
        configuration.setProjectId("my-google-cloud-project-id");
        configuration.setTopicName("my-pubsub-topic-name");
        configuration.setMessageAttributes(Collections.emptyMap());
        return configuration;
    }
}
