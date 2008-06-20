package org.ncbo.stanford.util.cvs;

import com.ice.cvsc.CVSResponse;
import com.ice.cvsc.CVSUserInterface;

public class CVSTestUI implements CVSUserInterface {

	public void uiDisplayProgressMsg(String message) {
		System.err.println(message);
	}

	public void uiDisplayProgramError(String error) {
		System.err.println(error);
	}

	public void uiDisplayResponse(CVSResponse response) {
		System.err.println(response.getResultStatus());
	}
}
