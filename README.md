# SimpleMQ
## Steps
1. Clear environment. Stop processes and remove existing containers.

```bash
docker stop CassandraNode1
```

```bash
docker rm CassandraNode1
```

2. Build docker image.

```bash
docker build -t pubsub.java .
```

3. Pull cassandra image.

```bash
docker pull cassandra
```

4. Start cassandra instance and wait for it to start.

```bash
docker run -p 9042:9042 --rm --name CassandraNode1 -d cassandra
```

5. Monitor with:

```bash
docker logs -f CassandraNode1
```

and wait for the line:

```
Created default superuser role 'cassandra'
```

6. Run sql shell on cassandra cluster.

```bash
docker exec -it CassandraNode1 bash -c 'cqlsh'
```

and create the application keyspace:

```roomsql
CREATE KEYSPACE pubsubkeyspace 
WITH replication = 
{
    'class' : 'SimpleStrategy', 
    'replication_factor' : 1
};
```

also create the table:

```roomsql
use pubsubkeyspace;
```

```roomsql
create table if not exists pubsubkeyspace.message (
    topic text,
    partitionId int,
    payload text,
    messageDate timestamp,
    primary key((topic, partitionId), messageDate)
)
with clustering order by (messageDate asc);
```

7. Get IP and load details.

```bash
docker exec -it CassandraNode1 bash -c 'nodetool status'
```

8. Hit publish URL:

```bash
http://localhost:8082/api/v1/publish
```

and verify data in the DB:

```bash
select * from messages;
```
