/*******************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.jnlp.distroprovider.cloudsmith;

import java.util.Map;

/**
 * @author Karel Brezina
 *
 */
public interface IDistroService
{
	/**
	 * Cancel resolution
	 */
	void cancel();

	/**
	 * Fires distro resolution

	 * @param draft	is draft stack
	 * @param cspecId CSpec ID
	 */
	void fireDistroResolution(boolean draft, Long cspecId);

	/**
	 * Get progress information
	 * 
	 * @return
	 */
	IProgressInfo getProgressInfo();

	/**
	 * Gets resolution results
	 * 
	 * @return P2 properties + OPML
	 */
	Map<String, String> getDistroP2Properties();
}
