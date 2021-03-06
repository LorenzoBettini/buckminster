/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/

package org.eclipse.buckminster.core.commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.buckminster.cmdline.SimpleErrorExitException;
import org.eclipse.buckminster.core.Messages;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.util.NLS;

/**
 * @author Thomas Hallgren
 */
public class ImportPreferences extends AbstractPreferencesCommand {
	@Override
	protected int internalRun(IProgressMonitor monitor) throws Exception {
		InputStream input = null;
		File prefsFile = this.getFile();
		try {
			if (prefsFile == null)
				input = System.in;
			else
				input = new BufferedInputStream(new FileInputStream(prefsFile));

			IPreferencesService prefService = Platform.getPreferencesService();
			IExportedPreferences prefs = prefService.readPreferences(input);
			prefService.applyPreferences(prefs, this.getFilter());
			return 0;
		} catch (IOException e) {
			throw new SimpleErrorExitException(NLS.bind(Messages.ImportPreferences_Unable_to_open_file_0, prefsFile));
		} finally {
			if (prefsFile != null)
				IOUtils.close(input);
		}
	}
}
