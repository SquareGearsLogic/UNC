package org.libreoffice.boustrophedon.core;

import org.libreoffice.boustrophedon.helper.DocumentHelper;

import com.sun.star.document.DocumentEvent;
import com.sun.star.document.XDocumentEventListener;
import com.sun.star.lang.EventObject;
import com.sun.star.registry.XSimpleRegistry;
import com.sun.star.style.LineSpacing;
import com.sun.star.text.ControlCharacter;
import com.sun.star.text.XText;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.uno.XComponentContext;
import com.sun.star.view.XLineCursor;

public class VerticalProcessor 
	implements XDocumentEventListener
{
	private static volatile VerticalProcessor instance;
	private final XComponentContext mCtx;
	private final XTextDocument mDocument;
	
    private static boolean isToggled = false;
	private static LineSpacing mLineSpacing;
	
	private boolean isSkip = false;
	private int mCol = 0;
	private int mRow = 0;
	private int mRowsinCol = 0;
	
	
	private static short BU_LINESPACING_HEIGHT = 50;
	
	private VerticalProcessor(XComponentContext context, XSimpleRegistry registry)
	{
		mCtx = context;
		mDocument = DocumentHelper.getCurrentDocument(mCtx);
	}
	
	public static VerticalProcessor getInstance(XComponentContext context, XSimpleRegistry registry)
	{
		if(instance == null) {
			synchronized(VerticalProcessor.class) {
				if(instance == null) {
					instance = new VerticalProcessor(context, registry);
				}
			}
		}
		return instance;
	}
	
	public void toggle()
	{
		try{
	    	if (isToggled)
	    		toggleOff();
	    	else
	    		toggleOn();
	    	
	    	isToggled = !isToggled;
		}catch(Throwable t){
			System.out.println("Can't toggle:");
			t.printStackTrace();
		}
	}
	
	private void toggleOn()
	{
        if (!DocumentHelper.subscribeEventListener(this, mCtx)){
        	System.out.println("Failed to initialize events.");
        	return;
        }
        XTextViewCursor cur = DocumentHelper.getTextViewCursor(mCtx);
        
        mLineSpacing = DocumentHelper.getLineSpacing(cur);
        DocumentHelper.setLineSpacingHeight(BU_LINESPACING_HEIGHT, cur);
        
    	mCol = 0;
    	mRow = 0;
	}
	
	private void toggleOff()
	{
		if (!DocumentHelper.unsubscribeEventListener(this, mCtx))
        	System.out.println("Failed to initialize events.");
		
		float was = DocumentHelper.getLineSpacingHeight(DocumentHelper.getTextViewCursor(mCtx));
		DocumentHelper.setLineSpacing(mLineSpacing, DocumentHelper.getTextViewCursor(mCtx));
		System.out.println("OFF: was=" + was 
				+ ", now=" + DocumentHelper.getLineSpacingHeight(DocumentHelper.getTextViewCursor(mCtx)) 
				+ ", expected=" + mLineSpacing.Height);
	}
	
	@Override
	public void disposing(EventObject arg0) 
	{
		toggleOff();
	}

	@Override
	public void documentEventOccured(DocumentEvent paramDocumentEvent) 
	{
    	if (isToggled && "OnLayoutFinished".equals(paramDocumentEvent.EventName))
    		update();
	}
	
	public void update()
	{
		if (isSkip){
			isSkip = false;
			return;
		}
		if (process(mDocument.getText(), DocumentHelper.getTextViewCursor(mCtx)))
			isSkip = true;
	}
	
	private boolean process(XText text, XTextViewCursor cursor) 
	{
		XLineCursor linecur = DocumentHelper.getTextViewLineCursor(mCtx);
		if(linecur.isAtStartOfLine())
			return false;
		
		String lastchar = cutCharacter(text, cursor);
		if (lastchar == null || lastchar.isEmpty())
			return false;
		
		moveCursor(text, cursor);
		
    	text.insertString(cursor, lastchar, false);
    	return true;
	}
	
	private String cutCharacter(XText text, XTextCursor cursor)
	{
		cursor.goLeft((short) 1, true);
		String selectedChar = cursor.getString();
		if (selectedChar == null || selectedChar.isEmpty() || selectedChar.equals("\n"))
			return null;

		text.insertString(cursor, "", true);
		System.out.println(selectedChar + " : " + cursor.getText().getString());
		return selectedChar;
	}

	private boolean moveCursor(XText text, XTextCursor cursor) {
		text.insertControlCharacter(cursor, ControlCharacter.LINE_BREAK, false);
		return true;
	}

}
