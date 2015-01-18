package com.seatgeek.sixpack.android;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.seatgeek.sixpack.Alternative;
import com.seatgeek.sixpack.Experiment;
import com.seatgeek.sixpack.OnParticipationFailure;
import com.seatgeek.sixpack.OnParticipationSuccess;
import com.seatgeek.sixpack.ParticipatingExperiment;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SixpackActivity extends ActionBarActivity {
    @Inject @Named(SixpackModule.BUTTON_COLOR) Experiment buttonColor;

    @InjectView(R.id.colorful_button) Button colorfulButton;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SixpackApplication) getApplication()).inject(this);

        setContentView(R.layout.activity_sixpack);
        ButterKnife.inject(this);

        buttonColor.participate(
                new OnParticipationSuccess() {
                    @Override public void onParticipation(ParticipatingExperiment experiment, Alternative selectedAlternative) {
                        if (SixpackModule.BUTTON_COLOR_RED.equals(selectedAlternative.getName())) {
                            colorfulButton.setBackgroundColor(Color.RED);
                        } else if (SixpackModule.BUTTON_COLOR_BLUE.equals(selectedAlternative.getName())) {
                            colorfulButton.setBackgroundColor(Color.BLUE);
                        }
                        colorfulButton.setVisibility(View.VISIBLE);
                    }
                },
                new OnParticipationFailure() {
                    @Override public void onParticipationFailed(Experiment experiment, Throwable error) {
                        colorfulButton.setBackgroundColor(Color.RED);
                        colorfulButton.setVisibility(View.VISIBLE);
                    }
                }
        );
    }
}
