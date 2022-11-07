package com.vizzionnaire.server.service.edge.rpc.constructor;

import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.gen.edge.v1.CustomerUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class CustomerMsgConstructor {

    public CustomerUpdateMsg constructCustomerUpdatedMsg(UpdateMsgType msgType, Customer customer) {
        CustomerUpdateMsg.Builder builder = CustomerUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(customer.getId().getId().getMostSignificantBits())
                .setIdLSB(customer.getId().getId().getLeastSignificantBits())
                .setTitle(customer.getTitle());
        if (customer.getCountry() != null) {
            builder.setCountry(customer.getCountry());
        }
        if (customer.getState() != null) {
            builder.setState(customer.getState());
        }
        if (customer.getCity() != null) {
            builder.setCity(customer.getCity());
        }
        if (customer.getAddress() != null) {
            builder.setAddress(customer.getAddress());
        }
        if (customer.getAddress2() != null) {
            builder.setAddress2(customer.getAddress2());
        }
        if (customer.getZip() != null) {
            builder.setZip(customer.getZip());
        }
        if (customer.getPhone() != null) {
            builder.setPhone(customer.getPhone());
        }
        if (customer.getEmail() != null) {
            builder.setEmail(customer.getEmail());
        }
        if (customer.getAdditionalInfo() != null) {
            builder.setAdditionalInfo(JacksonUtil.toString(customer.getAdditionalInfo()));
        }
        return builder.build();
    }

    public CustomerUpdateMsg constructCustomerDeleteMsg(CustomerId customerId) {
        return CustomerUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(customerId.getId().getMostSignificantBits())
                .setIdLSB(customerId.getId().getLeastSignificantBits()).build();
    }
}
