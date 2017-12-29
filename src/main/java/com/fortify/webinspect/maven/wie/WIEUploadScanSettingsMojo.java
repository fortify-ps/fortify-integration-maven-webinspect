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
package com.fortify.webinspect.maven.wie;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Mojo for uploading scan settings to WebInspect Enterprise
 * 
 * @author Ruud Senden
 *
 */
@Mojo(name = "wieUploadScanSettings", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
public class WIEUploadScanSettingsMojo extends AbstractWIEMojo {
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;
	
	@Parameter(property = "com.fortify.wie.scan.settingsFile", required = true)
	private File scanSettingsFile;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String fileId = getWIEConnection().api().scan().uploadScanSettings(scanSettingsFile);
		logResult(fileId);
		// Make the file id available for other Mojo's that need to access the settings file
		project.getProperties().put("com.fortify.wie.scan.settingsFileId", fileId);
	}

}
