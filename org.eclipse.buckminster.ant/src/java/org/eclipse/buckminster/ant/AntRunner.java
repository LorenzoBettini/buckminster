/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Michael Giroux (michael.giroux@objectweb.org) - bug 149739
 *******************************************************************************/
package org.eclipse.buckminster.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.Property;
import org.eclipse.ant.internal.core.IAntCoreConstants;
import org.eclipse.ant.internal.core.InternalCoreAntMessages;
import org.eclipse.buckminster.runtime.BuckminsterPreferences;
import org.eclipse.buckminster.runtime.Logger;
import org.eclipse.buckminster.runtime.Trivial;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * Entry point for running Ant builds inside Eclipse (within the same JRE).
 * Clients may instantiate this class; it is not intended to be subclassed.
 * <p/>
 * <div class="TableSubHeadingColor"> <b>Usage note:</b><br/>
 * As opposed to {@link org.eclipse.ant.core.AntRunner} this class will not set
 * up a new classloader for each call and consequently, it does not support the
 * customClassPath.
 * <p>
 * Refer to the "Platform Ant Support" chapter of the Programmer's Guide section
 * in the Platform Plug-in Developer Guide for complete details.
 * </p>
 * </div>
 */
@SuppressWarnings("restriction")
public class AntRunner {
	/** Message priority of &quot;error&quot;. */
	public static final int MSG_ERR = 0;

	/** Message priority of &quot;warning&quot;. */
	public static final int MSG_WARN = 1;

	/** Message priority of &quot;information&quot;. */
	public static final int MSG_INFO = 2;

	/** Message priority of &quot;verbose&quot;. */
	public static final int MSG_VERBOSE = 3;

	/** Message priority of &quot;debug&quot;. */
	public static final int MSG_DEBUG = 4;

	private static boolean buildRunning = false;

	private static final Class<?> internalAntRunnerClass;

	private static final Method addBuildLogger;

	private static final Method getBuildErrorMessage;

	private static final Method setBuildFileLocation;

	private static final Method setAntHome;

	private static final Method addUserProperties;

	private static final Method addPropertyFiles;

	private static final Method setArguments;

	private static final Method setProgressMonitor;

	private static final Method setMessageOutputLevel;

	private static final Method setExecutionTargets;

	private static final Method run;

	static {
		try {
			Class<?>[] string = new Class<?>[] { String.class };
			Class<?>[] strings = new Class<?>[] { String[].class };
			internalAntRunnerClass = getInternalAntRunnerClass();
			addBuildLogger = internalAntRunnerClass.getMethod("addBuildLogger", string); //$NON-NLS-1$
			getBuildErrorMessage = internalAntRunnerClass.getMethod("getBuildExceptionErrorMessage", //$NON-NLS-1$
					new Class[] { Throwable.class });
			run = internalAntRunnerClass.getMethod("run", Trivial.EMPTY_CLASS_ARRAY); //$NON-NLS-1$
			setBuildFileLocation = internalAntRunnerClass.getMethod("setBuildFileLocation", string); //$NON-NLS-1$
			setAntHome = internalAntRunnerClass.getMethod("setAntHome", string); //$NON-NLS-1$
			addUserProperties = internalAntRunnerClass.getMethod("addUserProperties", new Class[] { Map.class }); //$NON-NLS-1$
			addPropertyFiles = internalAntRunnerClass.getMethod("addPropertyFiles", strings); //$NON-NLS-1$
			setArguments = internalAntRunnerClass.getMethod("setArguments", strings); //$NON-NLS-1$
			setProgressMonitor = internalAntRunnerClass.getMethod("setProgressMonitor", //$NON-NLS-1$
					new Class[] { IProgressMonitor.class });
			setMessageOutputLevel = internalAntRunnerClass.getMethod("setMessageOutputLevel", //$NON-NLS-1$
					new Class[] { int.class });
			setExecutionTargets = internalAntRunnerClass.getMethod("setExecutionTargets", strings); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(problemLoadingClass(e));
		} catch (NoSuchMethodException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/**
	 * Returns whether an Ant build is already in progress
	 * 
	 * Only one Ant build can occur at any given time.
	 * 
	 * @since 2.1
	 * @return boolean
	 */
	public static boolean isBuildRunning() {
		return buildRunning;
	}

	/*
	 * Handles exceptions that are loaded by the Ant Class Loader by asking the
	 * Internal Ant Runner class for the correct error message.
	 * 
	 * Handles OperationCanceledExceptions, nested NoClassDefFoundError and
	 * nested ClassNotFoundException
	 */
	static CoreException handleInvocationTargetException(Object runner, Class<?> classInternalAntRunner, InvocationTargetException e) {
		Throwable realException = e.getTargetException();
		if (realException instanceof OperationCanceledException)
			return new CoreException(Status.CANCEL_STATUS);

		String message = null;
		if (runner != null) {
			try {
				message = (String) getBuildErrorMessage.invoke(runner, new Object[] { realException });
			} catch (Exception ex) {
				// do nothing as already in error state
			}
		}

		// J9 throws NoClassDefFoundError nested in a InvocationTargetException
		//
		if (message == null && ((realException instanceof NoClassDefFoundError) || (realException instanceof ClassNotFoundException)))
			return problemLoadingClass(realException);

		boolean internalError = false;
		if (message == null) {
			// error did not result from a BuildException
			//
			internalError = true;
			message = (realException.getMessage() == null) ? InternalCoreAntMessages.AntRunner_Build_Failed__3 : realException.getMessage();
		}
		IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_RUNNING_BUILD, message, realException);
		if (internalError)
			AntCorePlugin.getPlugin().getLog().log(status);

		return new CoreException(status);
	}

	static CoreException problemLoadingClass(Throwable e) {
		String missingClassName = e.getMessage();
		String message;
		if (missingClassName != null) {
			missingClassName = missingClassName.replace('/', '.');
			message = InternalCoreAntMessages.AntRunner_Could_not_find_one_or_more_classes__Please_check_the_Ant_classpath__2;
			message = NLS.bind(message, new String[] { missingClassName });
		} else {
			message = InternalCoreAntMessages.AntRunner_Could_not_find_one_or_more_classes__Please_check_the_Ant_classpath__1;
		}
		IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_RUNNING_BUILD, message, e);
		AntCorePlugin.getPlugin().getLog().log(status);
		return new CoreException(status);
	}

	private static ClassLoader getClassLoader() {
		return AntCorePlugin.getPlugin().getNewClassLoader();
	}

	@SuppressWarnings("unchecked")
	private static List<Property> getGlobalAntProperties() {
		return AntCorePlugin.getPlugin().getPreferences().getProperties();
	}

	private static Class<?> getInternalAntRunnerClass() throws ClassNotFoundException {
		ClassLoader loader = getClassLoader();
		Thread.currentThread().setContextClassLoader(loader);
		return loader.loadClass("org.eclipse.ant.internal.core.ant.InternalAntRunner"); //$NON-NLS-1$
	}

	private String buildFileLocation = IAntCoreConstants.DEFAULT_BUILD_FILENAME;

	private String[] targets;

	private Map<String, String> userProperties;

	private String[] arguments;

	private String[] propertyFiles;

	private String antHome;

	private String buildLoggerClassName;

	/**
	 * Adds user-defined properties. Keys and values must be String objects.
	 * 
	 * @param properties
	 *            a Map of user-defined properties
	 */
	public void addUserProperties(Map<String, String> properties) {
		if (userProperties == null)
			userProperties = new HashMap<String, String>(properties);
		else
			userProperties.putAll(properties);
	}

	/**
	 * Runs the build file. If a progress monitor is specified it will be
	 * available during the script execution as a reference in the Ant Project (
	 * <code>org.apache.tools.ant.Project.getReferences()</code>). A long-
	 * running task could, for example, get the monitor during its execution and
	 * check for cancellation. The key value to retrieve the progress monitor
	 * instance is <code>AntCorePlugin.ECLIPSE_PROGRESS_MONITOR</code>.
	 * 
	 * Only one build can occur at any given time.
	 * 
	 * Sets the current threads context class loader to the AntClassLoader for
	 * the duration of the build.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @throws CoreException
	 *             Thrown if a build is already occurring or if an exception
	 *             occurs during the build
	 */
	public void run(IProgressMonitor monitor) throws CoreException {
		synchronized (internalAntRunnerClass) {
			Object runner = null;
			ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
			try {
				runner = internalAntRunnerClass.newInstance();
				setBuildFileLocation.invoke(runner, new Object[] { buildFileLocation });

				if (antHome != null)
					setAntHome.invoke(runner, new Object[] { antHome });

				if (buildLoggerClassName == null)
					//
					// indicate that the default logger is not to be used
					//
					buildLoggerClassName = ""; //$NON-NLS-1$

				addBuildLogger.invoke(runner, new Object[] { buildLoggerClassName });

				if (userProperties != null) {
					Map<String, String> allProps = userProperties;

					// The eclipse ant runner will not include the global
					// properties
					// if we add user properties so we need to include them here
					//
					List<Property> properties = getGlobalAntProperties();
					if (properties != null) {
						allProps = new HashMap<String, String>(userProperties);
						for (Property property : properties) {
							// We must do early expansion since the expansion is
							// based
							// on Eclipse variables and not on other properties.
							//
							String value = property.getValue(true);
							if (value != null)
								allProps.put(property.getName(), value);
						}
					}
					addUserProperties.invoke(runner, new Object[] { allProps });
				}

				if (propertyFiles != null && propertyFiles.length > 0)
					addPropertyFiles.invoke(runner, new Object[] { propertyFiles });

				if (arguments != null && arguments.length > 0)
					setArguments.invoke(runner, new Object[] { arguments });

				if (monitor != null)
					setProgressMonitor.invoke(runner, new Object[] { monitor });

				int messageOutputLevel;
				switch (BuckminsterPreferences.getLogLevelAntLogger()) {
					case Logger.DEBUG:
						messageOutputLevel = MSG_DEBUG;
						break;
					case Logger.WARNING:
						messageOutputLevel = MSG_WARN;
						break;
					case Logger.ERROR:
						messageOutputLevel = MSG_ERR;
						break;
					default:
						messageOutputLevel = MSG_INFO;
				}
				if (messageOutputLevel != MSG_INFO)
					setMessageOutputLevel.invoke(runner, new Object[] { new Integer(messageOutputLevel) });

				if (targets != null)
					setExecutionTargets.invoke(runner, new Object[] { targets });

				run.invoke(runner, Trivial.EMPTY_OBJECT_ARRAY);
			} catch (NoClassDefFoundError e) {
				throw problemLoadingClass(e);
			} catch (InvocationTargetException e) {
				throw handleInvocationTargetException(runner, internalAntRunnerClass, e);
			} catch (Exception e) {
				String message = (e.getMessage() == null) ? InternalCoreAntMessages.AntRunner_Build_Failed__3 : e.getMessage();
				IStatus status = new Status(IStatus.ERROR, AntCorePlugin.PI_ANTCORE, AntCorePlugin.ERROR_RUNNING_BUILD, message, e);
				throw new CoreException(status);
			} finally {
				buildRunning = false;
				Thread.currentThread().setContextClassLoader(originalClassLoader);
			}
		}
	}

	/**
	 * Sets the Ant home to use for this build
	 * 
	 * @param antHome
	 *            String specifying the Ant home to use
	 * @since 2.1
	 */
	public void setAntHome(String antHome) {
		this.antHome = antHome;
	}

	/**
	 * Sets the arguments to be passed to the build (e.g. -verbose).
	 * 
	 * @param arguments
	 *            the arguments to be passed to the build
	 */
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

	/**
	 * Sets the build file location on the file system.
	 * 
	 * @param buildFileLocation
	 *            the file system location of the build file
	 */
	public void setBuildFileLocation(IPath buildFileLocation) {
		if (buildFileLocation == null)
			this.buildFileLocation = IAntCoreConstants.DEFAULT_BUILD_FILENAME;
		else
			this.buildFileLocation = buildFileLocation.toOSString();
	}

	/**
	 * Sets the build logger. The parameter <code>className</code> is the class
	 * name of an <code>org.apache.tools.ant.BuildLogger</code> implementation.
	 * The class will be instantiated at runtime and the logger will be called
	 * on build events (<code>org.apache.tools.ant.BuildEvent</code>). Only one
	 * build logger is permitted for any build.
	 * 
	 * <p>
	 * Refer to {@link AntRunner Usage Note} for implementation details.
	 * 
	 * @param className
	 *            a build logger class name
	 */
	public void setBuildLogger(String className) {
		buildLoggerClassName = className;
	}

	/**
	 * Sets the targets and execution order.
	 * 
	 * @param executionTargets
	 *            which targets should be run and in which order
	 */
	public void setExecutionTargets(String[] executionTargets) {
		targets = executionTargets;
	}

	/**
	 * Sets the user specified property files.
	 * 
	 * @param propertyFiles
	 *            array of property file paths
	 * @since 2.1
	 */
	public void setPropertyFiles(String[] propertyFiles) {
		this.propertyFiles = propertyFiles;
	}
}
