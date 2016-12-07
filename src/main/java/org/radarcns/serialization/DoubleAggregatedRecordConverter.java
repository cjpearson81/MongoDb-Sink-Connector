package org.radarcns.serialization;

import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.bson.BsonDateTime;
import org.bson.BsonDouble;
import org.bson.Document;
import org.radarcns.util.Utility;

import java.util.Collection;
import java.util.Collections;

import static org.radarcns.mongodb.MongoDbSinkConnector.COLL_DOUBLE_SINGLETON;

public class DoubleAggregatedRecordConverter implements RecordConverter<Document> {
    @Override
    public Collection<String> supportedSchemaNames() {
        return Collections.singleton(COLL_DOUBLE_SINGLETON);
    }

    @Override
    public Document convert(SinkRecord record) {
        Struct key = (Struct) record.key();
        Struct value = (Struct) record.value();

        String mongoId = key.getString("userID") + "-" +
                key.getString("sourceID") + "-" +
                key.getInt64("start") + "-" +
                key.getInt64("end");

        return new Document("_id", mongoId)
                .append("user", key.getString("userID"))
                .append("source", key.getString("sourceID"))
                .append("min", new BsonDouble(value.getFloat64("min")))
                .append("max", new BsonDouble(value.getFloat64("max")))
                .append("sum", new BsonDouble(value.getFloat64("sum")))
                .append("count", new BsonDouble(value.getFloat64("count")))
                .append("avg", new BsonDouble(value.getFloat64("avg")))
                .append("quartile", Utility.extractQuartile(value.getArray("quartile")))
                .append("iqr", new BsonDouble(value.getFloat64("iqr")))
                .append("start", new BsonDateTime(key.getInt64("start")))
                .append("end", new BsonDateTime(key.getInt64("end")));
    }
}
