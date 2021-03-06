/**
 * 
 */
package org.exist.eclipse.auto.internal.view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.exist.eclipse.auto.internal.mod.AutoModEvent;
import org.exist.eclipse.auto.internal.mod.IAutoModificationNotifier;
import org.exist.eclipse.auto.internal.model.IAutoModel;
import org.exist.eclipse.auto.internal.model.QueryOrderType;

/**
 * This class represents the automation configuration section.
 * 
 * @author Markus Tanner
 */
public class AutomationSection implements ModifyListener, FocusListener {

	private Text _threadCount;
	private FormToolkit _toolkit;
	private IAutoModel _autoModel;
	private Composite _navigation;
	private Combo _queryOrderTypeCombo;
	private Text _autoNote;
	private IAutoModificationNotifier _notifier;

	public AutomationSection(Composite navigation, IAutoModel model,
			FormToolkit toolkit, IAutoModificationNotifier notifier) {
		_navigation = navigation;
		_autoModel = model;
		_toolkit = toolkit;
		_notifier = notifier;
	}

	/**
	 * Initializes the Automation configuration section.
	 */
	public void init() {
		Section autoSection = _toolkit.createSection(_navigation,
				ExpandableComposite.TITLE_BAR);
		autoSection.setText("Automation Details");
		autoSection
				.setDescription("Automation specific values can be edited here.");

		autoSection.marginWidth = 10;
		autoSection.marginHeight = 5;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		autoSection.setLayoutData(gd);

		Composite autoClient = _toolkit.createComposite(autoSection, SWT.WRAP);
		GridLayout autoLayout = new GridLayout();
		autoLayout.numColumns = 2;
		autoLayout.marginWidth = 2;
		autoLayout.marginHeight = 5;
		autoClient.setLayout(autoLayout);

		// thread count
		_toolkit.createLabel(autoClient, "Thread Count:");
		_threadCount = _toolkit.createText(autoClient, "", SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 10;
		_threadCount.addModifyListener(this);
		_threadCount.addFocusListener(this);
		_threadCount.setLayoutData(gd);

		autoSection.setClient(autoClient);
		_threadCount.setText(Integer.toString(_autoModel.getThreadCount()));

		// query order type
		Label queryOrderTypeLbl = _toolkit.createLabel(autoClient,
				"Query Order Type:");
		String queryOrderTypeToolTip = "SEQUENTIAL: Running queries sequentially\n"
				+ "RANDOM: Running queries in a random order\n"
				+ "ITERATING: Looping over queries - a query is run once per iteration";
		queryOrderTypeLbl.setToolTipText(queryOrderTypeToolTip);
		_queryOrderTypeCombo = new Combo(autoClient, SWT.NONE);
		_queryOrderTypeCombo.setToolTipText(queryOrderTypeToolTip);
		_toolkit.paintBordersFor(autoClient);
		gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 10;
		_queryOrderTypeCombo.setLayoutData(gd);
		for (QueryOrderType type : QueryOrderType.values()) {
			_queryOrderTypeCombo.add(type.toString());
		}
		_queryOrderTypeCombo.select(_autoModel.getQueryOrderType().ordinal());
		_queryOrderTypeCombo.addModifyListener(this);

		// notes
		Label noteLabel = _toolkit.createLabel(autoClient, "Note:");
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		noteLabel.setLayoutData(gd);
		_autoNote = _toolkit.createText(autoClient, "", SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 30;
		gd.widthHint = 10;
		_autoNote.setLayoutData(gd);
		_autoNote.addModifyListener(this);
		_autoNote.setText(_autoModel.getAutoNote());

	}

	// --------------------------------------------------------------------------
	// Actions
	// --------------------------------------------------------------------------

	@Override
	public void modifyText(ModifyEvent e) {
		handleThreadCountInput();
		handleQueryOrderTypeInput();
		handleNoteInput();
	}

	@Override
	public void focusGained(FocusEvent e) {
		// This event is not of interest
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (_threadCount.getText().compareTo("") == 0) {
			_threadCount.setText("1");
		}
	}

	// --------------------------------------------------------------------------
	// Private Methods
	// --------------------------------------------------------------------------

	private boolean isValidInt(String input) {
		char[] chars = new char[input.length()];
		input.getChars(0, chars.length, chars, 0);
		for (int i = 0; i < chars.length; i++) {
			if (!('0' <= chars[i] && chars[i] <= '9')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Handles the input concerning a number field. Make sure that only numbers
	 * can be added.
	 */
	private void handleThreadCountInput() {
		boolean sendNotification = false;
		if (_autoModel != null) {
			if (isValidInt(_threadCount.getText())
					&& _threadCount.getText().compareTo("") != 0) {
				// if string represents valid number
				int value = Integer.parseInt(_threadCount.getText());
				if (value >= 1 && value <= 20) {
					_autoModel.setThreadCount(Integer.parseInt(_threadCount
							.getText()));
				} else {
					MessageDialog
							.openInformation(_navigation.getShell(),
									"Invalid thread count",
									"The thread count needs to be a value between 1 and 20.");
					_threadCount.setText("1");
				}
				sendNotification = true;
				// if it's empty
			} else if (_threadCount.getText().compareTo("") == 0) {
				_autoModel.setThreadCount(Integer.parseInt(_threadCount
						.getText()));
				sendNotification = true;
			} else {
				// if the current input contains only one character and is not a
				// number, the field should be cleared
				if (_threadCount.getText().length() != 1) {
					_threadCount.setText(Integer.toString(_autoModel
							.getThreadCount()));
				} else {
					_threadCount.setText("");
				}
			}
		}
		if (sendNotification) {
			_notifier.automationModified(new AutoModEvent(
					"Thread Count modified."));
		}
	}

	private void handleQueryOrderTypeInput() {
		if (_autoModel != null && _queryOrderTypeCombo != null) {
			_autoModel
					.setQueryOrderType(QueryOrderType.values()[_queryOrderTypeCombo
							.getSelectionIndex()]);
		}

	}

	private void handleNoteInput() {
		if (_autoModel != null && _autoNote != null) {
			_autoModel.setAutoNote(_autoNote.getText());
		}
	}

}
