package com.messagequeue.pubsub.entities;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("messages")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {

    @PrimaryKey
    private @NonNull UUID id;
    private @NonNull String payload;

    public Message(String payload) {
        id = Uuids.endOf(System.currentTimeMillis());
        this.payload = payload;
    }
}
