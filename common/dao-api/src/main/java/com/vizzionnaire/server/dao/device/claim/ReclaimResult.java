package com.vizzionnaire.server.dao.device.claim;

import com.vizzionnaire.server.common.data.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReclaimResult {
    Customer unassignedCustomer;
}
