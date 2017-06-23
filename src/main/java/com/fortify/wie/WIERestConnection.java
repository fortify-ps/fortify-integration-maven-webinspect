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
package com.fortify.wie;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.http.auth.Credentials;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.fortify.util.json.JSONMap;
import com.fortify.util.rest.RestConnection;

/**
 * This {@link RestConnection} implementation provides various
 * methods for working with the WebInspect Enterprise API.
 * 
 * @author Ruud Senden
 *
 */
public class WIERestConnection extends RestConnection {
	private final Credentials credentials;
	private String apiKey = null;
	
	public WIERestConnection(String baseUrl, Credentials credentials, Map<String, Object> connectionProperties) {
		super(baseUrl, null, connectionProperties);
		this.credentials = credentials;
	}
	
	@Override
	protected WebTarget updateWebTarget(WebTarget webTarget) {
		webTarget = super.updateWebTarget(webTarget);
		if ( apiKey == null ) {
			if ( credentials==null ) {
				throw new IllegalStateException("No WIE credentials have been configured");
			}
			JSONMap auth = new JSONMap();
			auth.put("username", credentials.getUserPrincipal().getName());
			auth.put("password", credentials.getPassword());
			apiKey = new RestConnection(getBaseUrl(), getConnectionProperties())
					.executeRequest(HttpMethod.POST, getBaseResource().path("/api/v1/auth"),
							Entity.entity(auth, MediaType.APPLICATION_JSON), JSONMap.class)
					.get("data", String.class);
		}
		return webTarget.queryParam("api_key", apiKey);
	}
	
	public String uploadScanSettings(File file) {
		String uuid = UUID.randomUUID().toString();
		uploadTempFile(uuid, uuid, 5, file);
		return uuid;
	}
	
	public void uploadTempFile(String sessionId, String fileId, int fileType, File file) {
		JSONMap request = new JSONMap();
		request.put("sessionID", sessionId);
		request.put("fileId", fileId);
		request.put("fileName", file.getName());
		request.put("fileType", fileType);
		executeRequest(HttpMethod.POST, getBaseResource().path("/api/v1/tempFile"),
				Entity.entity(request, MediaType.APPLICATION_JSON), null);
		
        MultiPart multiPart = new FormDataMultiPart();
        try {
			multiPart.type(new MediaType("multipart", "form-data",
		    		Collections.singletonMap(Boundary.BOUNDARY_PARAMETER, Boundary.createBoundary())));
			//multiPart.bodyPart(new FormDataBodyPart("Filename", file.getName()));
			multiPart.bodyPart(new FileDataBodyPart("data", file, MediaType.APPLICATION_OCTET_STREAM_TYPE));
			executeRequest(HttpMethod.POST, getBaseResource().path("/api/v1/tempFile/{id}/fileData")
					.resolveTemplate("id", fileId), Entity.entity(multiPart, multiPart.getMediaType()), null);
        } finally {
        	try {
				multiPart.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	public JSONMap createScan(ScanData scanData) {
		System.out.println(scanData.getJsonMap());
		return executeRequest(HttpMethod.POST, getBaseResource().path("/api/v1/scans"), 
			Entity.entity(scanData.getJsonMap(), MediaType.APPLICATION_JSON), JSONMap.class);
	}
	
	@Override
	protected ClientConfig createClientConfig() {
		// We require the MultiPartFeature to upload files to WIE
		return super.createClientConfig().register(MultiPartFeature.class);
	}
	
	public static final class ScanData { 
		private JSONMap jsonMap = new JSONMap();
		
		protected JSONMap getJsonMap() {
			return jsonMap;
		}
		
		private ScanData putPath(String path, Object value) {
			jsonMap.putPath(path, value, true);
			return this;
		}
		
		public ScanData scanName(String value) {
			return putPath("name", value);
		}
		
		public ScanData projectName(String value) {
			return putPath("project.name", value);
		}
		
		public ScanData projectVersionName(String value) {
			return putPath("projectVersion.name", value);
		}
		
		public ScanData siteId(String value) {
			return putPath("projectVersion.siteId", value);
		}
		
		public ScanData priority(int value) {
			return putPath("priority", value);
		}
		
		public ScanData policyId(String value) {
			return putPath("policy.id", value);
		}
		
		public ScanData sensorId(String value) {
			return putPath("sensor.id", value);
		}
		
		public ScanData settingsFileId(String value) {
			return putPath("fileID", value);
		}
		
		public ScanData scanTemplateId(String value) {
			return putPath("scanTemplateId", value);
		}
		
		public ScanData startUri(String value) {
			return putPath("startURI", value);
		}

	}
}
