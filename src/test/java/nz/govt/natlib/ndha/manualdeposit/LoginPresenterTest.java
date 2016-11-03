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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nz.govt.natlib.ndha.manualdeposit.login.ILoginListener;
import nz.govt.natlib.ndha.manualdeposit.login.ILoginPresenter;
import nz.govt.natlib.ndha.manualdeposit.login.LoginEvent;
import nz.govt.natlib.ndha.manualdeposit.login.LoginListener;
import nz.govt.natlib.ndha.manualdeposit.login.LoginPresenter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

public class LoginPresenterTest implements ILoginPresenter {

	private final static Log LOG = LogFactory.getLog(LoginPresenterTest.class);
	private ILoginPresenter loginPresenter;
	private LoginTest loginFrame;
	private AppProperties applicationProperties;
	private boolean testingFailure = false;
	private boolean failed = false;

	private List<ILoginListener> loginListeners = new ArrayList<ILoginListener>();

	/**
	 * These first methods are stub implementations of ILoginPresenter THEY MUST
	 * NOT BE MODIFIED WITHOUT CHECKING HOW IT WILL AFFECT MANUAL DEPOSIT TESTS
	 * They are not intended to be part of testing the actual login presenter
	 */
	public void setup() {
	}

	public void login(final String userName, final String password) {
		for (ILoginListener listener : loginListeners) {
			final LoginEvent evt = new LoginEvent(listener, userName, password,
					"");
			listener.loginSucceeded(evt);
		}
	}

	public void cancelLogin() {
	}

	public void resetLoginAttempts() {
	}

	public void clearLoginListeners() {
		loginListeners = new ArrayList<ILoginListener>();
	}

	public void addLoginListener(final ILoginListener listener) {
		loginListeners.add(listener);
	}

	public int getLoginListenerCount() {
		return loginListeners.size();
	}

	/**
	 * These first methods are stub implementations of ILoginPresenter THEY MUST
	 * NOT BE MODIFIED WITHOUT CHECKING HOW IT WILL AFFECT MANUAL DEPOSIT TESTS
	 * They are not intended to be part of testing the actual login presenter
	 */

	@Before
	public void before() {
		final File f = new File("./");
		LOG.debug("LoginPresenterTest.before, current dir: "
				+ f.getAbsolutePath());
		loginFrame = new LoginTest();
		try {
			applicationProperties = new AppProperties();
			LOG
					.debug("LoginPresenterTest.before loaded app properties, XML file: "
							+ applicationProperties.getAppPropertiesFile());
			loginPresenter = new LoginPresenter(loginFrame,
					applicationProperties);
		} catch (Exception ex) {
			LOG.debug("LoginPresenterTest.before Failed");
			fail();
		}
	}

	@Test
	public final void testSetup() {
		LOG.debug("LoginPresenterTest.testSetup, XML file: "
				+ applicationProperties.getAppPropertiesFile());
		assertFalse("Login fram should be invisible", loginFrame.getIsVisible());
		loginPresenter.setup();
		assertTrue("Login fram should be visible", loginFrame.getIsVisible());
		LOG.debug("LoginPresenterTest.testSetup end, XML file: "
				+ applicationProperties.getAppPropertiesFile());
	}

	@Test
	public final void testLogin() {
		loginPresenter.addLoginListener(new LoginListener() {
			public void loginFailed(final LoginEvent e) {
				assertTrue(testingFailure);
				assertFalse(e.getErrorMessage().equals(""));
				assertFalse(e.getErrorMessage() == null);
				assertTrue(e.getErrorMessage() != null);
				failed = true;
			}

			public void loginSucceeded(LoginEvent e) {
				assertFalse(testingFailure);
				assertTrue(e.getLoginName().equals("mngroot"));
				assertTrue(e.getLoginPassword().equals("mngroot"));
				assertTrue(e.getErrorMessage() == null);
				assertFalse(loginFrame.getIsVisible());
				loginFrame.setIsVisible(true);
			}
		});
		loginFrame.setIsVisible(true);
		testingFailure = false;
		loginPresenter.login("mngroot", "mngroot");

		loginFrame.setErrorFails(false);
		testingFailure = true;
		loginFrame.setErrorMessage(null);
		failed = false;
		loginPresenter.resetLoginAttempts();
		loginPresenter.login("", "rubbish");
		assertTrue(loginFrame.getErrorMessage() != null);
		try {
			Thread.sleep(100);
		} catch (Exception ex) {
		}
		assertFalse(failed);

		loginFrame.setErrorMessage(null);
		failed = false;
		loginPresenter.login("", "rubbish");
		assertTrue(loginFrame.getErrorMessage() != null);
		try {
			Thread.sleep(100);
		} catch (Exception ex) {
		}
		assertFalse(failed);

		loginFrame.setErrorMessage(null);
		failed = false;
		loginPresenter.login("", "rubbish");
		assertTrue(loginFrame.getErrorMessage() == null);
		try {
			Thread.sleep(100);
		} catch (Exception ex) {
		}
		assertTrue(failed); // should fail after three attempts

		loginFrame.setErrorMessage(null);
		failed = false;
		loginPresenter.cancelLogin();
		assertTrue(loginFrame.getErrorMessage() == null);
		try {
			Thread.sleep(100);
		} catch (Exception ex) {
		}
		assertFalse(loginFrame.getIsVisible());
		assertTrue(failed);
	}
}
