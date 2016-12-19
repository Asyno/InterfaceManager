package manage;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MaxLengthDoc extends PlainDocument
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int maxLength;
	
	public MaxLengthDoc(int maxLength)
	{
	    this.maxLength=maxLength+1;
	}
	@Override
	public void insertString(int offs,String str,AttributeSet a) throws BadLocationException
    {
    if(str.length()==0)
        return;
    if(getLength()+str.length()<maxLength)
        {
        super.insertString(offs,str,a);
        }
    }
}