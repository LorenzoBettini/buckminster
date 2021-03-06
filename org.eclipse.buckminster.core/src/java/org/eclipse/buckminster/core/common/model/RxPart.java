/*******************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.core.common.model;

import java.util.List;

import org.eclipse.buckminster.sax.AbstractSaxableElement;
import org.eclipse.buckminster.sax.Utils;
import org.eclipse.core.runtime.CoreException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Thomas Hallgren
 */
public abstract class RxPart extends AbstractSaxableElement {
	public static final String ATTR_NAME = "name"; //$NON-NLS-1$

	public static final String ATTR_OPTIONAL = "optional"; //$NON-NLS-1$

	private final String name;

	private final boolean optional;

	protected RxPart(String name, boolean optional) {
		this.name = name;
		this.optional = optional;
	}

	public abstract void addPattern(StringBuilder bld, List<RxPart> namedParts) throws CoreException;

	public String getName() {
		return name;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean isTagged() {
		return false;
	}

	@Override
	protected void addAttributes(AttributesImpl attrs) {
		if (!(isTagged() || name == null))
			Utils.addAttribute(attrs, ATTR_NAME, name);
		if (optional)
			Utils.addAttribute(attrs, ATTR_OPTIONAL, Boolean.TRUE.toString());
	}
}
