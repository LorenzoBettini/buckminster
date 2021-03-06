// TODO: Remove license that should not be used
/*******************************************************************
 * Copyright (c) 2006-2008, Cloudsmith Inc.
 * The code, documentation and other materials contained herein
 * are the sole and exclusive property of Cloudsmith Inc. and may
 * not be disclosed, used, modified, copied or distributed without
 * prior written consent or license from Cloudsmith Inc.
 ******************************************************************/
/*******************************************************************************
 * Copyright (c) 2008
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed below, as Initial Contributors under such license.
 * The text of such license is available at 
 * http://www.eclipse.org/legal/epl-v10.html.
 * 
 * Contributors:
 * 		Henrik Lindberg
 *******************************************************************************/

package org.eclipse.equinox.p2.authoring.internal;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;


/**
 * A {@link DelegatingStyledCellLabelProvider} wrapping a {@link P2AuthoringLabelProvider} and
 * with implementation of (plain) {@link ILabelProvier}.
 * 
 * @author Henrik Lindberg
 *
 */
public class P2StyledLabelProvider extends DelegatingStyledCellLabelProvider implements ILabelProvider
{

	public P2StyledLabelProvider()
	{
		super(new P2AuthoringLabelProvider());
	}

	public String getText(Object element)
	{
		return this.getStyledText(element).toString();
	}

}
