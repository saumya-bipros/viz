package com.vizzionnaire.server.dao.attributes;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vizzionnaire.server.cache.CacheSpecsMap;
import com.vizzionnaire.server.cache.RedisTbTransactionalCache;
import com.vizzionnaire.server.cache.TBRedisCacheConfiguration;
import com.vizzionnaire.server.cache.TbRedisSerializer;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.kv.AttributeKvEntry;
import com.vizzionnaire.server.common.data.kv.BaseAttributeKvEntry;
import com.vizzionnaire.server.common.data.kv.BooleanDataEntry;
import com.vizzionnaire.server.common.data.kv.DoubleDataEntry;
import com.vizzionnaire.server.common.data.kv.JsonDataEntry;
import com.vizzionnaire.server.common.data.kv.KvEntry;
import com.vizzionnaire.server.common.data.kv.LongDataEntry;
import com.vizzionnaire.server.common.data.kv.StringDataEntry;
import com.vizzionnaire.server.gen.transport.TransportProtos.AttributeValueProto;
import com.vizzionnaire.server.gen.transport.TransportProtos.KeyValueType;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("AttributeCache")
public class AttributeRedisCache extends RedisTbTransactionalCache<AttributeCacheKey, AttributeKvEntry> {
	
    public AttributeRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.ATTRIBUTES_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbRedisSerializer<>() {
            @Override
            public byte[] serialize(AttributeKvEntry attributeKvEntry) throws SerializationException {
                AttributeValueProto.Builder builder = AttributeValueProto.newBuilder()
                        .setLastUpdateTs(attributeKvEntry.getLastUpdateTs());
                switch (attributeKvEntry.getDataType()) {
                    case BOOLEAN:
                        attributeKvEntry.getBooleanValue().ifPresent(builder::setBoolV);
                        builder.setHasV(attributeKvEntry.getBooleanValue().isPresent());
                        builder.setType(KeyValueType.BOOLEAN_V);
                        break;
                    case STRING:
                        attributeKvEntry.getStrValue().ifPresent(builder::setStringV);
                        builder.setHasV(attributeKvEntry.getStrValue().isPresent());
                        builder.setType(KeyValueType.STRING_V);
                        break;
                    case DOUBLE:
                        attributeKvEntry.getDoubleValue().ifPresent(builder::setDoubleV);
                        builder.setHasV(attributeKvEntry.getDoubleValue().isPresent());
                        builder.setType(KeyValueType.DOUBLE_V);
                        break;
                    case LONG:
                        attributeKvEntry.getLongValue().ifPresent(builder::setLongV);
                        builder.setHasV(attributeKvEntry.getLongValue().isPresent());
                        builder.setType(KeyValueType.LONG_V);
                        break;
                    case JSON:
                        attributeKvEntry.getJsonValue().ifPresent(builder::setJsonV);
                        builder.setHasV(attributeKvEntry.getJsonValue().isPresent());
                        builder.setType(KeyValueType.JSON_V);
                        break;

                }
                return builder.build().toByteArray();
            }

            @Override
            public AttributeKvEntry deserialize(AttributeCacheKey key, byte[] bytes) throws SerializationException {
                try {
                    AttributeValueProto proto = AttributeValueProto.parseFrom(bytes);
                    boolean hasValue = proto.getHasV();
                    KvEntry entry;
                    switch (proto.getType()) {
                        case BOOLEAN_V:
                            entry = new BooleanDataEntry(key.getKey(), hasValue ? proto.getBoolV() : null);
                            break;
                        case LONG_V:
                            entry = new LongDataEntry(key.getKey(), hasValue ? proto.getLongV() : null);
                            break;
                        case DOUBLE_V:
                            entry = new DoubleDataEntry(key.getKey(), hasValue ? proto.getDoubleV() : null);
                            break;
                        case STRING_V:
                            entry = new StringDataEntry(key.getKey(), hasValue ? proto.getStringV() : null);
                            break;
                        case JSON_V:
                            entry = new JsonDataEntry(key.getKey(), hasValue ? proto.getJsonV() : null);
                            break;
                        default:
                            throw new InvalidProtocolBufferException("Unrecognized type: " + proto.getType() + " !");
                    }
                    return new BaseAttributeKvEntry(proto.getLastUpdateTs(), entry);
                } catch (InvalidProtocolBufferException e) {
                    throw new SerializationException(e.getMessage());
                }
            }
        });
    }

}
