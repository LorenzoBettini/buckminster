/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.common.parser;

import org.eclipse.buckminster.core.common.model.Documentation;
import org.eclipse.buckminster.core.common.model.Flow;
import org.eclipse.buckminster.sax.AbstractHandler;
import org.eclipse.buckminster.sax.ISaxableElement;

/**
 * @author Thomas Hallgren
 * 
 */
public class DocumentationHandler extends FlowHandler {
	public static final String TAG = Documentation.BM_TAG;

	public DocumentationHandler(AbstractHandler parentHandler) {
		super(parentHandler, TAG);
	}

	public Documentation createDocumentation() {
		return (Documentation) this.createElement();
	}

	@Override
	Flow createFlowElement(String localName, String[] keyValuePairs, ISaxableElement[] children) {
		return new Documentation(children, keyValuePairs);
	}
}
