# Fortify WebInspect Maven Plugin
This Maven plugin allows for performing various WebInspect stand-alone and WebInspect Enterprise
actions through Maven goals. At the moment, the main purpose of this plugin is to record HTTP(S) 
traffic from integration tests or other automated testing tools, and automatically starting a 
WebInspect stand-alone or WebInspect Enterprise (WIE) scan based on the recorded traffic. 

## Prerequisites for using the plugin
Of course you will need to have Maven installed to build and use the plugin. 

In addition, you will need to have a WebInspect stand-alone instance running with the WebInspect API
service running. Please refer to the WebInspect documentation for more details. 
At the moment, the plugin has only been tested with authentication disabled on the
WebInspect API service. However Basic Authentication may work out of the box as well by 
providing the appropriate credentials. If you enable HTTPS for the WebInspect API, you may need
to import the HTTPS certificate into the Java trusted keystore for the Java instance used to
run Maven.

If you want to run scans on WebInspect Enterprise, you will still need a WebInspect stand-alone
instance, as the current version of the WIE API doesn't provide all required functionality. 
For example, the WIE API doesn't support managing proxy instances, and doesn't allow to 
configure advanced scan settings.

For WIE-based scans, you will need to have WIE credentials that allow access to the appropriate
project versions. If your WIE instance uses a certificate that has not been signed by a public CA,
you will need to import the certificate into the Java trusted keystore for the Java instance used to
run Maven.


## Building the Maven plugin
Simply run a ```mvn clean install``` from the fortify-integration-maven-webinspect directory to
build the plugin and deploy it to your local Maven repository. Please refer to the 
documentation of your onsite Maven repository for instructions on how to deploy the plugin
to an onsite shared Maven repository.


## Managing WebInspect proxies
The plugin provides Maven goals for creating and deleting WebInspect stand-alone proxy instances,
and saving the recorded traffic in various formats. Recorded traffic can either be downloaded to
the system where Maven is running, or saved on the WebInspect host for later use.

**Create a proxy**

Create a proxy with the given instance id, port and address. If any of these values are not specified, 
they will be generated.

``mvn -Dcom.fortify.webinspect.connection="http://[WIHost]:[port]/webinspect;[optionalExtraProperties]" -Dcom.fortify.webinspect.proxy.instanceId=[instanceId] -Dcom.fortify.webinspect.proxy.port=[port] -Dcom.fortify.webinspect.proxy.host=[listen address] com.fortify.integration.maven.webinspect:maven-webinspect-plugin:2.0:wiCreateProxy``

This goal will output the specified or generated instance id, port and address on the Maven console for later use.
In addition, this goal will set the following project properties for use by other goals:
* com.fortify.webinspect.proxy.instanceId
* com.fortify.webinspect.proxy.port
* com.fortify.webinspect.proxy.address

**Save proxy traffic on WebInspect host**

Save proxy traffic on the WebInspect host. Allowed extensions are tsf (native WebInspect proxy format),
xml (scan settings) or webmacro (workflow or login macro).

``mvn -Dcom.fortify.webinspect.connection="http://[WIHost]:[port]/webinspect;[optionalExtraProperties]" -Dcom.fortify.webinspect.proxy.instanceId=[instanceId] -Dcom.fortify.webinspect.proxy.extension=[extension] com.fortify.maven.plugin:webinspect-maven-plugin:17.10.0-SNAPSHOT:wiSaveProxyTrafficOnServer``

**Save proxy traffic on the local system** 

Save proxy traffic on the local system. Allowed extensions are tsf (native WebInspect proxy format),
xml (scan settings) or webmacro (workflow or login macro).

``mvn -Dcom.fortify.webinspect.connection="http://[WIHost]:[port]/webinspect;[optionalExtraProperties]" -Dcom.fortify.webinspect.proxy.instanceId=[instanceId] -Dcom.fortify.webinspect.proxy.extension=[extension] -Dcom.fortify.webinspect.proxy.outputFile=[filename] com.fortify.integration.maven.webinspect:maven-webinspect-plugin:2.0:wiSaveProxyTraffic``

**Delete a proxy**

``mvn -Dcom.fortify.webinspect.connection="http://[WIHost]:[port]/webinspect;[optionalExtraProperties]" com.fortify.integration.maven.webinspect:maven-webinspect-plugin:2.0:wiDeleteProxy``

## Creating WebInspect stand-alone scan
TODO

## Creating WebInspect Enterprise scan
TODO