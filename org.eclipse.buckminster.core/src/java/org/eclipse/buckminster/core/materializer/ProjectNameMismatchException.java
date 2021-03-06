/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.materializer;

import org.eclipse.buckminster.core.Messages;
import org.eclipse.buckminster.core.helpers.LocalizedException;
import org.eclipse.osgi.util.NLS;

/**
 * @author Thomas Hallgren
 */
public class ProjectNameMismatchException extends LocalizedException {
	private static final long serialVersionUID = -2168949402426015793L;

	public ProjectNameMismatchException(String wantedName, String existingName) {
		super(NLS.bind(Messages.ProjectBinding_name_conflict_information_indicates_0_for_project_named_1, wantedName, existingName));
	}
}
