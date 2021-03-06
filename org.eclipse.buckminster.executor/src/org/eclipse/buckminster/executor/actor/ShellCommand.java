package org.eclipse.buckminster.executor.actor;

import java.util.HashMap;

/**
 * Provides shell interpreters for various Operating Systems
 * 
 * @author Guillaume CHATELET
 */
public final class ShellCommand {
	private static final String WINDOWS_CMD = "cmd.exe /C"; //$NON-NLS-1$

	private static final String LINUX_CMD = "sh -c"; //$NON-NLS-1$

	private static final HashMap<String, String> shellCommands;

	static {
		shellCommands = new HashMap<String, String>();
		shellCommands.put("Windows 95", "command.com /C"); //$NON-NLS-1$ //$NON-NLS-2$
		shellCommands.put("Windows 98", "command.com /C"); //$NON-NLS-1$ //$NON-NLS-2$
		shellCommands.put("Windows NT", WINDOWS_CMD); //$NON-NLS-1$
		shellCommands.put("Windows 2000", WINDOWS_CMD); //$NON-NLS-1$
		shellCommands.put("Windows 2003", WINDOWS_CMD); //$NON-NLS-1$
		shellCommands.put("Windows XP", WINDOWS_CMD); //$NON-NLS-1$
		shellCommands.put("Linux", LINUX_CMD); //$NON-NLS-1$
		shellCommands.put("Mac OS X", LINUX_CMD); //$NON-NLS-1$
		shellCommands.put("Linux", LINUX_CMD); //$NON-NLS-1$
	}

	public static String getOsName() {
		return System.getProperty("os.name"); //$NON-NLS-1$
	}

	public static String getShellCommand() {
		return shellCommands.get(getOsName());
	}
}
