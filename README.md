## Pre-requisites
- The OpenShift Serverless operator has been installed to your cluster and knative serving has been configured.
- The AMQ Broker operator has been installed to your cluster.

## Deployment
Log into you OpenShift cluster and create a new project:
```
oc login ...
oc new-project camel-amqp-knative
```

Create the AMQ broker and addresses:
```
oc apply -f amq/broker-cr.yaml
oc apply -f amq/address-1-cr.yaml
oc apply -f amq/address-2-cr.yaml
```

Create 2 knative services to test with:
```
kn service create event-display-1 \
  --concurrency-target=1 \
  --image=quay.io/openshift-knative/knative-eventing-sources-event-display:latest

kn service create event-display-2 \
  --concurrency-target=1 \
  --image=quay.io/openshift-knative/knative-eventing-sources-event-display:latest
```

Deploy the camel app:
```
mvn clean package -Dquarkus.kubernetes.deploy=true [-Dquarkus.kubernetes-client.trust-certs=true]
```

## Testing

- Send a message to `testAddress1` (this can be done via the AMQ console). You should see a pod for `event-display-1` spin up and the content of the message will appear in the log for `event-display-1`.
- Send a message to `testAddress2` (this can be done via the AMQ console). You should see a pod for `event-display-2` spin up and the content of the message will appear in the log for `event-display-2`.