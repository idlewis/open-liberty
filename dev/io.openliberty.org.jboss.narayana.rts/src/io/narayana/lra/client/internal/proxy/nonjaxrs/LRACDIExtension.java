package io.narayana.lra.client.internal.proxy.nonjaxrs;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

public class LRACDIExtension implements Extension {

    public void observe(@Observes AfterBeanDiscovery event, BeanManager beanManager) {

        System.err.println("I am registering a bean\n");

        String message = "hello " + new Date() + "\n";

        event.addBean().read(beanManager.createAnnotatedType(LRAParticipantRegistry.class)).beanClass(LRAParticipantRegistry.class).scope(ApplicationScoped.class).createWith(context -> new LRAParticipantRegistry(message));

    }
}
