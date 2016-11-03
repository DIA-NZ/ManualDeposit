/**
 * Software License
 *
 * Copyright 2007/2010 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package nz.govt.natlib.ndha.manualdeposit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import nz.govt.natlib.ndha.common.PropertiesUtil;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidApplicationDataException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.InvalidCMSSystemException;
import nz.govt.natlib.ndha.manualdeposit.exceptions.LoginException;
import nz.govt.natlib.ndha.manualdeposit.metadata.ApplicationData;
import nz.govt.natlib.ndha.manualdeposit.metadata.IndigoUser;
import nz.govt.natlib.ndha.manualdeposit.metadata.UserData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AppProperties {

	private final static String PROP_APP_PROPS_FILE = "ApplicationPropertiesFile";
	private final static String PROP_USER_DATA_FILE = "UserDataFile";
	private final static String PROP_DEFAULT_USER = "DefaultUserName";
	private final static String PROP_APP_VERSION = "ApplicationVersion";
	private final static Log LOG = LogFactory.getLog(AppProperties.class);

	private String appPropertiesFile = "";
	private String userDataFile = "";
	private String appVersion = "";
	private ApplicationData appData = null;
	private UserData userData = null;
	private String loggedOnUser = null;
	private String loggedOnUserPassword = null;
	private String systemProxyUser = "";
	private String systemProxyPassword = "";

	public AppProperties() throws IOException {
		loadProperties();
	}

	public String getAppPropertiesFile() {
		return appPropertiesFile;
	}

	public String getUserDataFile() {
		return userDataFile;
	}

	public ApplicationData getApplicationData() {
		return appData;
	}

	public UserData getUserData() {
		return userData;
	}

	public String getLoggedOnUser() {
		return loggedOnUser;
	}
	
	public String getAppVersion() {
		return appVersion;
	}

	public void setLoggedOnUser(final String value) throws LoginException,
			FileNotFoundException, InvalidApplicationDataException,
			InvalidCMSSystemException {
		final IndigoUser user = userData.getUser(value);
		if (user == null) {
			throw new LoginException(
					"Login name not found in the configuration file");
		}
		user.getUserGroupData().loadStructureMapFileTypes(true);
		loggedOnUser = value;

	}

	public void clearLoggedOnUser() {
		loggedOnUser = null;
	}

	public String getLoggedOnUserPassword() {
		return loggedOnUserPassword;
	}

	public void setLoggedOnUserPassword(final String value) {
		loggedOnUserPassword = value;
	}
	
	public String getSysProxyUser() {
		return systemProxyUser;
	}
	
	public void setSysProxyUser(String value) {
		systemProxyUser = value;
	}
	
	public String getSysProxyPassword() {
		return systemProxyPassword;
	}
	
	public void setSysProxyPassword(String value) {
		systemProxyPassword = value;
	}

	@SuppressWarnings("unchecked")
	private void loadProperties() throws IOException {
		Properties props;
		final String propertiesPath = "./Application.properties";
		final ClassLoader contextClassLoader = PropertiesUtil
				.getContextClassLoaderInternal();
		final Enumeration urls = PropertiesUtil.getResources(
				contextClassLoader, propertiesPath);
		final String unfoundFileMessage = "Couldn't find properties file: "
				+ propertiesPath;
		if ((urls == null) || (!urls.hasMoreElements())) {
			throw new FileNotFoundException(unfoundFileMessage);
		}
		final URL propertiesURL = (URL) urls.nextElement();
		LOG.debug("AppProperties.loadProperties Properties Path: "
				+ propertiesURL.toString());
		props = PropertiesUtil.getProperties(propertiesURL);
		if (props == null) {
			throw new FileNotFoundException(unfoundFileMessage);
		}

		if (props.get(PROP_APP_PROPS_FILE) != null) {
			appPropertiesFile = props.get(PROP_APP_PROPS_FILE).toString();
			LOG.debug("AppProperties.loadProperties _appPropertiesFile: "
					+ appPropertiesFile);
			appData = ApplicationData.getInstance(appPropertiesFile);
		}
		if (props.get(PROP_USER_DATA_FILE) != null) {
			try {
				userDataFile = props.get(PROP_USER_DATA_FILE).toString();
				userData = UserData.create(userDataFile);
			} catch (Exception ex) {
			}
		}
		if (props.get(PROP_DEFAULT_USER) != null) {
			try {
				loggedOnUser = props.get(PROP_DEFAULT_USER).toString();
			} catch (Exception ex) {
			}
		}
		if (props.get(PROP_APP_VERSION) != null) {
			try {
				appVersion = props.get(PROP_APP_VERSION).toString();
				LOG.debug("Indigo Application version is: " + appVersion);
			} catch (Exception ex) {
			}
		}

	}
}
