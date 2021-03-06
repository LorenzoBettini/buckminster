/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.metadata.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.buckminster.core.RMContext;
import org.eclipse.buckminster.core.cspec.QualifiedDependency;
import org.eclipse.buckminster.core.cspec.model.CSpec;
import org.eclipse.buckminster.core.cspec.model.ComponentIdentifier;
import org.eclipse.buckminster.core.cspec.model.ComponentRequest;
import org.eclipse.buckminster.core.metadata.parser.ElementRefHandler;
import org.eclipse.buckminster.core.mspec.model.MaterializationSpec;
import org.eclipse.buckminster.core.query.IAdvisorNode;
import org.eclipse.buckminster.core.query.model.ComponentQuery;
import org.eclipse.buckminster.core.resolver.NodeQuery;
import org.eclipse.buckminster.sax.Utils;
import org.eclipse.core.runtime.CoreException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Thomas Hallgren
 */
public class ResolvedNode extends BOMNode {
	public static final String ATTR_RESOLUTION_ID = "resolutionId"; //$NON-NLS-1$

	public static final String CHILD_TAG = "child"; //$NON-NLS-1$

	@SuppressWarnings("hiding")
	public static final String TAG = "resolvedNode"; //$NON-NLS-1$

	private final List<BOMNode> children;

	private final Resolution resolution;

	/**
	 * This constructor will create a resolved node that has all of its children
	 * unresolved. The set of children is and how the children are qualified are
	 * determined using the attributes and prune flag defined in the
	 * <code>cquery</code>.
	 * 
	 * @param cquery
	 *            The component query whos advice will be used
	 * @param resolution
	 *            The resolution for the created resolved node
	 * @throws CoreException
	 */
	public ResolvedNode(NodeQuery query, Resolution resolution) throws CoreException {
		this.resolution = resolution;

		CSpec cspec = resolution.getCSpec().prune(query.getContext(), query.getProperties(), query.isPrune(),
				resolution.getQualifiedDependency().getAttributeNames());
		List<QualifiedDependency> qDeps = cspec.getQualifiedDependencies(query.isPrune());
		int nDeps = qDeps.size();
		if (nDeps == 0)
			children = Collections.emptyList();
		else {
			ComponentQuery cquery = query.getComponentQuery();
			List<BOMNode> childList = new ArrayList<BOMNode>(nDeps);
			for (QualifiedDependency qDep : qDeps) {
				ComponentRequest request = qDep.getRequest();
				IAdvisorNode override = cquery.getMatchingNode(request, query.getContext());
				if (override != null) {
					qDep = qDep.applyAdvice(override);
					if (qDep == null)
						//
						// We don't want anything at all from this component.
						// All attributes
						// were pruned
						//
						continue;
				}
				UnresolvedNode node = new UnresolvedNode(qDep);
				childList.add(node);
			}
			children = Collections.unmodifiableList(childList);
		}
	}

	public ResolvedNode(Resolution resolution, List<BOMNode> children) {
		this(resolution, children, false);
	}

	public ResolvedNode(Resolution resolution, List<BOMNode> children, boolean forBuild) {
		this.resolution = resolution;

		// If forBuild is true, we still wrap the list in an unmodifiableList.
		// This means
		// that only the caller of this constructor is able to modify the
		// content. We
		// don't call the createUnmodifiableList in that case though, since that
		// creates
		// a full copy of the list.
		//
		this.children = forBuild ? Collections.unmodifiableList(children) : Utils.createUnmodifiableList(children);
	}

	@Override
	public void addUnresolved(List<ComponentRequest> unresolved, Set<Resolution> skipThese) {
		if (skipThese.add(getResolution())) {
			for (BOMNode child : getChildren())
				child.addUnresolved(unresolved, skipThese);
		}
	}

	@Override
	public List<Resolution> findAll(Set<Resolution> skipThese) throws CoreException {
		HashSet<Resolution> notThese = new HashSet<Resolution>();
		if (skipThese != null)
			notThese.addAll(skipThese);
		List<Resolution> all = new ArrayList<Resolution>();
		collectAll(notThese, all);
		return all;
	}

	@Override
	public synchronized List<BOMNode> getChildren() {
		return children;
	}

	@Override
	public String getDefaultTag() {
		return TAG;
	}

	@Override
	public QualifiedDependency getQualifiedDependency() {
		return getResolution().getQualifiedDependency();
	}

	@Override
	public ComponentRequest getRequest() {
		return getResolution().getRequest();
	}

	@Override
	public synchronized Resolution getResolution() {
		return resolution;
	}

	@Override
	public String getViewName() throws CoreException {
		Resolution r = getResolution();
		StringBuilder bld = new StringBuilder();
		r.getRequest().appendViewName(bld);
		bld.append(':');
		r.getVersionMatch().toString(bld);
		return bld.toString();
	}

	/**
	 * Checks if the nodeID is equal to one of the children ids.
	 * 
	 * @param nodeID
	 *            The nodeID to check for.
	 * @return true if the nodeID was equal to one of the children ids.
	 */
	@Override
	public boolean isChild(BOMNode node) {
		for (BOMNode child : children)
			if (child.equals(node))
				return true;
		return false;
	}

	@Override
	public final boolean isReferencing(BOMNode node, boolean shallow) throws CoreException {
		if (equals(node))
			return true;

		for (BOMNode child : children)
			if (child.equals(node))
				return true;

		if (!shallow) {
			for (BOMNode child : children)
				if (child.isReferencing(node, shallow))
					return true;
		}
		return false;
	}

	@Override
	protected void addAttributes(AttributesImpl attrs) {
		Utils.addAttribute(attrs, ATTR_RESOLUTION_ID, resolution.getId().toString());
	}

	@Override
	protected void emitElements(ContentHandler receiver, String namespace, String prefix) throws SAXException {
		if (children.size() > 0) {
			String childName = Utils.makeQualifiedName(prefix, CHILD_TAG);
			for (BOMNode child : children) {
				AttributesImpl attrs = new AttributesImpl();
				Utils.addAttribute(attrs, ElementRefHandler.ATTR_REFID, child.getId().toString());
				receiver.startElement(namespace, CHILD_TAG, childName, attrs);
				receiver.endElement(namespace, CHILD_TAG, childName);
			}
		}
	}

	@Override
	protected boolean isFullyResolved(ComponentQuery query, HashSet<BOMNode> seen, Map<String, ? extends Object> properties) throws CoreException {
		if (seen.add(this)) {
			for (BOMNode child : getChildren())
				if (!child.isFullyResolved(query, seen, properties))
					return false;
		}
		return true;
	}

	@Override
	void addMaterializationCandidates(RMContext context, List<Resolution> resolutions, ComponentQuery query, MaterializationSpec mspec,
			Set<Resolution> perused) throws CoreException {
		Resolution r = getResolution();
		if (perused.add(r)) {
			for (BOMNode child : getChildren())
				child.addMaterializationCandidates(context, resolutions, query, mspec, perused);

			ComponentIdentifier ci = r.getComponentIdentifier();
			if (r.isMaterializable() && !(query.skipComponent(ci, context) || mspec.isExcluded(r)))
				resolutions.add(r);
		}
	}

	@Override
	void collectAll(Set<Resolution> notThese, List<Resolution> all) throws CoreException {
		Resolution r = getResolution();
		if (notThese.add(r)) {
			// It's rather important that we do depth first here and store
			// the child before its parent since they need to be materialized
			// and bound in that order.
			//
			for (BOMNode child : getChildren())
				child.collectAll(notThese, all);
			all.add(getResolution());
		}
	}

	@Override
	BOMNode replaceNode(BOMNode node, Map<BOMNode, BOMNode> visited) throws CoreException {
		BOMNode self = super.replaceNode(node, visited);
		if (self != this)
			return self;

		List<BOMNode> newChildren = null;
		List<BOMNode> oldChildren = getChildren();
		int numChildren = oldChildren.size();
		for (int idx = 0; idx < numChildren; ++idx) {
			BOMNode oldChild = oldChildren.get(idx);
			BOMNode newChild = oldChild.replaceNode(node, visited);
			if (oldChild == newChild)
				continue;

			if (newChildren == null) {
				newChildren = new ArrayList<BOMNode>(numChildren);
				newChildren.addAll(oldChildren);
			}
			newChildren.set(idx, newChild);
		}

		if (newChildren == null)
			return this;

		self = new ResolvedNode(getResolution(), newChildren);
		visited.put(this, self);
		return self;
	}
}
