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
package com.fortify.wi;

import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.apache.http.auth.Credentials;

import com.fortify.util.json.JSONMap;
import com.fortify.util.rest.RestConnection;

/**
 * This {@link RestConnection} implementation provides various
 * methods for working with the WebInspect stand-alone API.
 * 
 * @author Ruud Senden
 *
 */
public class WIRestConnection extends RestConnection {
	
	public WIRestConnection(String baseUrl, Credentials credentials, Map<String, Object> connectionProperties) {
		super(baseUrl, credentials, connectionProperties);
	}
	
	public JSONMap createProxy(String instanceId, String address, int port) {
		JSONMap entity = new JSONMap();
		entity.put("port", port);
		entity.put("address", address);
		entity.put("instanceId", instanceId);
		return executeRequest(HttpMethod.POST, 
				getBaseResource().path("/proxy"), 
				Entity.entity(entity, MediaType.APPLICATION_JSON), JSONMap.class);
	}
	
	public JSONMap deleteProxy(String instanceId) {
		return executeRequest(HttpMethod.DELETE, 
				getBaseResource().path("/proxy/{instanceId}").resolveTemplate("instanceId", instanceId), 
				JSONMap.class);
	}
	
	public JSONMap saveProxyTrafficOnServer(String instanceId, String extension, String action) {
		return executeRequest(HttpMethod.PUT, 
				getBaseResource().path("/proxy/{instanceId}.{extension}")
				.resolveTemplate("instanceId", instanceId)
				.resolveTemplate("extension", extension)
				.queryParam("action", action), 
				Entity.entity("", MediaType.APPLICATION_JSON), JSONMap.class);
	}
	
	public void saveProxyTraffic(String instanceId, String extension, Path outputPath, CopyOption... copyOptions) {
		executeRequestAndSaveResponse(HttpMethod.GET, 
				getBaseResource().path("/proxy/{instanceId}.{extension}")
				.resolveTemplate("instanceId", instanceId)
				.resolveTemplate("extension", extension), outputPath, copyOptions);
	}
	
	public JSONMap createScan(JSONMap scanData) {
		return executeRequest(HttpMethod.POST, getBaseResource().path("/scanner/scans"),
				Entity.entity(scanData, MediaType.APPLICATION_JSON), JSONMap.class);
	}
	
	public void saveScan(String scanId, String extension, String detailType, Path outputPath, CopyOption... copyOptions) {
		executeRequestAndSaveResponse(HttpMethod.GET, getBaseResource().path("/scanner/scans/{scanId}.{extension}")
				.resolveTemplate("scanId", scanId)
				.resolveTemplate("extension", extension), outputPath, copyOptions);
	}
	
	
}
