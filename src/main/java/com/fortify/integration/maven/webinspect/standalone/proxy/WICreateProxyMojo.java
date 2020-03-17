/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates, a Micro Focus company
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.integration.maven.webinspect.standalone.proxy;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.fortify.client.webinspect.api.WebInspectProxyAPI;
import com.fortify.integration.maven.webinspect.standalone.AbstractWIMojo;
import com.fortify.util.rest.json.JSONMap;


/**
 * Mojo for creating a WebInspect proxy. Proxy properties like instance id,
 * port and address will be saved as project properties for later use by other mojo's.
 * 
 * @author Ruud Senden
 *
 */
@Mojo(name = "wiCreateProxy", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
public class WICreateProxyMojo extends AbstractWIMojo {
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;
	
	/**
	 * Represents the unique name of this proxy instance.
	 * If not specified, WebInspect will automatically generate an
	 * instance id when creating a new proxy instance. This
	 * generated proxy instance id then needs to be specified
	 * for the other proxy-related goals.
	 */
	@Parameter(property = "com.fortify.webinspect.proxy.instanceId", required = false)
	protected String instanceId;
	
	/**
	 * Port to listen on by the proxy server. If not specified, WebInspect
	 * will use a random unused port.
	 */
	@Parameter(property = "com.fortify.webinspect.proxy.port", required = false)
	private int proxyPort;

	/**
	 * Hostname or ip address where the proxy should bind on. If not specified,
	 * the WebInspect proxy will bind on the default network interface address
	 * (127.0.0.1).
	 */
	@Parameter(property = "com.fortify.webinspect.proxy.address", required = false)
	protected String proxyAddress;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		JSONMap result = getWebInspectConnection().api(WebInspectProxyAPI.class).createProxy(instanceId, proxyAddress, proxyPort);
		logResult(result);
		project.getProperties().put("com.fortify.webinspect.proxy.instanceId", result.get("instanceId"));
		project.getProperties().put("com.fortify.webinspect.proxy.port", result.get("port"));
		project.getProperties().put("com.fortify.webinspect.proxy.address", result.get("address"));
	}
}
