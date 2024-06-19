import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class DigitFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string != null) {
            string = string.replaceAll("[^1-9]", ""); // Only keep digits 1-9
            if (fb.getDocument().getLength() + string.length() <= 1) {
                super.insertString(fb, offset, string, attr);
            }
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text != null) {
            text = text.replaceAll("[^1-9]", ""); // Only keep digits 1-9
            if (fb.getDocument().getLength() - length + text.length() <= 1) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
    }
}
