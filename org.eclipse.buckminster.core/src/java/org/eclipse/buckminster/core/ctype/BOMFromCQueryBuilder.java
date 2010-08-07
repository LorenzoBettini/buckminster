/*****************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.ctype;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.cspec.AbstractResolutionBuilder;
import org.eclipse.buckminster.core.metadata.model.BOMNode;
import org.eclipse.buckminster.core.metadata.model.BillOfMaterials;
import org.eclipse.buckminster.core.metadata.model.UnresolvedNodeException;
import org.eclipse.buckminster.core.query.model.ComponentQuery;
import org.eclipse.buckminster.core.reader.ICatalogReader;
import org.eclipse.buckminster.core.reader.IComponentReader;
import org.eclipse.buckminster.core.reader.IFileReader;
import org.eclipse.buckminster.core.reader.IStreamConsumer;
import org.eclipse.buckminster.core.resolver.IResolver;
import org.eclipse.buckminster.core.resolver.MainResolver;
import org.eclipse.buckminster.core.resolver.NodeQuery;
import org.eclipse.buckminster.core.resolver.ResolutionContext;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.buckminster.runtime.URLUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Thomas Hallgren
 */
public class BOMFromCQueryBuilder extends AbstractResolutionBuilder implements IStreamConsumer<ComponentQuery> {
	@Override
	public synchronized BOMNode build(IComponentReader[] readerHandle, boolean forResolutionAidOnly, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(null, 2000);
		try {
			ComponentQuery cquery;
			IComponentReader reader = readerHandle[0];

			NodeQuery query = reader.getNodeQuery();
			ResolutionContext ctx = query.getResolutionContext();
			if (reader instanceof ICatalogReader) {
				ICatalogReader catRdr = (ICatalogReader) reader;
				String fileName = getMetadataFile(catRdr, IComponentType.PREF_CQUERY_FILE, CorePlugin.CQUERY_FILE,
						MonitorUtils.subMonitor(monitor, 100));
				cquery = catRdr.readFile(fileName, this, MonitorUtils.subMonitor(monitor, 100));
			} else
				cquery = ((IFileReader) reader).readFile(this, MonitorUtils.subMonitor(monitor, 200));
			reader.close();
			readerHandle[0] = null;

			ResolutionContext newCtx = new ResolutionContext(cquery, ctx);
			IResolver resolver = new MainResolver(newCtx);
			BillOfMaterials bom = resolver.resolve(MonitorUtils.subMonitor(monitor, 1800));
			if (bom.getResolution() == null)
				throw new UnresolvedNodeException(query.getComponentRequest());
			return bom;
		} catch (IOException e) {
			throw BuckminsterException.wrap(e);
		} finally {
			monitor.done();
		}
	}

	@Override
	public ComponentQuery consumeStream(IComponentReader reader, String streamName, InputStream stream, IProgressMonitor monitor)
			throws CoreException {
		URL url;
		try {
			url = URLUtils.normalizeToURL(streamName);
		} catch (MalformedURLException e) {
			url = null;
		}
		return ComponentQuery.fromStream(url, null, stream, true);
	}
}
