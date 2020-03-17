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
package com.fortify.integration.maven.webinspect.standalone.scan;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.fortify.client.webinspect.api.WebInspectScanAPI;
import com.fortify.integration.maven.webinspect.standalone.AbstractWIMojo;
import com.fortify.util.rest.json.JSONMap;

/**
 * Mojo for creating a WebInspect stand-alone scan and optionally running it
 * 
 * @author Ruud Senden
 *
 */
@Mojo(name = "wiCreateScan", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
public class WICreateScanMojo extends AbstractWIMojo {
	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;
	
	public enum CrawlAuditMode {
		CrawlOnly, AuditOnly, CrawlAndAudit
	}

	public enum ScanScope {
		Children, Ancestors, Self, Unrestricted;
	}

	public enum StartOption {
		Url, Macro;
	}

	/**
	 * ScanName - any alpha-numeric value, does not need to be unique.
	 * ("ScanName":"My Scan Name 123")
	 */
	@Parameter(property = "com.fortify.webinspect.scan.name", required = true)
	private String scanName;

	/**
	 * CrawlAuditMode - modes are: CrawlOnly, AuditOnly, CrawlAndAudit
	 * ("CrawlAuditMode":"CrawlAndAudit")
	 */
	@Parameter(property = "com.fortify.webinspect.scan.crawlAuditMode", required = true, defaultValue = "AuditOnly")
	private CrawlAuditMode crawlAuditMode;

	/**
	 * ScanScope - options are: Unrestricted, Self, Children, Ancestors. If
	 * ScopedPaths is specified, ScanScope must also be specified.
	 * ("ScanScope":"Unrestricted")
	 */
	@Parameter(property = "com.fortify.webinspect.scan.scope", required = true, defaultValue = "Children")
	private ScanScope scanScope;

	/**
	 * ScopedPaths - list of folder restrictions. Paths found in StartUrls are
	 * automatically added to the ScopedPaths list. This setting interacts with
	 * ScanScope to determine which paths will be crawled during a scan. If
	 * ScopedPaths is specified, ScanScope must also be specified.
	 * ("ScopedPaths":["/ancestor/path1/","/ancestor/path2/"])
	 */
	@Parameter(property = "com.fortify.webinspect.scan.scopedPaths")
	private String[] scopedPaths;

	/**
	 * StartOption - options are: Url, Macro ("StartOption":"Url")
	 */
	@Parameter(property = "com.fortify.webinspect.scan.startOption", required = true, defaultValue = "Macro")
	private StartOption startOption;

	/**
	 * LoginMacro - a webmacro file name. This file must exist in the WebInspect
	 * scan settings folder on the WebInspect machine. ("LoginMacro":"mylogin")
	 */
	@Parameter(property = "com.fortify.webinspect.scan.loginMacro")
	private String loginMacro;

	/**
	 * StartUrls - a list of valid, fully qualified urls, including scheme, host
	 * and port. This field is only used if StartOption is Url.
	 * ("StartUrls":["http://myhost:80/some/path/login.php",
	 * "http://anotherhost:80/index.html"])
	 */
	@Parameter(property="com.fortify.webinspect.scan.startUrls")
	private String[] startUrls;

	/**
	 * AllowedHosts - an array of allowed host:port entries. Hosts found in
	 * StartUrls are automatically added to the allowed hosts list. Use the
	 * special value "*" to add all hosts found in workflow macros, login macros
	 * and start urls to allowed hosts. Caution! This is a convenience feature,
	 * and it can cause the scanner to go out of scope.
	 * ("AllowedHosts":["zero.webappsecurity.com:80",
	 * "zero.webappsecurity.com:443", "myhost:8888"]) or ("AllowedHosts":["\*"])
	 */
	@Parameter(property="com.fortify.webinspect.scan.allowedHosts")
	private String[] allowedHosts;

	/**
	 * WorkflowMacros - an array of webmacro file names to be used as workflow
	 * macros. These files must exist in the WebInspect scan settings folder on
	 * the WebInspect machine. ("WorkflowMacros":["workflow1","workflow2"])
	 */
	@Parameter(property="com.fortify.webinspect.scan.workflowMacros")
	private String[] workflowMacros;

	/**
	 * DontStartScan - options are: true, false. If true, the scan will be
	 * created using the settingsName and overrides options, but it will remain
	 * in the stopped state. ("DontStartScan":true)
	 */
	@Parameter(property="com.fortify.webinspect.scan.dontStart")
	private boolean dontStartScan;
	
	/**
	 * SettingsName - specify the scan settings to use 
	 */
	@Parameter(property="com.fortify.webinspect.scan.settingsName", required=true, defaultValue="Default")
	private String settingsName;

	public String getScanName() {
		return scanName;
	}

	public CrawlAuditMode getCrawlAuditMode() {
		return crawlAuditMode;
	}

	public ScanScope getScanScope() {
		return scanScope;
	}

	public String[] getScopedPaths() {
		return scopedPaths;
	}

	public StartOption getStartOption() {
		return startOption;
	}

	public String getLoginMacro() {
		return loginMacro;
	}

	public String[] getStartUrls() {
		return startUrls;
	}

	public String[] getAllowedHosts() {
		return allowedHosts;
	}

	public String[] getWorkflowMacros() {
		return workflowMacros;
	}

	public boolean isDontStartScan() {
		return dontStartScan;
	}
	
	public String getSettingsName() {
		return settingsName;
	}

	protected JSONMap submitWebInspectScan() {
		return getWebInspectConnection().api(WebInspectScanAPI.class).createScan(getEntity());
	}

	protected JSONMap getEntity() {
		JSONMap entity = new JSONMap();
		entity.putPath("settingsName", getSettingsName());
		entity.putPath("overrides.scanName", getScanName());
		entity.putPath("overrides.crawlAuditMode", getCrawlAuditMode());
		entity.putPath("overrides.startOption", getStartOption());
		entity.putPath("overrides.startUrls", getStartUrls());
		entity.putPath("overrides.workflowMacros", getWorkflowMacros(), true);
		entity.putPath("overrides.allowedHosts", getAllowedHosts(), true);
		entity.putPath("overrides.scopedPaths", getScopedPaths(), true);
		entity.putPath("overrides.dontStartScan", isDontStartScan(), true);
		entity.putPath("overrides.loginMacro", getLoginMacro(), true);
		

		if (!CrawlAuditMode.AuditOnly.equals(getCrawlAuditMode())) {
			entity.putPath("overrides.scanScope", getScanScope());
		}

		getLog().info("Create Scan with parameters " + entity);
		return entity;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		JSONMap result = submitWebInspectScan();
		logResult(result);
		// Make the scan id available for other Mojo's that need to access the scan
		project.getProperties().put("com.fortify.webinspect.scan.id", result.get("ScanId", String.class));
	}
}
