/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.unfoldingword.mobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fechner on 8/9/15.
 */
public class UWTabBar {

    public interface BottomBarListener{

        void buttonPressedAtIndex(int index);
    }

    private Context context;

    private ViewGroup parentLayout;
    private LinearLayout baseLayout;
    private List<ImageButton> buttons;
    private BottomBarListener listener;

    public UWTabBar(Context context, int[] buttonImages, ViewGroup layout, BottomBarListener listener) {
        this.context = context;
        parentLayout = layout;
        this.listener = listener;
        setupViews(buttonImages);
    }

    public Context getContext() {
        return context;
    }

    public ViewGroup getParentLayout() {
        return parentLayout;
    }

    public LinearLayout getBaseLayout() {
        return baseLayout;
    }

    private void setupViews(int[] buttons){

        setupBaseLayout();
        setupButtons(buttons);
    }

    private void setupBaseLayout(){

        baseLayout = new LinearLayout(context);
        baseLayout.setBackgroundResource(R.color.primary);
        baseLayout.setWeightSum((float) 1.0);
        parentLayout.addView(baseLayout);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(baseLayout.getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = getSizeForDp(40);
        baseLayout.setLayoutParams(params);

    }

    private void setupButtons(int[] buttonImageRes){

        buttons = new ArrayList<>(buttonImageRes.length);

        for(int i = 0; i < buttonImageRes.length; i++){

            ImageButton button = createButton(i, buttonImageRes[i], ((float) 1.0 / (float) buttonImageRes.length));
            buttons.add(i, button);
            baseLayout.addView(button);
        }
    }

    private ImageButton createButton(int index, int imageResource, float weight){

        ImageButton button = new ImageButton(context);
        button.setBackgroundResource(R.drawable.basic_button_selector);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, weight));
        int padding = getButtonPadding();
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setPadding(padding, padding, padding, padding);
        button.setOnClickListener(new TabButtonListener(index));
        button.setImageResource(imageResource);
        return button;
    }

    protected int getButtonPadding(){
        return getSizeForDp(6);
    }

//    public void toggleHidden(){
//
//        setHidden(!hidden);
//    }

//    public void setHidden(boolean isHidden){
//
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) parentLayout.getLayoutParams();
//        if(isHidden){
//            params.addRule(RelativeLayout.BELOW, R.id.bottom_marker_layout);
//        }
//        else{
//            params.removeRule(RelativeLayout.BELOW);
//        }
//
//        parentLayout.setLayoutParams(params);
//        hidden = !hidden;
//    }

    private class TabButtonListener implements View.OnClickListener{

        final int index;

        public TabButtonListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            listener.buttonPressedAtIndex(index);
        }
    }

    private int getSizeForDp(int sizeInDP){
        return (int) (sizeInDP * getContext().getResources().getDisplayMetrics().density + 0.5f) ;
    }

    public void setImageAtIndex(int imageResource, int index){

        buttons.get(index).setImageResource(imageResource);
    }

    public ImageButton getButton(int index){
        return buttons.get(index);
    }
}
