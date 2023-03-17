package com.example.knative.amqp;

import javax.enterprise.inject.Produces;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPConnectionDetails;
import org.apache.camel.component.knative.KnativeComponent;
import org.apache.camel.component.knative.KnativeEnvironmentSupport;
import org.apache.camel.component.knative.spi.Knative;
import org.apache.camel.component.knative.spi.KnativeEnvironment;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.Unremovable;

public class AMQPToKnative extends RouteBuilder {

    @ConfigProperty(name = "amq.hostname")
    String amqHostname;
    
    @ConfigProperty(name = "amq.port")
    String amqPort;

    @ConfigProperty(name = "amq.address1")
    String address1;

    @ConfigProperty(name = "amq.address2")
    String address2;

    @ConfigProperty(name = "knative.service1")
    String service1;

    @ConfigProperty(name = "knative.service2")
    String service2;

    @Produces
    @Unremovable
    AMQPConnectionDetails amqpConnection() {
      return new AMQPConnectionDetails("amqp://" + amqHostname + ":" + amqPort);
    }
    
    @Override
    public void configure() throws Exception {

        KnativeComponent component = getContext().getComponent("knative", KnativeComponent.class);
        component.setEnvironment(KnativeEnvironment.on(
            KnativeEnvironmentSupport.endpoint(Knative.EndpointKind.sink, "event-display-1", service1),
            KnativeEnvironmentSupport.endpoint(Knative.EndpointKind.sink, "event-display-2", service2)
        ));

        from("amqp:queue:" + address1)
            .to("knative:endpoint/event-display-1");

        from("amqp:queue:" + address2)
            .to("knative:endpoint/event-display-2");
    }
  }
