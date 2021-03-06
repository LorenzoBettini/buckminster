/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.buckminster.pde.internal.imports;

import java.io.File;
import java.util.List;

import org.eclipse.buckminster.core.cspec.model.ComponentRequest;
import org.eclipse.buckminster.core.helpers.FileUtils;
import org.eclipse.buckminster.core.materializer.MaterializationContext;
import org.eclipse.buckminster.core.metadata.WorkspaceInfo;
import org.eclipse.buckminster.core.mspec.ConflictResolution;
import org.eclipse.buckminster.core.resolver.NodeQuery;
import org.eclipse.buckminster.pde.IPDEConstants;
import org.eclipse.buckminster.pde.Messages;
import org.eclipse.buckminster.pde.internal.EclipseImportReaderType;
import org.eclipse.buckminster.pde.internal.datatransfer.IImportStructureProvider;
import org.eclipse.buckminster.pde.internal.datatransfer.ImportOperation;
import org.eclipse.buckminster.pde.internal.dialogs.IOverwriteQuery;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.core.build.IBuildEntry;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.build.WorkspaceBuildModel;
import org.eclipse.pde.internal.core.ifeature.IFeatureInstallHandler;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;

@SuppressWarnings("restriction")
public class FeatureImportOperation implements IWorkspaceRunnable {
	private final IFeatureModel model;

	private final NodeQuery query;

	private final EclipseImportReaderType classpathCollector;

	private final boolean binary;

	private final IWorkspaceRoot root;

	private final IPath destination;

	/**
	 * @param models
	 * @param targetPath
	 *            a parent of external project or null
	 * @param replaceQuery
	 */
	public FeatureImportOperation(EclipseImportReaderType classpathCollector, IFeatureModel model, NodeQuery query, IPath destination, boolean binary) {
		this.classpathCollector = classpathCollector;
		this.model = model;
		this.binary = binary;
		this.query = query;
		this.root = ResourcesPlugin.getWorkspace().getRoot();
		this.destination = destination;
	}

	/*
	 * @see IWorkspaceRunnable#run(IProgressMonitor)
	 */
	@Override
	public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		createProject(monitor);
		MonitorUtils.testCancelStatus(monitor);
	}

	private void createBuildProperties(IProject project) {
		IFile file = project.getFile("build.properties"); //$NON-NLS-1$
		if (file.exists())
			return;

		WorkspaceBuildModel buildModel = new WorkspaceBuildModel(file);
		IBuildEntry ientry = buildModel.getFactory().createEntry("bin.includes"); //$NON-NLS-1$
		try {
			IResource[] res = project.members();
			for (int i = 0; i < res.length; i++) {
				String path = res[i].getProjectRelativePath().toString();
				if (!path.equals(".project")) //$NON-NLS-1$
					ientry.addToken(path);
			}
			buildModel.getBuild().add(ientry);
			buildModel.save();
		} catch (CoreException e) {
		}
	}

	private void createProject(IProgressMonitor monitor) throws CoreException {
		MaterializationContext context = (MaterializationContext) query.getContext();
		ComponentRequest request = query.getComponentRequest();
		String projectName = request.getProjectName();
		monitor.beginTask(NLS.bind(Messages.importing_feature_0, projectName), 100);
		IProject project = root.getProject(projectName);
		try {
			ConflictResolution conflictResolution = context.getMaterializationSpec().getConflictResolution(
					WorkspaceInfo.getResolution(request, false));
			if (project.exists()) {
				switch (conflictResolution) {
					case FAIL:
						throw BuckminsterException.fromMessage(NLS.bind(Messages.project_0_already_exists, projectName));
					case KEEP:
						return;
					default:
				}

				// Overwrite, i.e. remove current contents.
				//
				project.delete(true, true, MonitorUtils.subMonitor(monitor, 10));
				try {
					RepositoryProvider.unmap(project);
				} catch (TeamException e) {
				}
			} else
				MonitorUtils.worked(monitor, 10);

			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProjectDescription description = workspace.newProjectDescription(projectName);
			FileUtils.prepareDestination(destination.toFile(), conflictResolution, MonitorUtils.subMonitor(monitor, 10));
			description.setLocation(destination);
			project.create(description, MonitorUtils.subMonitor(monitor, 5));
			project.open(MonitorUtils.subMonitor(monitor, 5));
			File featureDir = new File(model.getInstallLocation());

			importContent(featureDir, project.getFullPath(), org.eclipse.buckminster.pde.internal.datatransfer.FileSystemStructureProvider.INSTANCE,
					null, MonitorUtils.subMonitor(monitor, 50));

			IFolder folder = project.getFolder("META-INF"); //$NON-NLS-1$
			if (folder.exists())
				folder.delete(true, null);

			if (binary) {
				// Mark this project so that we can show image overlay
				// using the label decorator
				project.setPersistentProperty(PDECore.EXTERNAL_PROJECT_PROPERTY, PDECore.BINARY_PROJECT_VALUE);
			}
			createBuildProperties(project);
			setProjectNatures(project, model, MonitorUtils.subMonitor(monitor, 20));
			if (project.hasNature(JavaCore.NATURE_ID))
				classpathCollector.addProjectClasspath(project, getClasspath(project, model));

			project.delete(false, true, MonitorUtils.subMonitor(monitor, 100));
		} finally {
			monitor.done();
		}
	}

	private IClasspathEntry[] getClasspath(IProject project, IFeatureModel featureModel) throws JavaModelException {
		IClasspathEntry jreCPEntry = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER")); //$NON-NLS-1$

		String libName = featureModel.getFeature().getInstallHandler().getLibrary();
		IClasspathEntry handlerCPEntry = JavaCore.newLibraryEntry(project.getFullPath().append(libName), null, null);

		return new IClasspathEntry[] { jreCPEntry, handlerCPEntry };
	}

	private void importContent(Object source, IPath destPath, IImportStructureProvider provider, List<?> filesToImport, IProgressMonitor monitor)
			throws CoreException {
		IOverwriteQuery overwrite = new IOverwriteQuery() {
			@Override
			public String queryOverwrite(String file) {
				return ALL;
			}
		};
		ImportOperation op = new ImportOperation(destPath, source, provider, overwrite);
		op.setCreateContainerStructure(false);
		if (filesToImport != null)
			op.setFilesToImport(filesToImport);

		IStatus status = op.runInWorkspace(monitor);
		if (status == Status.CANCEL_STATUS || monitor.isCanceled())
			throw new OperationCanceledException(status.getMessage());

		if (status.getSeverity() == IStatus.ERROR)
			throw new CoreException(status);
	}

	private boolean needsJavaNature(IFeatureModel featureModel) {
		IFeatureInstallHandler handler = featureModel.getFeature().getInstallHandler();
		if (handler == null)
			return false;

		String libName = handler.getLibrary();
		if (libName == null || libName.length() == 0)
			return false;

		File lib = new File(featureModel.getInstallLocation(), libName);
		return lib.exists();
	}

	private void setProjectNatures(IProject project, IFeatureModel model, IProgressMonitor monitor) throws CoreException {
		IProjectDescription desc = project.getDescription();
		if (needsJavaNature(model))
			desc.setNatureIds(new String[] { JavaCore.NATURE_ID, IPDEConstants.FEATURE_NATURE });
		else
			desc.setNatureIds(new String[] { IPDEConstants.FEATURE_NATURE });
		project.setDescription(desc, monitor);
	}
}
