package com.vizzionnaire.server.service.edge.rpc.constructor;

import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.UserCredentials;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.gen.edge.v1.UserCredentialsUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UserUpdateMsg;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class UserMsgConstructor {

    public UserUpdateMsg constructUserUpdatedMsg(UpdateMsgType msgType, User user, CustomerId customerId) {
        UserUpdateMsg.Builder builder = UserUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(user.getId().getId().getMostSignificantBits())
                .setIdLSB(user.getId().getId().getLeastSignificantBits())
                .setEmail(user.getEmail())
                .setAuthority(user.getAuthority().name());
        if (customerId != null) {
            builder.setCustomerIdMSB(customerId.getId().getMostSignificantBits());
            builder.setCustomerIdLSB(customerId.getId().getLeastSignificantBits());
        }
        if (user.getFirstName() != null) {
            builder.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            builder.setLastName(user.getLastName());
        }
        if (user.getAdditionalInfo() != null) {
            builder.setAdditionalInfo(JacksonUtil.toString(user.getAdditionalInfo()));
        }
        return builder.build();
    }

    public UserUpdateMsg constructUserDeleteMsg(UserId userId) {
        return UserUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(userId.getId().getMostSignificantBits())
                .setIdLSB(userId.getId().getLeastSignificantBits()).build();
    }

    public UserCredentialsUpdateMsg constructUserCredentialsUpdatedMsg(UserCredentials userCredentials) {
        UserCredentialsUpdateMsg.Builder builder = UserCredentialsUpdateMsg.newBuilder()
                .setUserIdMSB(userCredentials.getUserId().getId().getMostSignificantBits())
                .setUserIdLSB(userCredentials.getUserId().getId().getLeastSignificantBits())
                .setEnabled(userCredentials.isEnabled())
                .setPassword(userCredentials.getPassword());
        return builder.build();
    }
}
