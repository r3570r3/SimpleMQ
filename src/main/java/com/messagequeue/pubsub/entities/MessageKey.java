package com.messagequeue.pubsub.entities;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;

@PrimaryKeyClass
public class MessageKey implements Serializable {

    @PrimaryKeyColumn(name="topic", type=PrimaryKeyType.PARTITIONED)
    private String topic;

    @PrimaryKeyColumn(name="partitionId", type=PrimaryKeyType.PARTITIONED)
    private int partitionId;

    @PrimaryKeyColumn(name="messageDate", type=PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant messageDate;

    public MessageKey(
            final String topic,
            final int partitionId) {
        this.topic = topic;
        this.partitionId = partitionId;
        messageDate = Instant.now();
    }
}