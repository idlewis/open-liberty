-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.org.eclipse.microprofile.lra-1.0
singleton=true
-bundles=com.ibm.websphere.org.eclipse.microprofile.lra; location:="dev/api/stable/,lib/"; mavenCoordinates="org.eclipse.microprofile.lra:microprofile-lra-api:1.0-RC1"
-features=com.ibm.websphere.appserver.javax.jaxrs-2.1
kind=noship
edition=core
WLP-Activation-Type: parallel
