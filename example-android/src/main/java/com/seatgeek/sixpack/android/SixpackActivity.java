package com.seatgeek.sixpack.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seatgeek.sixpack.Alternative;
import com.seatgeek.sixpack.ConvertedExperiment;
import com.seatgeek.sixpack.Experiment;
import com.seatgeek.sixpack.OnConvertFailure;
import com.seatgeek.sixpack.OnConvertSuccess;
import com.seatgeek.sixpack.OnParticipationFailure;
import com.seatgeek.sixpack.OnParticipationSuccess;
import com.seatgeek.sixpack.ParticipatingExperiment;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SixpackActivity extends AppCompatActivity {
    @Inject @Named(SixpackModule.BUTTON_COLOR) Experiment buttonColor;

    @InjectView(R.id.colorful_button) Button colorfulButton;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SixpackApplication) getApplication()).inject(this);

        setContentView(R.layout.activity_sixpack);
        ButterKnife.inject(this);

        buttonColor.participate(
                new OnParticipationSuccess() {
                    @Override public void onParticipation(final ParticipatingExperiment experiment) {
                        if (SixpackModule.BUTTON_COLOR_RED.equals(experiment.selectedAlternative.name)) {
                            colorfulButton.setBackgroundColor(Color.RED);
                        } else if (SixpackModule.BUTTON_COLOR_BLUE.equals(experiment.selectedAlternative.name)) {
                            colorfulButton.setBackgroundColor(Color.BLUE);
                        }

                        colorfulButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.setOnClickListener(null);

                                experiment.convert(
                                        new OnConvertSuccess() {
                                            @Override
                                            public void onConverted(ConvertedExperiment convertedExperiment) {
                                                Toast.makeText(SixpackActivity.this, "Converted!", Toast.LENGTH_SHORT).show();
                                            }
                                        },
                                        new OnConvertFailure() {
                                            @Override
                                            public void onConvertFailure(ParticipatingExperiment experiment, Throwable error) {
                                                // uhhhh, retry?
                                            }
                                        }
                                );
                            }
                        });

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
