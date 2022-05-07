package com.messagequeue.pubsub.entities;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("message")
@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
/***
 * This is the base entity for our pubsub system.
 *
 * Since we are trying to create a topic-pull based system, a lot of computation can be offloaded
 * to the cassandra cluster itself, to optimize our runtime processing.
 *
 * Cassandra does an excellent job of:
 * 1. Managing partitions
 *    (leverage this to provide topic functionality)
 *    We can leverage this to manage our topics. Since cassandra distributes data across nodes in a cluster
 *    based on the partition key, we can leverage this to store each topic in its own node, or in the least,
 *    organized close together in the same set of nodes. That is a massive read/write optimization right there.
 *    The only downside will be estimating the size of the cassandra cluster we would have to run as a
 *    datacenter(SimpleStrategy), or across datacenters(NetworkTopologyStrategy).
 *
 * 2. Managing clustering
 *    (leverage this to provide message ordering)
 *    Cassandra's storage engine does an excellent job of managing clustering. What this means is, data is written to the disk
 *    close together, and following a certain sort order based on the cluster keys. This can be leveraged to have partitions
 *    inside a topic like kafka does, with strict ordering of messages.
 *    The one downside could be when scaling down the partition (cluster keys at the DB level) based on the consumer group
 *    scaling down.
 *    Does cassandra have a good tolerance for frequent changes in clustering?
 *    E.g.:
 *    Consumer n+1 joins consumer group A.
 *    Consumer group A is connected to topic T.
 *    Ideally, we should get a new clustering for consumer n+1 for reads if we are replicating Kafka's behaviour.
 *
 *    To achieve this, we have to rethink our keys, a simple id column based primary key will not fit this use-case.
 *
 *    Since we want to achieve per message-topic based node separation, and per topic-partition based ordering, we have
 *    a composite primary key as ((topic), partition_id).
 *      @see com.messagequeue.pubsub.entities.MessageKey
 *
 *    TODO: Decide on the partitioning strategy based on producer/consumer group scaling and affinity.
 *    TODO: Decide on further tables, since cassandra works well with table per query.
 */
public class Message {

    @PrimaryKey @Column
    private @NonNull MessageKey id;

    @Column
    private @NonNull String payload;

    public Message(String topic, int partitionId, String payload) {
        id = new MessageKey(topic, partitionId);
        this.payload = payload;
    }
}
