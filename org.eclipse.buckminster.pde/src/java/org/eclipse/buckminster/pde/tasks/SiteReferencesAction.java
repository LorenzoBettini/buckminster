/*******************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/
package org.eclipse.buckminster.pde.tasks;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.p2.publisher.AbstractPublisherAction;
import org.eclipse.equinox.p2.publisher.IPublisherInfo;
import org.eclipse.equinox.p2.publisher.IPublisherResult;
import org.eclipse.equinox.p2.publisher.eclipse.URLEntry;
import org.eclipse.equinox.p2.repository.IRepository;
import org.eclipse.equinox.p2.repository.IRepositoryReference;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.spi.RepositoryReference;

/**
 * Action which processes a feature.xml, build.properties, and feature
 * localization files and generates categories, mirrors url, and referenced
 * repositories for a p2 MDR. The process relies on IUs for the various features
 * to have already been generated.
 */
@SuppressWarnings("restriction")
public class SiteReferencesAction extends AbstractPublisherAction {
	private final URLEntry[] urlEntries;

	public SiteReferencesAction(URLEntry[] urlEntries) {
		this.urlEntries = urlEntries;
	}

	@Override
	public IStatus perform(IPublisherInfo publisherInfo, IPublisherResult results, IProgressMonitor monitor) {
		// publish the top feature discovery sites as repository references
		IMetadataRepository mdr = publisherInfo.getMetadataRepository();
		for (URLEntry refSite : urlEntries)
			generateSiteReference(refSite.getURL(), refSite.getAnnotation(), null, mdr);
		return Status.OK_STATUS;
	}

	private void generateSiteReference(String location, String label, String featureId, IMetadataRepository metadataRepo) {
		if (metadataRepo == null)
			return;
		try {
			URI associateLocation = new URI(location);
			ArrayList<IRepositoryReference> references = new ArrayList<IRepositoryReference>(2);
			references.add(new RepositoryReference(associateLocation, label, IRepository.TYPE_METADATA, IRepository.ENABLED));
			references.add(new RepositoryReference(associateLocation, label, IRepository.TYPE_ARTIFACT, IRepository.ENABLED));
			metadataRepo.addReferences(references);
		} catch (URISyntaxException e) {
		}
	}
}
