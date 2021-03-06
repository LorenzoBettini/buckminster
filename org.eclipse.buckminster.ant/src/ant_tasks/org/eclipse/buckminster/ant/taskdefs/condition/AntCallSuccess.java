/**************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ***************************************************************************/
package org.eclipse.buckminster.ant.taskdefs.condition;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.CallTarget;
import org.apache.tools.ant.taskdefs.condition.Condition;

/**
 * @author Thomas Hallgren
 */
public class AntCallSuccess extends CallTarget implements Condition {
	private String trapPattern;

	public boolean eval() throws BuildException {
		Project p = getProject();

		if (trapPattern == null)
			throw new BuildException("Missing required attribute 'trapPattern'", getLocation());

		boolean wasKeepGoing = p.isKeepGoingMode();
		p.setKeepGoingMode(true);

		Pattern _trapPattern;
		try {
			_trapPattern = Pattern.compile(trapPattern);

		} catch (PatternSyntaxException e) {
			throw new BuildException("Attribute 'trapPattern' is an invalid regexp: ", e, getLocation());
		}

		try {
			execute();
			return true;
		} catch (BuildException e) {
			String msg = e.getMessage();
			if (msg != null) {
				if (_trapPattern.matcher(msg).find())
					return false;
			}
			throw e;
		} finally {
			p.setKeepGoingMode(wasKeepGoing);
		}
	}

	public void setTrapPattern(String trapPattern) {
		this.trapPattern = trapPattern;
	}
}
