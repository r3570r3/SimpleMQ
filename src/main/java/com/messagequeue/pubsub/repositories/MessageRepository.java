package com.messagequeue.pubsub.repositories;

import com.messagequeue.pubsub.entities.Message;
//import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
//public interface MessageRepository extends CassandraRepository<Message, Long> {
}
