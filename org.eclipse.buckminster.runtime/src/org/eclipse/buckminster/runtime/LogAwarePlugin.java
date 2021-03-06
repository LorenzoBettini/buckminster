/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.runtime;

import org.eclipse.core.runtime.Plugin;

/**
 * @author Thomas Hallgren
 */
public class LogAwarePlugin extends Plugin {
	private Logger logger;

	public synchronized Logger getBundleLogger() {
		if (logger == null)
			logger = new Logger(this.getBundle());
		return logger;
	}
}
