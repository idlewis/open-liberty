-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=io.openliberty.mpLRACoordinator-1.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-ShortName: mpLRACoordinator-1.0
Subsystem-Name: MicroProfile Long Running Actions Coordinator 1.0
-features=com.ibm.websphere.appserver.jaxrs-2.1, \
          com.ibm.websphere.appserver.servlet-4.0, \
          com.ibm.wsspi.appserver.webBundle-1.0, \
          io.openliberty.org.eclipse.microprofile.lra-1.0, \
          com.ibm.websphere.appserver.org.eclipse.microprofile.openapi-1.1
-bundles= \
    io.openliberty.microprofile.lra.coordinator.1.0.internal, \
    io.openliberty.org.jboss.narayana.rts.lra-coordinator
kind=noship
edition=full
