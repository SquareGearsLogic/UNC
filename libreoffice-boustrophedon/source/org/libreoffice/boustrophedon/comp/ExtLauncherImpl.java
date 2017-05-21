package org.libreoffice.boustrophedon.comp;

import java.io.File;

import org.libreoffice.boustrophedon.core.VerticalProcessor;
import org.libreoffice.boustrophedon.helper.DialogHelper;
import org.libreoffice.boustrophedon.helper.FileHelper;

import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.registry.XSimpleRegistry;
import com.sun.star.task.XJobExecutor;
import com.sun.star.uno.XComponentContext;

public final class ExtLauncherImpl extends WeakBase
   implements XServiceInfo, XJobExecutor
{
    private final XComponentContext mCtx;
    private static final String mImplementationName = ExtLauncherImpl.class.getName();
    private static final String[] mServiceNames = {"org.libreoffice.boustrophedon.ExtLauncher"};
    private final XSimpleRegistry mRegistry;

    private static final String REGISTRY_PATH = "registry\\org\\openoffice\\Office\\Settings.xcu";
    
    public ExtLauncherImpl( XComponentContext context )
    {
    	mCtx = context;
    	mRegistry = null; //FileHelper.getRegistry(REGISTRY_PATH, mCtx);
    }

    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( mImplementationName ) )
            xFactory = Factory.createComponentFactory(ExtLauncherImpl.class, mServiceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(mImplementationName,
                                                mServiceNames,
                                                xRegistryKey);
    }

    // com.sun.star.lang.XServiceInfo:
    @Override
    public String getImplementationName() {
         return mImplementationName;
    }

    public boolean supportsService( String sService ) {
        int len = mServiceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(mServiceNames[i]))
                return true;
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return mServiceNames;
    }

    // com.sun.star.task.XJobExecutor:
    @Override
    public void trigger(String action)
    {
    	switch (action) {
    	// See line 38 at registry\org\openoffice\Office\Addons.xcu
    	// and line 3 at dialog\ActionOneDialog.xdl
    	case "actionToggle":
    		VerticalProcessor.getInstance(mCtx, mRegistry).toggle();
    		break;
    	case "setup":
    		//actionToggleDialog actionToggleDialog = new actionToggleDialog(m_xContext);
    		//actionToggleDialog.show();
    		break;
    	default:
    		DialogHelper.showErrorMessage(mCtx, null, "Unknown action: " + action);
    	}
        
    }
 	
    private void doStuff(){
    	/*
    	XSimpleRegistry reg = EnvHelper.getRegistry(m_xContext);
    	try {
	    	
	    	boolean bReadOnly = true;
	    	boolean bCreate = false;
			reg.open("org.libreoffice.example.ExtLauncher", bReadOnly, bCreate);
	    	//org\openoffice\Office\TestLocalSettings
	    	//org.libreoffice.example.ExtLauncher.test1
	    	System.out.println("=>>" + reg.isValid() + "; " + reg.getURL());
    	} catch (InvalidRegistryException e) {
			e.printStackTrace();
		}
    	try {
			reg.close();
		} catch (InvalidRegistryException e) {
			e.printStackTrace();
		}
    	
    	//XDesktop desktop = DocumentHelper.getCurrentDesktop(m_xContext);
    	XTextDocument document = DocumentHelper.getCurrentDocument(m_xContext);
    	XText text = document.getText();
    	System.out.println(text.getString());
    	
    	text.getStart().setString("\n\n["); //u"\xa0"
    	text.getEnd().setString("]");
    	System.out.println(text.getString());
    	
    	XTextCursor cur = text.createTextCursor(); // at start
    	cur.goRight((short) 5, false);
    	text.insertString(cur, " hello world", true);
    	System.out.println(text.getString());
    	
    	cur.gotoEnd(false);
    	text.insertControlCharacter(cur, ControlCharacter.PARAGRAPH_BREAK, false);
    	*/
    }


}
