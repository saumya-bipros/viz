package com.vizzionnaire.edge.rpc;

import com.vizzionnaire.server.gen.edge.v1.DownlinkMsg;
import com.vizzionnaire.server.gen.edge.v1.DownlinkResponseMsg;
import com.vizzionnaire.server.gen.edge.v1.EdgeConfiguration;
import com.vizzionnaire.server.gen.edge.v1.UplinkMsg;
import com.vizzionnaire.server.gen.edge.v1.UplinkResponseMsg;

import java.util.function.Consumer;

public interface EdgeRpcClient {

    void connect(String integrationKey,
                 String integrationSecret,
                 Consumer<UplinkResponseMsg> onUplinkResponse,
                 Consumer<EdgeConfiguration> onEdgeUpdate,
                 Consumer<DownlinkMsg> onDownlink,
                 Consumer<Exception> onError);

    void disconnect(boolean onError) throws InterruptedException;

    void sendSyncRequestMsg(boolean syncRequired);

    void sendUplinkMsg(UplinkMsg uplinkMsg);

    void sendDownlinkResponseMsg(DownlinkResponseMsg downlinkResponseMsg);
}
