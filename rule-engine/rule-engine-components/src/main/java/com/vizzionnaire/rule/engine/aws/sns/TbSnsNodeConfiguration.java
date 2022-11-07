package com.vizzionnaire.rule.engine.aws.sns;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

@Data
public class TbSnsNodeConfiguration implements NodeConfiguration<TbSnsNodeConfiguration> {

    private String topicArnPattern;
    private String accessKeyId;
    private String secretAccessKey;
    private String region;

    @Override
    public TbSnsNodeConfiguration defaultConfiguration() {
        TbSnsNodeConfiguration configuration = new TbSnsNodeConfiguration();
        configuration.setTopicArnPattern("arn:aws:sns:us-east-1:123456789012:MyNewTopic");
        configuration.setRegion("us-east-1");
        return configuration;
    }
}
