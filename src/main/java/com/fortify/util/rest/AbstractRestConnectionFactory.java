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
package com.fortify.util.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.glassfish.jersey.client.ClientProperties;

import com.google.common.base.Splitter;

/**
 * This is an abstract factory for {@link RestConnection} instances,
 * providing functionality for parsing URI's with connection properties,
 * and caching a created connection.
 * 
 * @author Ruud Senden
 *
 * @param <T> Concrete type of the {@link RestConnection} managed by this class
 */
public abstract class AbstractRestConnectionFactory<T extends RestConnection> {
	private T connection;
	private URI uri;
	private Map<String, Object> connectionProperties;
	
	public T getConnection() {
		if ( connection == null ) {
			connection = createConnection();
		}
		return connection;
	}
	
	public void set(String uriWithProperties) {
		String[] parts = uriWithProperties.split(";");
		if ( parts.length > 0 ) {
			setUri(getUri(parts[0]));
			if ( parts.length > 1 ) {
				setConnectionProperties(getConnectionProperties(parts[1]));
			}
		}
	}

	private Map<String, Object> getConnectionProperties(String propertiesString) {
		Map<String, Object> properties = new HashMap<String, Object>();
		if ( StringUtils.isNotBlank(propertiesString) ) {
			properties = Collections.<String,Object>unmodifiableMap(
					Splitter.on(',').withKeyValueSeparator("=").split(propertiesString));
		}
		return properties;
	}

	private URI getUri(String uriString) {
		try {
			URI uri = new URI(uriString);
			return uri;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Input cannot be parsed as URI: "+uriString);
		}
	}
	
	protected abstract T createConnection();

	protected String getBaseUrl() {
		if ( uri == null ) {
			throw new RuntimeException("URI must be configured");
		}
		try {
			return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), null, null).toString();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Error constructing URI");
		}
	}
	
	// TODO This method probably doesn't work correctly if decoded username or password contains a ':'
	protected Credentials getCredentials() {
		String userInfo = uri.getUserInfo();
		return StringUtils.isBlank(userInfo) ? null : new UsernamePasswordCredentials(userInfo);
	}

	public Map<String, Object> getConnectionProperties() {
		return connectionProperties;
	}

	public void setConnectionProperties(Map<String, Object> connectionProperties) {
		this.connectionProperties = new HashMap<String, Object>();
		Map<String,String> propertyKeyReplacementMap = getPropertyKeyReplacementMap();
		for ( Map.Entry<String, Object> entry : connectionProperties.entrySet() ) {
			this.connectionProperties.put(propertyKeyReplacementMap.getOrDefault(entry.getKey(),  entry.getKey()), entry.getValue());
		}
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	protected Map<String, String> getPropertyKeyReplacementMap() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("connectTimeout", ClientProperties.CONNECT_TIMEOUT);
		result.put("readTimeout", ClientProperties.READ_TIMEOUT);
		return result;
	}
}
