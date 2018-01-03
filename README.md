### Vertx Eventbus Cluster Example

An example vert.x application with multiple pods using the vertx eventbus clustered with jgroups on kubernetes

```
oc new-project eventbus --display-name='Vertx Eventbus' --description='Vertx Eventbus'
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default
mvn fabric8:deploy -P openshift
```

Test - you should see different pods answering the http request ~> eventbus response

```
http://vertx-eventbus-multi-eventbus.192.168.137.2.nip.io/api/greeting?name=mike
{"content":"[vertx-eventbus-multi-1-sr2d4][vert.x-eventloop-thread-3] Hello, mike!"}
{"content":"[vertx-eventbus-multi-1-bm98j][vert.x-eventloop-thread-3] Hello, mike!"}
```
