package org.exist.eclipse.internal.wizards;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.exist.eclipse.IConnection;
import org.exist.eclipse.internal.ConnectionBox;
import org.exist.eclipse.listener.IViewListener;
import org.exist.eclipse.listener.ViewRegistration;

/**
 * Its role is to create a new remote connection.
 * 
 * @author Pascal Schmidiger
 */
public class RemoteConnectionWizard extends Wizard implements IWorkbenchWizard {
	static final String WIZARD_TITLE = "New eXist remote connection";
	static final String WIZARD_DESCRIPTION = "This wizard creates a new eXist remote connection.";
	static final String WIZARD_EDIT_DESCRIPTION = "This wizard edits an eXist remote connection.";
	private IWorkbench _workbench;
	private IConnection _connection;
	private RemoteConnectionWizardPage _page;
	private boolean _copy;

	/**
	 * Constructor for NewConnectionWizard.
	 */
	public RemoteConnectionWizard() {
		super();
		this.setWindowTitle(WIZARD_TITLE);
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		_page = new RemoteConnectionWizardPage();
		_page.setTitle(WIZARD_TITLE);
		if (!_copy && _connection != null) {
			_page.setDescription(WIZARD_EDIT_DESCRIPTION);
		} else {
			_page.setDescription(WIZARD_DESCRIPTION);
		}
		_page.setConnection(_connection, _copy);
		addPage(_page);
	}

	/**
	 * This method figures out whether the 'Finish' button should be enabled. The
	 * button should only be enabled on the NewConnectionWizardPage.
	 */
	@Override
	public boolean canFinish() {
		return getContainer().getCurrentPage().isPageComplete();
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We will
	 * create an operation and run it using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		boolean isFinished = true;
		IConnection connection = _page.getConnection();
		ConnectionBox box = ConnectionBox.getInstance();
		if (_connection != null && !_copy) {
			box.removeConnection(_connection);
		}
		box.addConnection(connection);
		IWorkbenchPage activePage = _workbench.getActiveWorkbenchWindow().getActivePage();
		Iterator<IViewListener> listeners = ViewRegistration.getInstance().getListeners();
		while (listeners.hasNext()) {
			listeners.next().openView(activePage);
		}
		return isFinished;
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		_workbench = workbench;
	}

	public void setConnection(IConnection connection, boolean copy) {
		_connection = connection;
		_copy = copy;
	}
}
