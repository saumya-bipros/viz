package com.vizzionnaire.server.transport.snmp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.device.data.DeviceData;
import com.vizzionnaire.server.common.data.device.data.DeviceTransportConfiguration;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;
import com.vizzionnaire.server.common.transport.TransportService;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.util.DataDecodingEncodingService;
import com.vizzionnaire.server.queue.util.TbSnmpTransportComponent;

import java.util.UUID;

@TbSnmpTransportComponent
@Service
@RequiredArgsConstructor
public class ProtoTransportEntityService {
    private final TransportService transportService;
    private final DataDecodingEncodingService dataDecodingEncodingService;

    public Device getDeviceById(DeviceId id) {
        TransportProtos.GetDeviceResponseMsg deviceProto = transportService.getDevice(TransportProtos.GetDeviceRequestMsg.newBuilder()
                .setDeviceIdMSB(id.getId().getMostSignificantBits())
                .setDeviceIdLSB(id.getId().getLeastSignificantBits())
                .build());

        if (deviceProto == null) {
            return null;
        }

        DeviceProfileId deviceProfileId = new DeviceProfileId(new UUID(
                deviceProto.getDeviceProfileIdMSB(), deviceProto.getDeviceProfileIdLSB())
        );

        Device device = new Device();
        device.setId(id);
        device.setDeviceProfileId(deviceProfileId);

        DeviceTransportConfiguration deviceTransportConfiguration = (DeviceTransportConfiguration) dataDecodingEncodingService.decode(
                deviceProto.getDeviceTransportConfiguration().toByteArray()
        ).orElseThrow(() -> new IllegalStateException("Can't find device transport configuration"));

        DeviceData deviceData = new DeviceData();
        deviceData.setTransportConfiguration(deviceTransportConfiguration);
        device.setDeviceData(deviceData);

        return device;
    }

    public DeviceCredentials getDeviceCredentialsByDeviceId(DeviceId deviceId) {
        TransportProtos.GetDeviceCredentialsResponseMsg deviceCredentialsResponse = transportService.getDeviceCredentials(
                TransportProtos.GetDeviceCredentialsRequestMsg.newBuilder()
                        .setDeviceIdMSB(deviceId.getId().getMostSignificantBits())
                        .setDeviceIdLSB(deviceId.getId().getLeastSignificantBits())
                        .build()
        );

        return (DeviceCredentials) dataDecodingEncodingService.decode(deviceCredentialsResponse.getDeviceCredentialsData().toByteArray())
                .orElseThrow(() -> new IllegalArgumentException("Device credentials not found"));
    }

    public TransportProtos.GetSnmpDevicesResponseMsg getSnmpDevicesIds(int page, int pageSize) {
        TransportProtos.GetSnmpDevicesRequestMsg requestMsg = TransportProtos.GetSnmpDevicesRequestMsg.newBuilder()
                .setPage(page)
                .setPageSize(pageSize)
                .build();
        return transportService.getSnmpDevicesIds(requestMsg);
    }
}
