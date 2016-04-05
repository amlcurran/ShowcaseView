package com.github.amlcurran.showcaseview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.espian.showcaseview.sample.R;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by Kevin on 5/04/2016.
 */
public class ButtonTwoActivity extends Activity implements OnShowcaseEventListener {


    private ShowcaseView sv;
    private boolean clickedButton1;
    private boolean clickedButton2;
    private TextView mTextView;
    private Button mButtonAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_buttons);
        mTextView = (TextView) findViewById(R.id.Textview_two_buttons);
        mButtonAgain = (Button) findViewById(R.id.Button_two_buttons);
        mButtonAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShowCase();
                mTextView.setText(R.string.two_buttons);
            }
        });
        showShowCase();
    }

    private void showShowCase(){
        clickedButton1 = false;
        clickedButton2 = false;
        ViewTarget target = new ViewTarget(R.id.Textview_two_buttons, this);
        sv = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .hideOnTouchOutside()
                .setTarget(target)
                .setContentTitle(R.string.title_two_buttons)
                .setContentText(R.string.showcase_twobuttons_message)
                .setStyle(R.style.CustomShowcaseTheme2)
                .setShowcaseEventListener(this)
                .addSecondButton()
                .replaceEndButton(R.layout.view_custom_button)
                .replaceButtonTwo(R.layout.view_custom_button)
                .build();
        sv.setButtonsText(getString(R.string.button1),getString(R.string.button2));
        sv.overrideButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedButton1 = true;
                sv.hide();
            }
        });
        sv.overrideButtonTwoClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedButton2 = true;
                sv.hide();
            }
        });


    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {

        if(clickedButton1){
            mTextView.setText(R.string.clicked_button1);
            clickedButton1 = false;
        }
        else if(clickedButton2) {
            mTextView.setText(R.string.clicked_button2);
            clickedButton2 = false;
        }
        else{
            mTextView.setText(R.string.clicked_showcase);
        }
    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }

    @Override
    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

    }
}
