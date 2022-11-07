package com.vizzionnaire.server.queue.sqs;

import com.amazonaws.http.SdkHttpMetadata;
import com.vizzionnaire.server.queue.TbQueueMsgMetadata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AwsSqsTbQueueMsgMetadata implements TbQueueMsgMetadata {

    private final SdkHttpMetadata metadata;
}
