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
 * Mojo for deleting a WebInspect proxy
 * 
 * @author Ruud Senden
 *
 */
@Mojo(name = "wiDeleteProxy", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
public class WIDeleteProxyMojo extends AbstractWIMojo {
	/**
	 * The instance id of the proxy to be deleted, as specified or generated when
	 * creating the proxy instance.
	 */
	@Parameter(property = "com.fortify.webinspect.proxy.instanceId", required = false)
	protected String instanceId;
	
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
    	logResult(getWebInspectConnection().api(WebInspectProxyAPI.class).deleteProxy(instanceId));
    }
}
