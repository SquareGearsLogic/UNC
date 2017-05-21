package org.libreoffice.boustrophedon.helper;

import java.util.Map;
import java.util.Map.Entry;

import com.sun.star.beans.XPropertySet;
import com.sun.star.document.XDocumentEventListener;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawPageSupplier;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XGlobalEventBroadcaster;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.style.LineSpacing;
import com.sun.star.style.LineSpacingMode;
import com.sun.star.text.XPageCursor;
import com.sun.star.text.XParagraphCursor;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextRange;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.XInterface;
import com.sun.star.util.XSearchDescriptor;
import com.sun.star.util.XSearchable;
import com.sun.star.view.XLineCursor;

public class DocumentHelper {
	
	private static XMultiComponentFactory getMultiFactory(XComponentContext context) 
	{
		return (XMultiComponentFactory)UnoRuntime.queryInterface(XMultiComponentFactory.class,	context.getServiceManager());
	}
	
	public static void printAvailableServices(XComponentContext context){
		try {
			XMultiComponentFactory xMCF = getMultiFactory(context);
			for (String s : xMCF.getAvailableServiceNames())
				System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Returns the current XDesktop */
	public static XDesktop getCurrentDesktop(XComponentContext context) 
	{
		try {
			Object desktop = getMultiFactory(context).createInstanceWithContext("com.sun.star.frame.Desktop", context);
			return (XDesktop) UnoRuntime.queryInterface(com.sun.star.frame.XDesktop.class, desktop);
		} catch (Throwable e) {
			e.printStackTrace();
		}
        return null;
	}
	
	/** Returns the current XComponent */
    private static XComponent getCurrentComponent(XComponentContext context) {
        return (XComponent) getCurrentDesktop(context).getCurrentComponent();
    }
    
    /** Returns the current frame */
    public static XFrame getCurrentFrame(XComponentContext context) {
    	XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class, getCurrentComponent(context));
    	return xModel.getCurrentController().getFrame();
    }

    /** Returns the current text document (if any) */
    public static XTextDocument getCurrentDocument(XComponentContext context) {
        return (XTextDocument) UnoRuntime.queryInterface(XTextDocument.class, getCurrentComponent(context));
    }

	public static boolean subscribeEventListener(XDocumentEventListener listener, XComponentContext context) 
	{
		try {
			Object geb = getMultiFactory(context).createInstanceWithContext("com.sun.star.frame.GlobalEventBroadcaster", context);
			XGlobalEventBroadcaster events = (XGlobalEventBroadcaster) UnoRuntime.queryInterface(XGlobalEventBroadcaster.class, geb);
			events.addDocumentEventListener(listener);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
		}
        return false;
	}
	
	public static boolean unsubscribeEventListener(XDocumentEventListener listener, XComponentContext context) 
	{
		try {
			Object geb = getMultiFactory(context).createInstanceWithContext("com.sun.star.frame.GlobalEventBroadcaster", context);
			XGlobalEventBroadcaster events = (XGlobalEventBroadcaster) UnoRuntime.queryInterface(XGlobalEventBroadcaster.class, geb);
			events.removeDocumentEventListener(listener);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
		}
        return false;
	}
	
	// cur.getPosition()
	public static XTextViewCursor getTextViewCursor(XComponentContext context)
	{
        try{
	        XController xController = getCurrentFrame(context).getController();
	        XTextViewCursorSupplier supTextViewCursor = (XTextViewCursorSupplier) UnoRuntime.queryInterface(XTextViewCursorSupplier.class, xController);
	        return supTextViewCursor.getViewCursor();
        }catch(Throwable t){
        	t.printStackTrace();
        }
        return null;
	}
	
	// The current page number is curPage.getPage()
	public static XPageCursor getTextViewPageCursor(XComponentContext context)
	{
        try{
        	XTextViewCursor viewCursor = getTextViewCursor(context);
            return (XPageCursor) UnoRuntime.queryInterface(XPageCursor.class, viewCursor);
        }catch(Throwable t){
        	t.printStackTrace();
        }
        return null;
	}
	
	public static XParagraphCursor getTextViewParagraphCursor(XComponentContext context)
	{
        try{
        	XTextViewCursor viewCursor = getTextViewCursor(context);
            return (XParagraphCursor) UnoRuntime.queryInterface(XParagraphCursor.class, viewCursor);
        }catch(Throwable t){
        	t.printStackTrace();
        }
        return null;
	}
	
	// l.isAtEndOfLine();
	public static XLineCursor getTextViewLineCursor(XComponentContext context)
	{
        try{
        	XTextViewCursor viewCursor = getTextViewCursor(context);
            return (XLineCursor) UnoRuntime.queryInterface(XLineCursor.class, viewCursor);
        }catch(Throwable t){
        	t.printStackTrace();
        }
        return null;
	}
	
	public static boolean getSearchDescriptor (String what, XTextDocument textDocument, XTextViewCursor textViewCursor)
	{
		try{
			XSearchable xSearchable = (XSearchable) UnoRuntime.queryInterface(XSearchable.class, textDocument); 
			XSearchDescriptor searchDescriptor = (XSearchDescriptor) xSearchable.createSearchDescriptor(); 
			searchDescriptor.setSearchString(what);
	
		    XInterface xfi = (XInterface) xSearchable.findFirst(searchDescriptor); 
		    if (xfi != null) { 
		        XTextRange xStart = (XTextRange) UnoRuntime.queryInterface(XTextRange.class, xfi); 
		        textViewCursor.gotoRange(xStart, false);
		        return true;
		    } 
        }catch(Throwable t){
        	t.printStackTrace();
        }
        return false;
	}
	
	// set the appropriate properties for character and paragraph style
	// CharStyleName:Quotation
    // ParaStyleName:Quotations
	public static boolean setViewCursorProperty(Map<String, String> newProperties, XTextViewCursor cursor){
		try{
			XPropertySet cursorPropertySet = UnoRuntime.queryInterface(XPropertySet.class, cursor);
			for (Entry<String, String> entry : newProperties.entrySet())
				cursorPropertySet.setPropertyValue(entry.getKey(), entry.getValue());
	        return true;
	    }catch(Throwable t){
	    	t.printStackTrace();
	    }
	    return false;
	}
	
	public static XParagraphCursor getModelCursor(XTextViewCursor cursor)
	{
		try{
			XText textDocument = cursor.getText();
			XTextCursor modelCursor = textDocument.createTextCursorByRange(cursor.getStart());
	        return (XParagraphCursor) UnoRuntime.queryInterface(XParagraphCursor.class, modelCursor);
	    }catch(Throwable t){
	    	t.printStackTrace();
	    }
	    return null;
	}
	
	public static XDrawPage getDrawPage(XTextDocument textDocument)
	{
		try{
			XDrawPageSupplier xDrawPageSupplier = UnoRuntime.queryInterface (XDrawPageSupplier.class, textDocument );
			return xDrawPageSupplier.getDrawPage();
	    }catch(Throwable t){
	    	t.printStackTrace();
	    }
	    return null;
	}
	
	public static boolean setLineSpacingHeight(short spacing, XTextViewCursor cursor)
	{
		try{
			LineSpacing lineSpacing = new LineSpacing();
			lineSpacing.Mode = LineSpacingMode.PROP; // Percents, normally it's set to 100.
			lineSpacing.Height = spacing;
			
	        return setLineSpacing(lineSpacing, cursor);
	    }catch(Throwable t){
	    	t.printStackTrace();
	    }
	    return false;
	}
	
	public static boolean setLineSpacing(LineSpacing lineSpacing, XTextViewCursor cursor)
	{
		try{
			XPropertySet xCursorProps = UnoRuntime.queryInterface(XPropertySet.class, cursor );
			xCursorProps.setPropertyValue("ParaLineSpacing", lineSpacing);
	        return true;
	    }catch(Throwable t){
	    	t.printStackTrace();
	    }
	    return false;
	}
	
	public static Short getLineSpacingHeight(XTextViewCursor cursor)
	{
		try{
			return getLineSpacing(cursor).Height;
	    }catch(Throwable t){
	    	t.printStackTrace();
	    }
	    return null;
	}
	
	public static LineSpacing getLineSpacing(XTextViewCursor cursor)
	{
		try{
			XPropertySet xCursorProps = UnoRuntime.queryInterface(XPropertySet.class, cursor );
			return (LineSpacing)xCursorProps.getPropertyValue("ParaLineSpacing");
	    }catch(Throwable t){
	    	t.printStackTrace();
	    }
	    return null;
	}
}
