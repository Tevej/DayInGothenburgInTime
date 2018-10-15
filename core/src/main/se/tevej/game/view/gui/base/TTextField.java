package main.se.tevej.game.view.gui.base;

public interface TTextField extends TUiElement {

    TTextField set(String text);

    TTextField addListener(OnChangeListener onChangeListener);

    interface OnChangeListener {
        void onChange(String value);
    }

}