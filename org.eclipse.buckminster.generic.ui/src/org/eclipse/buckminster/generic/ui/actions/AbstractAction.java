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

package org.eclipse.buckminster.generic.ui.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

/**
 * An Action with useful shortcuts to common operations performed by an action.
 * 
 * @author Henrik Lindberg
 * 
 */
public abstract class AbstractAction extends Action {
	private final Viewer viewer;

	public AbstractAction(Viewer viewer) {
		this.viewer = viewer;
	}

	protected ISelection getSelection() {
		return viewer.getSelection();
	}

	protected Shell getShell() {
		return viewer.getControl().getShell();
	}

	/**
	 * If it is known that the selection is a structured selection it is
	 * conveniently obtained by this method.
	 * 
	 * @return
	 */
	protected IStructuredSelection getStructuredSelection() {
		return (IStructuredSelection) (viewer.getSelection());
	}

	protected Viewer getViewer() {
		return viewer;
	}

	protected boolean showConfirm(String title, String question) {
		return MessageDialog.openConfirm(getShell(), title, question);
	}

	protected void showError(String title, String message) {
		ErrorDialog.openError(getShell(), title, message, new Status(IStatus.ERROR, "org.eclipse.buckminster.generic.ui.actions", //$NON-NLS-1$
				message));
	}

	protected void showError(String title, String message, Throwable e) {
		ErrorDialog.openError(getShell(), title, message, new Status(IStatus.ERROR, "org.eclipse.buckminster.generic.ui.actions", //$NON-NLS-1$
				e.getMessage(), e));
	}

	protected void showMessage(String title, String message) {
		MessageDialog.openInformation(getShell(), title, message);
	}
}
