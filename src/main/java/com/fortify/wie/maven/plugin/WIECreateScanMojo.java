/*******************************************************************************
 * (c) Copyright 2017 Hewlett Packard Enterprise Development LP
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
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
package com.fortify.wie.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fortify.wie.WIERestConnection.ScanData;

/**
 * Mojo for creating a WebInspect Enterprise scan and running it
 * 
 * @author Ruud Senden
 *
 */
@Mojo(name = "wieCreateScan", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
public class WIECreateScanMojo extends AbstractWIEMojo {
	@Parameter(property = "com.fortify.wie.scan.name", required = true)
	private String scanName;
	
	@Parameter(property = "com.fortify.wie.scan.policyId", required = true)
	private String policyId;
	
	@Parameter(property = "com.fortify.wie.scan.priority", required = true, defaultValue="3")
	private int priority;
	
	@Parameter(property = "com.fortify.wie.scan.siteId", required = true)
	private String siteId;
	
	@Parameter(property = "com.fortify.wie.scan.settingsFileId", required = false, defaultValue = "${com.fortify.wie.scan.settingsFileId}")
	private String settingsFileId;
	
	@Parameter(property = "com.fortify.wie.scan.startUri", required = false)
	private String startUri;
	
	@Parameter(property = "com.fortify.wie.scan.templateId", required = false)
	private String templateId;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		ScanData scanData = new ScanData().scanName(scanName).policyId(policyId)
				.priority(priority).siteId(siteId).settingsFileId(settingsFileId)
				.startUri(startUri).scanTemplateId(templateId);
		getWIEConnection().createScan(scanData);
	}

}
