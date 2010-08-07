/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.buckminster.model.common.provider;

import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import org.eclipse.buckminster.model.common.CommonPackage;
import org.eclipse.buckminster.model.common.util.CommonAdapterFactory;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ChildCreationExtenderManager;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * @generated
 */
public class CommonItemProviderAdapterFactory extends CommonAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable,
		IChildCreationExtender {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement
	 * {@link org.eclipse.emf.edit.provider.IChangeNotifier}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This helps manage the child creation extenders.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ChildCreationExtenderManager childCreationExtenderManager = new ChildCreationExtenderManager(CommonEditPlugin.INSTANCE, CommonPackage.eNS_URI);

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.Constant} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ConstantItemProvider constantItemProvider;

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.buckminster.model.common.Documentation} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected DocumentationItemProvider documentationItemProvider;

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.Format} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected FormatItemProvider formatItemProvider;

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.Match} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected MatchItemProvider matchItemProvider;

	/**
	 * This keeps track of the one adapter used for all {@link java.util.Map.Entry} instances.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	protected PropertyConstantItemProvider propertyConstantItemProvider;

	/**
	 * This keeps track of the one adapter used for all {@link java.util.Map.Entry} instances.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	protected PropertyElementItemProvider propertyElementItemProvider;

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.PropertyRef} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected PropertyRefItemProvider propertyRefItemProvider;

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.Replace} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ReplaceItemProvider replaceItemProvider;

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.RxGroup} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected RxGroupItemProvider rxGroupItemProvider;

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.RxPattern} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected RxPatternItemProvider rxPatternItemProvider;

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.Split} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected SplitItemProvider splitItemProvider;

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.ToLower} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ToLowerItemProvider toLowerItemProvider;

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.buckminster.model.common.ToUpper} instances. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ToUpperItemProvider toUpperItemProvider;

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.buckminster.model.common.ComponentRequest} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentRequestItemProvider componentRequestItemProvider;

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.buckminster.model.common.ComponentIdentifier} instances.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentIdentifierItemProvider componentIdentifierItemProvider;

	/**
	 * This constructs an instance. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	public CommonItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */

	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This creates an adapter for a
	 * {@link org.eclipse.buckminster.model.common.ComponentIdentifier}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public Adapter createComponentIdentifierAdapter() {
		if (componentIdentifierItemProvider == null) {
			componentIdentifierItemProvider = new ComponentIdentifierItemProvider(this);
		}

		return componentIdentifierItemProvider;
	}

	/**
	 * This creates an adapter for a
	 * {@link org.eclipse.buckminster.model.common.ComponentRequest}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public Adapter createComponentRequestAdapter() {
		if (componentRequestItemProvider == null) {
			componentRequestItemProvider = new ComponentRequestItemProvider(this);
		}

		return componentRequestItemProvider;
	}

	/**
	 * This creates an adapter for a
	 * {@link org.eclipse.buckminster.model.common.Constant}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public Adapter createConstantAdapter() {
		if (constantItemProvider == null) {
			constantItemProvider = new ConstantItemProvider(this);
		}

		return constantItemProvider;
	}

	/**
	 * This creates an adapter for a
	 * {@link org.eclipse.buckminster.model.common.Documentation}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public Adapter createDocumentationAdapter() {
		if (documentationItemProvider == null) {
			documentationItemProvider = new DocumentationItemProvider(this);
		}

		return documentationItemProvider;
	}

	/**
	 * This creates an adapter for a {@link org.eclipse.buckminster.model.common.Format}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public Adapter createFormatAdapter() {
		if (formatItemProvider == null) {
			formatItemProvider = new FormatItemProvider(this);
		}

		return formatItemProvider;
	}

	/**
	 * This creates an adapter for a {@link org.eclipse.buckminster.model.common.Match}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public Adapter createMatchAdapter() {
		if (matchItemProvider == null) {
			matchItemProvider = new MatchItemProvider(this);
		}

		return matchItemProvider;
	}

	/**
	 * This creates an adapter for a
	 * {@link org.eclipse.buckminster.model.common.PropertyConstant}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public Adapter createPropertyConstantAdapter() {
		if (propertyConstantItemProvider == null) {
			propertyConstantItemProvider = new PropertyConstantItemProvider(this);
		}

		return propertyConstantItemProvider;
	}

	/**
	 * This creates an adapter for a
	 * {@link org.eclipse.buckminster.model.common.PropertyElement}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public Adapter createPropertyElementAdapter() {
		if (propertyElementItemProvider == null) {
			propertyElementItemProvider = new PropertyElementItemProvider(this);
		}

		return propertyElementItemProvider;
	}

	/**
	 * This creates an adapter for a
	 * {@link org.eclipse.buckminster.model.common.PropertyRef}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public Adapter createPropertyRefAdapter() {
		if (propertyRefItemProvider == null) {
			propertyRefItemProvider = new PropertyRefItemProvider(this);
		}

		return propertyRefItemProvider;
	}

	/**
	 * This creates an adapter for a {@link org.eclipse.buckminster.model.common.Replace}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public Adapter createReplaceAdapter() {
		if (replaceItemProvider == null) {
			replaceItemProvider = new ReplaceItemProvider(this);
		}

		return replaceItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link org.eclipse.buckminster.model.common.RxAssembly} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RxAssemblyItemProvider rxAssemblyItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.buckminster.model.common.RxAssembly}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createRxAssemblyAdapter() {
		if (rxAssemblyItemProvider == null) {
			rxAssemblyItemProvider = new RxAssemblyItemProvider(this);
		}

		return rxAssemblyItemProvider;
	}

	/**
	 * This creates an adapter for a {@link org.eclipse.buckminster.model.common.RxGroup}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public Adapter createRxGroupAdapter() {
		if (rxGroupItemProvider == null) {
			rxGroupItemProvider = new RxGroupItemProvider(this);
		}

		return rxGroupItemProvider;
	}

	/**
	 * This creates an adapter for a
	 * {@link org.eclipse.buckminster.model.common.RxPattern}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	@Override
	public Adapter createRxPatternAdapter() {
		if (rxPatternItemProvider == null) {
			rxPatternItemProvider = new RxPatternItemProvider(this);
		}

		return rxPatternItemProvider;
	}

	/**
	 * This creates an adapter for a {@link org.eclipse.buckminster.model.common.Split}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public Adapter createSplitAdapter() {
		if (splitItemProvider == null) {
			splitItemProvider = new SplitItemProvider(this);
		}

		return splitItemProvider;
	}

	/**
	 * This creates an adapter for a {@link org.eclipse.buckminster.model.common.ToLower}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public Adapter createToLowerAdapter() {
		if (toLowerItemProvider == null) {
			toLowerItemProvider = new ToLowerItemProvider(this);
		}

		return toLowerItemProvider;
	}

	/**
	 * This creates an adapter for a {@link org.eclipse.buckminster.model.common.ToUpper}.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public Adapter createToUpperAdapter() {
		if (toUpperItemProvider == null) {
			toUpperItemProvider = new ToUpperItemProvider(this);
		}

		return toUpperItemProvider;
	}

	/**
	 * This disposes all of the item providers created by this factory. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public void dispose() {
		if (componentIdentifierItemProvider != null) componentIdentifierItemProvider.dispose();
		if (componentRequestItemProvider != null) componentRequestItemProvider.dispose();
		if (constantItemProvider != null) constantItemProvider.dispose();
		if (documentationItemProvider != null) documentationItemProvider.dispose();
		if (formatItemProvider != null) formatItemProvider.dispose();
		if (matchItemProvider != null) matchItemProvider.dispose();
		if (propertyConstantItemProvider != null) propertyConstantItemProvider.dispose();
		if (propertyElementItemProvider != null) propertyElementItemProvider.dispose();
		if (propertyRefItemProvider != null) propertyRefItemProvider.dispose();
		if (replaceItemProvider != null) replaceItemProvider.dispose();
		if (rxAssemblyItemProvider != null) rxAssemblyItemProvider.dispose();
		if (rxGroupItemProvider != null) rxGroupItemProvider.dispose();
		if (rxPatternItemProvider != null) rxPatternItemProvider.dispose();
		if (splitItemProvider != null) splitItemProvider.dispose();
		if (toLowerItemProvider != null) toLowerItemProvider.dispose();
		if (toUpperItemProvider != null) toUpperItemProvider.dispose();
	}

	/**
	 * This delegates to {@link #changeNotifier} and to
	 * {@link #parentAdapterFactory}. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */

	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public List<IChildCreationExtender> getChildCreationExtenders() {
		return childCreationExtenderManager.getChildCreationExtenders();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */

	public Collection<?> getNewChildDescriptors(Object object, EditingDomain editingDomain) {
		return childCreationExtenderManager.getNewChildDescriptors(object, editingDomain);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */

	public ResourceLocator getResourceLocator() {
		return childCreationExtenderManager;
	}

	/**
	 * This returns the root adapter factory that contains this factory. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */

	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */

	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This sets the composed adapter factory that contains this factory. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */

	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

}
