/*******************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.core.internal.ctype;

import java.util.Collections;

import org.eclipse.buckminster.core.cspec.AbstractResolutionBuilder;
import org.eclipse.buckminster.core.metadata.model.BOMNode;
import org.eclipse.buckminster.core.metadata.model.ResolvedNode;
import org.eclipse.buckminster.core.reader.IComponentReader;
import org.eclipse.buckminster.core.version.ProviderMatch;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class URIMatcherBuilder extends AbstractResolutionBuilder {
	private final ProviderMatch pm;

	public URIMatcherBuilder(ProviderMatch pm) {
		this.pm = pm;
	}

	@Override
	public BOMNode build(IComponentReader[] rdr, boolean forResolutionAidOnly, IProgressMonitor mon) throws CoreException {
		MonitorUtils.complete(mon);
		return new ResolvedNode(pm.getProvider().getURIMatcher().createResolution(pm), Collections.<BOMNode> emptyList());
	}
}
