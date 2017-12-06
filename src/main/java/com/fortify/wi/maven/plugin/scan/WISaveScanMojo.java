/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC
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
package com.fortify.wi.maven.plugin.scan;

import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fortify.wi.maven.plugin.AbstractWIMojo;

/**
 * Mojo for saving a WebInspect scan to local disk
 * 
 * @author Ruud Senden
 *
 */
@Mojo(name = "wiSaveScan", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
public class WISaveScanMojo extends AbstractWIMojo {

	// We specify default value as discussed here: https://stackoverflow.com/questions/4061386/maven-how-to-pass-parameters-between-mojos
	@Parameter(property = "com.fortify.webinspect.scan.id", required = true, defaultValue="${com.fortify.webinspect.scan.id}")
	private String scanId;
	@Parameter(property = "com.fortify.webinspect.scan.extension", required = true, defaultValue = "settings")
	private String extension;
	@Parameter(property = "com.fortify.webinspect.scan.detailType", required = false)
	private String detailType;
	@Parameter(property = "com.fortify.webinspect.scan.outputFile", required = true)
	private String outputFile;
	@Parameter(property = "com.fortify.webinspect.scan.replaceExistingOutputFile", required = false, defaultValue = "true")
	private boolean replaceExistingOutputFile;
	

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		CopyOption[] copyOptions = new CopyOption[]{};
		if ( replaceExistingOutputFile ) {
			copyOptions = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
		}
		getWebInspectConnection().api().scanner().saveScan(scanId, extension, detailType, Paths.get(outputFile), copyOptions);
	}
}
