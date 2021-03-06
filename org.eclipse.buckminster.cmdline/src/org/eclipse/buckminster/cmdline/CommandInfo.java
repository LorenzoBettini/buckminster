/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/

package org.eclipse.buckminster.cmdline;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.buckminster.runtime.Buckminster;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;

public class CommandInfo {
	public static final int NORMAL = 0;

	public static final int DEPRECATED = 1;

	public static final int HIDDEN = 2;

	public static final int DISABLED = 3;

	static private final String COMMAND_EXTPOINT = "org.eclipse.buckminster.cmdline.commands"; //$NON-NLS-1$

	static private final String ALIAS_ELEMENTS = "alias"; //$NON-NLS-1$

	static private final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	static private final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$

	static private final String DEPRECATED_BY_ATTRIBUTE = "deprecatedBy"; //$NON-NLS-1$

	static private final char PERIOD_CHARACTER = '.';

	static private final String STATUS_ATTRIBUTE = "status"; //$NON-NLS-1$

	static private final String ADD_HELP_FLAGS_ATTRIBUTE = "addhelpflags"; //$NON-NLS-1$

	static private CommandInfo[] commandInfos;

	public static CommandInfo getCommand(String commandName) throws UsageException {
		ArrayList<CommandInfo> matches = new ArrayList<CommandInfo>();
		CommandInfo[] implementors = CommandInfo.getImplementors();
		int top = implementors.length;
		StringBuffer sb = new StringBuffer();
		for (int idx = 0; idx < top; ++idx) {
			CommandInfo ci = implementors[idx];
			if (ci.getStatus() == CommandInfo.DISABLED)
				continue;

			String[] allNames = ci.getAllNames();
			for (int nidx = 0; nidx < allNames.length; ++nidx) {
				String[] parts = allNames[nidx].split("\\."); //$NON-NLS-1$
				int len = parts.length;
				for (int i = 0; i < len; i++) {
					sb.setLength(0);
					for (int j = len - i - 1; j < len; j++) {
						if (sb.length() != 0)
							sb.append('.');
						sb.append(parts[j]);
					}
					if (sb.toString().equals(commandName))
						matches.add(ci);
				}
			}
		}

		int foundMatches = matches.size();
		if (foundMatches == 0)
			throw new UsageException(NLS.bind(Messages.CommandInfo_Command_0_not_found, commandName));

		if (foundMatches > 1) {
			sb.setLength(0);
			sb.append(NLS.bind(Messages.CommandInfo_Multiple_matches_for_0_for, commandName));
			for (int idx = 0; idx < foundMatches; ++idx) {
				CommandInfo ci = matches.get(idx);
				sb.append(ci.getFullName());
				sb.append(NLS.bind(Messages.CommandInfo_implemented_by_class_0, ci.getImplementingClass()));
				if (idx < foundMatches - 1)
					sb.append(", "); //$NON-NLS-1$
			}
			throw new UsageException(sb.toString());
		}

		CommandInfo ci = matches.get(0);
		if (ci.getStatus() == DEPRECATED) {
			sb.setLength(0);
			sb.append(NLS.bind(Messages.CommandInfo_Command_0_is_deprecated, ci.getName()));

			String by = ci.getDeprecatedBy();
			if (by != null) {
				sb.append(NLS.bind(Messages.CommandInfo_Use_command_0_instead, by));
			}
			Buckminster.getLogger().warning(sb.toString());
		}
		return ci;
	}

	static public synchronized CommandInfo[] getImplementors() {
		if (commandInfos == null) {
			IExtensionRegistry er = Platform.getExtensionRegistry();
			IConfigurationElement[] elems = er.getConfigurationElementsFor(COMMAND_EXTPOINT);

			commandInfos = new CommandInfo[elems.length];
			for (int i = 0; i < elems.length; i++)
				commandInfos[i] = new CommandInfo(elems[i]);
		}

		return commandInfos;
	}

	private static int parseCommandStatus(String statusString) {
		int status;
		if ("normal".equalsIgnoreCase(statusString)) //$NON-NLS-1$
			status = NORMAL;
		else if ("deprecated".equalsIgnoreCase(statusString)) //$NON-NLS-1$
			status = DEPRECATED;
		else if ("hidden".equalsIgnoreCase(statusString)) //$NON-NLS-1$
			status = HIDDEN;
		else if ("disabled".equalsIgnoreCase(statusString)) //$NON-NLS-1$
			status = DISABLED;
		else
			throw new IllegalArgumentException(NLS.bind(Messages.CommandInfo__0_is_not_a_valid_command_status, statusString));
		return status;
	}

	private final String[] aliases;

	private final IConfigurationElement cfgElement;

	private final String deprecatedBy;

	private final String name;

	private final String[] allNames;

	private final String namespace;

	private final int status;

	private CommandInfo(IConfigurationElement cfgElement) {
		this.cfgElement = cfgElement;
		status = parseCommandStatus(cfgElement.getAttribute(STATUS_ATTRIBUTE));

		IExtension ext = cfgElement.getDeclaringExtension();
		StringBuffer ns = new StringBuffer(ext.getNamespaceIdentifier());

		String n = cfgElement.getAttribute(NAME_ATTRIBUTE);
		int period = n.lastIndexOf(PERIOD_CHARACTER);
		if (period != -1) {
			ns.append(PERIOD_CHARACTER).append(n.substring(0, period));
			n = n.substring(period + 1);
		}
		namespace = ns.toString();
		name = n;

		IConfigurationElement[] aliasElements = cfgElement.getChildren(ALIAS_ELEMENTS);
		HashSet<String> uniqueNames = new HashSet<String>();
		for (int i = 0; i < aliasElements.length; i++) {
			String alias = aliasElements[i].getAttribute(NAME_ATTRIBUTE);
			if (alias.indexOf(PERIOD_CHARACTER) != -1)
				throw new IllegalCommandAliasException(alias);
			if (!n.equals(alias))
				uniqueNames.add(alias);
		}
		aliases = uniqueNames.toArray(new String[uniqueNames.size()]);
		uniqueNames.clear();
		uniqueNames.add(namespace + PERIOD_CHARACTER + n);
		for (int i = 0; i < aliases.length; i++)
			uniqueNames.add(namespace + PERIOD_CHARACTER + aliases[i]);
		allNames = uniqueNames.toArray(new String[uniqueNames.size()]);
		deprecatedBy = cfgElement.getAttribute(DEPRECATED_BY_ATTRIBUTE);
	}

	public AbstractCommand createInstance() throws CoreException {
		AbstractCommand cmd = (AbstractCommand) cfgElement.createExecutableExtension(CLASS_ATTRIBUTE);
		cmd.init(Boolean.valueOf(cfgElement.getAttribute(ADD_HELP_FLAGS_ATTRIBUTE)).booleanValue());
		return cmd;
	}

	public String[] getAliases() {
		return aliases;
	}

	public String[] getAllNames() {
		return allNames;
	}

	public IConfigurationElement getCfgElement() {
		return cfgElement;
	}

	public String getDeprecatedBy() {
		return deprecatedBy;
	}

	public String getFullName() {
		return allNames[0];
	}

	public String getImplementingClass() {
		return cfgElement.getAttribute(CLASS_ATTRIBUTE);
	}

	public String getName() {
		return name;
	}

	public String getNamespace() {
		return namespace;
	}

	public int getStatus() {
		return status;
	}
}
