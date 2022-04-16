# SimpleMQ
## Steps
1. Clear environment. Stop processes and remove existing containers.

``docker stop CassandraNode1``

``docker rm CassandraNode1``
2. Build docker image.

``docker build -t pubsub.java .``
3. Pull cassandra image.

``docker pull cassandra``
4. Start cassandra instance and wait for it to start.

``docker run -p 9042:9042 --rm --name CassandraNode1 -d cassandra``

5. Monitor with:

``docker logs -f CassandraNode1``

and wait for the line:
`` Created default superuser role 'cassandra'``

6. Run sql shell on cassandra cluster.

``docker exec -it CassandraNode1 bash -c 'cqlsh'``

and create the application keyspace:

``CREATE KEYSPACE pubsub WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};``

also create the table:

``use pubsub;``

``CREATE TABLE messages (id timeuuid primary key, payload text);``
7. Get IP and load details.

``docker exec -it CassandraNode1 bash -c 'nodetool status'``

8. Hit publish URL:

``http://localhost:8082/api/v1/publish``

and verify data in the DB:

``select * from messages;``