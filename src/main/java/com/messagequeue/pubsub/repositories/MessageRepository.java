package com.messagequeue.pubsub.repositories;

import com.messagequeue.pubsub.entities.Message;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends CassandraRepository<Message, UUID> {
}
