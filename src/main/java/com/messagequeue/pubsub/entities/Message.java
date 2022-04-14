package com.messagequeue.pubsub.entities;

import lombok.*;
//import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Table
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Message {

    //@PrimaryKey
    @Id
    private @NonNull UUID id;
    private @NonNull String payload;

    public Message(String payload) {
        this.payload = payload;
    }
}
