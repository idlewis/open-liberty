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
          com.ibm.wsspi.appserver.webBundle-1.0
-bundles=io.openliberty.microprofile.lra.coordinator.1.0.internal
kind=noship
edition=full
