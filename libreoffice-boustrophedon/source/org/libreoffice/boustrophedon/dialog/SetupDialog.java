package org.libreoffice.boustrophedon.dialog;

import org.libreoffice.boustrophedon.helper.DialogHelper;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;


public class SetupDialog implements XDialogEventHandler {
	
	private XDialog dialog;
	private static final String actionOk = "actionOk";
	private String[] supportedActions = new String[] { actionOk };
	
	public SetupDialog(XComponentContext xContext) {
		this.dialog = DialogHelper.createDialog("actionToggleDialog.xdl", xContext, this);
	}

	public void show() {
		dialog.execute();
	}
	
	private void onOkButtonPressed() {
		dialog.endExecute();
	}
	
	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName) throws WrappedTargetException {
		if (methodName.equals(actionOk)) {
			onOkButtonPressed();
			return true; // Event was handled
		}
		return false; // Event was not handled
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}
