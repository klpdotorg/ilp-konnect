package in.org.klp.ilpkonnect.utils;

import android.content.Context;
import android.util.AttributeSet;


public class NoDefaultSpinner  extends android.support.v7.widget.AppCompatSpinner {

    public NoDefaultSpinner(Context context)
    { super(context); }

    public NoDefaultSpinner(Context context, AttributeSet attrs)
    { super(context, attrs); }

    public NoDefaultSpinner(Context context, AttributeSet attrs, int defStyle)
    { super(context, attrs, defStyle); }

    @Override public void
    setSelection(int position, boolean animate)
    {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override public void
    setSelection(int position)
    {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }
}