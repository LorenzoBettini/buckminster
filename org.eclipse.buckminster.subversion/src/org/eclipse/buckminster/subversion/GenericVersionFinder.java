package org.eclipse.buckminster.subversion;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.buckminster.core.RMContext;
import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.resolver.NodeQuery;
import org.eclipse.buckminster.core.rmap.model.Provider;
import org.eclipse.buckminster.core.version.AbstractSCCSVersionFinder;
import org.eclipse.buckminster.core.version.VersionMatch;
import org.eclipse.buckminster.core.version.VersionSelector;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class GenericVersionFinder<SVN_ENTRY_TYPE, SVN_REVISION_TYPE> extends AbstractSCCSVersionFinder {
	private final ISubversionSession<SVN_ENTRY_TYPE, SVN_REVISION_TYPE> session;

	private final ISvnEntryHelper<SVN_ENTRY_TYPE> helper;

	public GenericVersionFinder(Provider provider, IComponentType ctype, NodeQuery query) throws CoreException {
		super(provider, ctype, query);
		session = getSession(provider.getURI(query.getProperties()), null, query.getNumericRevision(), query.getTimestamp(), query.getContext());
		helper = session.getSvnEntryHelper();
	}

	@Override
	final public void close() {
		session.close();
	}

	@Override
	final protected boolean checkComponentExistence(VersionMatch versionMatch, IProgressMonitor monitor) throws CoreException {
		final NodeQuery query = getQuery();
		final String uri = getProvider().getURI(query.getProperties());
		final VersionSelector branchOrTag = versionMatch.getBranchOrTag();
		final long revision = versionMatch.getNumericRevision();
		final Date timestamp = versionMatch.getTimestamp();
		final RMContext context = query.getContext();
		final ISubversionSession<SVN_ENTRY_TYPE, SVN_REVISION_TYPE> checkerSession = getSession(uri, branchOrTag, revision, timestamp, context);
		try {
			// We list the folder rather then just obtaining the entry since the
			// listing
			// is cached. It is very likely that we save a call later.
			//
			final URI url = checkerSession.getSVNUrl();
			return checkerSession.listFolder(url, monitor).length > 0;
		} finally {
			checkerSession.close();
		}
	}

	@Override
	final protected List<RevisionEntry> getBranchesOrTags(boolean branches, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(null, 200);
		try {
			if (!session.hasTrunkStructure())
				return Collections.emptyList();

			URI url = session.getSVNRootUrl(branches);
			SVN_ENTRY_TYPE[] list = session.listFolder(url, monitor);
			if (list.length == 0)
				return Collections.emptyList();

			ArrayList<RevisionEntry> entries = new ArrayList<RevisionEntry>(list.length);
			for (SVN_ENTRY_TYPE e : list) {
				final String path = helper.getEntryPath(e);
				final long revision = helper.getEntryRevisionNumber(e);
				entries.add(new RevisionEntry(path, null, revision));
			}
			return entries;
		} finally {
			monitor.done();
		}
	}

	/**
	 * Implemented by subclasses. Used to retrieve a particular a concrete
	 * session instance.
	 * 
	 * @param repositoryURI
	 * @param branchOrTag
	 * @param revision
	 * @param timestamp
	 * @param context
	 * @return
	 * @throws CoreException
	 */
	protected abstract ISubversionSession<SVN_ENTRY_TYPE, SVN_REVISION_TYPE> getSession(String repositoryURI, VersionSelector branchOrTag,
			long revision, Date timestamp, RMContext context) throws CoreException;

	@Override
	final protected RevisionEntry getTrunk(IProgressMonitor monitor) throws CoreException {
		SVN_ENTRY_TYPE entry = session.getRootEntry(monitor);
		return entry == null ? null : new RevisionEntry(null, null, session.getSvnEntryHelper().getEntryRevisionNumber(entry));
	}
}
