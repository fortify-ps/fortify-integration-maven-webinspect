/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC, a Micro Focus company
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

import com.fortify.client.webinspect.api.WebInspectProxyAPI;
import com.fortify.integration.maven.webinspect.standalone.AbstractWIMojo;

/**
 * Mojo for saving WebInspect proxy traffic to the WebInspect host
 * 
 * @author Ruud Senden
 *
 */
@Mojo(name = "wiSaveProxyTrafficOnServer", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
public class WISaveProxyTrafficOnServerMojo extends AbstractWIMojo {
	private String action = "overwrite";
	
	/**
	 * The instance id of the proxy, as specified or generated when creating the proxy instance,
	 * for which the traffic needs to be saved. The file name will be based on the proxy
	 * instance id. Note however that if the instance id contains dots, everything after the
	 * last dot will be stripped from the file name. For example, an instance id 'a.b.c' will
	 * be saved as 'a.b.[extension]'.
	 */
	@Parameter(property = "com.fortify.webinspect.proxy.instanceId", required = false)
	protected String instanceId;

	/**
	 * Extension of savefile, choose between webmacro, tsf or xml.
	 * Defaults to webmacro.
	 */
	@Parameter(property = "com.fortify.webinspect.proxy.traffic.extension", required = true, defaultValue = "webmacro")
	private String extension;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		logResult(getWebInspectConnection().api(WebInspectProxyAPI.class).saveProxyTrafficOnServer(instanceId, extension, action));
	}

	
}
