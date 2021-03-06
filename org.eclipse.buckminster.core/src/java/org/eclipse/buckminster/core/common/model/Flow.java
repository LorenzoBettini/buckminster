/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.common.model;

import org.eclipse.buckminster.core.XMLConstants;
import org.eclipse.buckminster.runtime.Trivial;
import org.eclipse.buckminster.sax.ISaxableElement;
import org.eclipse.buckminster.sax.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author Thomas Hallgren
 * 
 */
public class Flow implements ISaxableElement {
	private final String tag;

	private final ISaxableElement[] children;

	public Flow(String tag, ISaxableElement[] children) {
		this.tag = tag;
		this.children = children;
	}

	public Attributes getAttributes() {
		return EMPTY_ATTRIBUTES;
	}

	public ISaxableElement[] getChildren() {
		return children;
	}

	@Override
	public String getDefaultTag() {
		return tag;
	}

	@Override
	public void toSax(ContentHandler receiver, String namespace, String prefix, String localName) throws SAXException {
		String qName = (prefix == null) ? localName : Utils.makeQualifiedName(prefix, localName);
		receiver.startElement(namespace, localName, qName, getAttributes());
		for (ISaxableElement child : children)
			child.toSax(receiver, XMLConstants.XHTML_NS, null, child.getDefaultTag());
		receiver.endElement(namespace, localName, qName);
	}

	String[] getKeyNamePairs() {
		return Trivial.EMPTY_STRING_ARRAY;
	}
}
