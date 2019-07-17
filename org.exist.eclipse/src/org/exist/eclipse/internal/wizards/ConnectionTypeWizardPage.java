package org.exist.eclipse.internal.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardNode;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardSelectionPage;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.exist.eclipse.ConnectionType;
import org.exist.eclipse.internal.BasePlugin;

/**
 * Select the type of the connection. This page will start the
 * {@link RemoteConnectionWizard} or the {@link LocalConnectionWizard}.
 * 
 * @author Pascal Schmidiger
 */
public class ConnectionTypeWizardPage extends WorkbenchWizardSelectionPage {
	private ConnectionType _type;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param aWorkbench       the workbench instance
	 * @param currentSelection the current selection
	 */
	public ConnectionTypeWizardPage(IWorkbench aWorkbench, IStructuredSelection currentSelection) {
		super("connectiontypewizardpage", aWorkbench, currentSelection, null, null);
		setTitle(NewConnectionWizard.WIZARD_TITLE);
		setDescription("Select the type of the connection");
		setImageDescriptor(BasePlugin.getImageDescriptor("icons/existdb.png"));
	}

	/**
	 * Get Credentials for the connection to the database.
	 * 
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		Button button = null;
		for (ConnectionType type : ConnectionType.values()) {
			button = new Button(container, SWT.RADIO);
			button.setText(type.description());
			button.setData(ConnectionType.class.getName(), type);
			button.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					_type = (ConnectionType) e.widget.getData(ConnectionType.class.getName());
					setErrorMessage(null);
					setPageComplete(true);
					updateSelectedNode();
				}
			});
		}

		setControl(container);
		setErrorMessage("Select a type");
		setPageComplete(false);
	}

	// //////////////////////////////////////////////////////////////////////////
	// private methods
	// //////////////////////////////////////////////////////////////////////////
	private ConnectionType getType() {
		return _type;
	}

	private void updateSelectedNode() {
		setErrorMessage(null);
		IWizardDescriptor element = null;
		if (ConnectionType.REMOTE.equals(getType())) {
			element = WorkbenchPlugin.getDefault().getNewWizardRegistry()
					.findWizard(RemoteConnectionWizard.class.getCanonicalName());
		} else if (ConnectionType.LOCAL.equals(getType())) {
			element = WorkbenchPlugin.getDefault().getNewWizardRegistry()
					.findWizard(LocalConnectionWizard.class.getCanonicalName());
			setSelectedNode(createWizardNode(element));
		}

		if (element == null) {
			setSelectedNode(null);
			return;
		}
		setSelectedNode(createWizardNode(element));
		setMessage(element.getDescription());
	}

	/*
	 * Create a wizard node given a wizard's descriptor.
	 */
	private IWizardNode createWizardNode(IWizardDescriptor element) {
		return new WorkbenchWizardNode(this, element) {
			@Override
			public IWorkbenchWizard createWizard() throws CoreException {
				return wizardElement.createWizard();
			}
		};
	}
}
