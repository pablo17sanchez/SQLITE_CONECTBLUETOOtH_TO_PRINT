package com.example.psanchez.mainproject;

/**
 * Created by psanchez on 3/2/2015.
 */

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import static android.text.TextUtils.isDigitsOnly;
import static android.text.TextUtils.isEmpty;
import static android.widget.TextView.BufferType.EDITABLE;


public final class MaskedWatcher implements TextWatcher  {
    private  EditText mEditText;
    private  String mMask;
    private boolean mIsUpdating, mAcceptOnlyNumbers;
    private char mCharRepresentation;

    public MaskedWatcher(EditText editText, String mask) {
        if (mask == null) throw new RuntimeException("Mask can't be null");
        if (editText == null) throw new RuntimeException("EditText can't be null");

        mEditText = editText;
        mMask = mask;
        mIsUpdating = false;
        editText.addTextChangedListener(this);
        mCharRepresentation = '#';

        if (editText.getEditableText() == null) {
            editText.setTextKeepState("", EDITABLE);
        }
    }
    public boolean acceptOnlyNumbers() {
        return mAcceptOnlyNumbers;
    }


    public char getCharRepresentation() {
        return mCharRepresentation;
    }

    public void setCharRepresentation(char charRepresentation) {
        mCharRepresentation = charRepresentation;
    }
    public void setAcceptOnlyNumbers(boolean acceptOnlyNumbers) {
        mAcceptOnlyNumbers = acceptOnlyNumbers;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {


        if (mIsUpdating) {
            mIsUpdating = false;
            return;
        }

        final CharSequence insertedSequence = charSequence.subSequence(start, count + start);

        if (mAcceptOnlyNumbers && !isNumeric(insertedSequence)) {
            delete(start, start + count);
            return;
        }

        if (charSequence.length() > mMask.length()) {
            delete(start, start + count);
            return;
        }

        final int length = mEditText.length();

        for (int i = 0; i < length; i++) {
            final char m = mMask.charAt(i);
            final char e = mEditText.getText().charAt(i);
            if (m != mCharRepresentation && m != e) {
                mIsUpdating = true;
                mEditText.getEditableText().insert(i, String.valueOf(m));
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void delete(int start, int end) {
        mIsUpdating = true;
        //TODO setText change the keyboard
        mEditText.setTextKeepState(mEditText.getText().delete(start, end), EDITABLE);
    }

    private boolean isNumeric(CharSequence charSequence) {
        return isEmpty(charSequence) || isDigitsOnly(charSequence);
    }


}
