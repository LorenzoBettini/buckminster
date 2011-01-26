/*******************************************************************************
 * Copyright (c) 2006-2008, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.pde.mapfile;

import java.util.Map;

import org.eclipse.buckminster.core.cspec.model.ComponentIdentifier;
import org.eclipse.buckminster.core.reader.IReaderType;

/**
 * @author Thomas Hallgren
 */
public class MapFileEntry {
	private final ComponentIdentifier componentIdentifier;

	private final IReaderType readerType;

	private final Map<String, String> properties;

	protected MapFileEntry(ComponentIdentifier componentIdentifier, IReaderType readerType, Map<String, String> properties) {
		this.componentIdentifier = componentIdentifier;
		this.readerType = readerType;
		this.properties = properties;
	}

	public ComponentIdentifier getComponentIdentifier() {
		return componentIdentifier;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public IReaderType getReaderType() {
		return readerType;
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		componentIdentifier.toString(bld);
		bld.append(", "); //$NON-NLS-1$
		bld.append(readerType.getId());
		bld.append(", "); //$NON-NLS-1$
		bld.append(properties);
		return bld.toString();
	}
}