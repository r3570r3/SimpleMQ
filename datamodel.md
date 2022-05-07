# SimpleMQ Cassandra Datamodel

## Keyspace
***
Like in the SQL world, Casssandra too has the concept of a schema, which holds tables. This is called a keyspace.
However, a keyspace in Cassandra is also the logical storage unit like tablespaces in RDBMS.

Keyspace has a 1:1 mapping with the application. For our SimpleMQ application, we will create a keyspace called 
pubsub.

### Replication Strategy
***
Being a distributed database, Cassandra achieves high availability through replication. 
This means, a keyspace is replicated across nodes in the cluster. 
In our application, we will replicate the pubsub keyspace.

This replication behavior and extent is determined by two factors expressed together as a replication strategy:

#### 1. Strategy Class
A cluster in cassandra can either be grouped in a single datacenter in a **SimpleStrategy**, or across 
datacenters in a **NetworkTopologyStrategy**.

**NetworkTopologyStrategy** is the preferred replication class for production systems. 

We will start with a SimpleStrategy and migrate to a NetworkTopologyStrategy later for our SimpleMQ application. 

#### 2. Replication Factor
Replication factor indicates the number of nodes to which the keyspace needs to be replicated in a datacenter. 
This can be set per datacenter as well.

For SimpleMQ, we will work with 3 replica nodes in one local dev. datacenter. Our keyspace creation query 
will look like:

```roomsql
CREATE KEYSPACE pubsubkeyspace WITH
    REPLICATION={
        'class' : 'SimpleStrategy',
        'replication_factor' : '3'
    };
    
describe keyspace pubsubkeyspace;
```

## Table Design
***
Coming from the RDBMS world, a lot of design choices in the NoSQL world might feel counterintuitive. However, 
these are the very design considerations that give NoSQL databases their efficiency. 

Here are some of them.

### Design Considerations

#### 1. Data Duplication is OK 
This is a very hard to grasp concept for RDBMS folks, where data duplication is frowned upon. Cassandra is based
on the fact that disk is the cheapest resource out there.

Hence, it makes much more sense to have multiple copies of the same data with different structures, rather than
having a single record for each data, and querying them over complex joins. This concept also gives rise to the
table per query design, where we have the same data structured with different primary keys to accommodate for 
different queries.

E.g.: 

In RDBMS, we would have two tables: *Employee* and *Department* and join over them to query them together. 

In Cassandra however, the same data can be represented by tables like *EmployeePerDepartment* where we create
copies of the Employee and Department data as single joined records when performing writes on Employee and 
Department tables. 

***At times, there may be no Employee or Department table, just a EmployeePerDepartment table.***

#### 2. The Same Table with Same Columns and Different Primary Keys is OK
Ths builds upon the data duplication aspect, and cranks it a notch up. It is not just OK to have te same overall 
data arranged across different **query tables**. it is also OK to have the same table duplicated with different 
primary keys.

E.g.: Employee table can have one copy with ((Employee name, joining date), manager name, manager id, employee id)
as one primary key. However, this key partitions by employee name and joining data, and sorts by the rest of the
fields. This is optimized for queries on Employee name, but if we want to query based on manager name, we should 
have the primary key as ((employee name, manager name), manager id, employee id). To accommodate for queries like
these, we can have a second copy of Employee table and it is perfectly OK in Cassandra.

#### 3. High Number of Writes is OK
In RDBMS, writes are costly. Writes can cause row lock contention in the worst implementations, or MVCC in 
the best implementation. MVCC although highly optimized, still has a lower throughput when it comes to 
concurrent writes. MVCC can at best achieve read and write separation through snapshot isolation.

In contrast, Cassandra is designed around a high write-throughput by implementing the *last writer wins* strategy.
No locks, no snapshots, just overwrite.

What is costly in cassandra however, is reads. This is solved by having the same data shaped according to different
queries and committed in different tables; a.k.a. high number of writes.

***Transactional NoSQL databases do MVCC for reads and optimistic concurrency for writes.***

#### 4. Optimize for Minimum Partition Reads
After going over the not to dos, here is the one and most important TODO in cassandra. If you look closely at this
point, it has to do with the design of the primary key.

The primary key determines how data is partitioned over nodes and how it is clustered in the partition. That also
means, if your queries are running over multiple partitions, you will request the coordinator to query over different
nodes. This introduces latency in the cluster and can cause performance issues when reading data.

Similarly, even if multiple nodes are on the same partition, there will still be performance issues because of the
way rows are stored in Cassandra.

Hence it is always better to limit the number of partitions you are querying.

More here:
https://shermandigital.com/blog/designing-a-cassandra-data-model/

Now, lets start designing with these ideas.

```roomsql
USE pubsubkeyspace;
create table if not exists pubsubkeyspace.message (
    topic text,
    partitionId int,
    payload text,
    messageDate timestamp,
    primary key((topic, partitionId), messageDate))
    with clustering order by (messageDate asc);
```

This will give a table with a composite primary key.

Each combination of topic and partition ID will generate a new hash, and the data for each will be stored in a 
new partition. In addition, the ordering will be maintained in each partition based on timestamp.