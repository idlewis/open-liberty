-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.mpLRA-1.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-ShortName: mpLRA-1.0
Subsystem-Name: MicroProfile Long Running Actions 1.0
IBM-API-Package: \
  org.eclipse.microprofile.lra.annotation; type="stable", \
  org.eclipse.microprofile.lra.annotation.ws.rs; type="stable";
-features=com.ibm.websphere.appserver.org.eclipse.microprofile.lra-1.0, \
          com.ibm.websphere.appserver.jaxrs-2.1, \
          com.ibm.websphere.appserver.servlet-4.0, \
          com.ibm.wsspi.appserver.webBundle-1.0
-bundles=com.ibm.ws.require.java8, \
         com.ibm.ws.lra, \
         com.ibm.ws.org.jboss.narayana.rts
kind=noship
edition=full
