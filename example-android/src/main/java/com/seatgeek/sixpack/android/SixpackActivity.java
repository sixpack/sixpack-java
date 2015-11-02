package com.seatgeek.sixpack.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seatgeek.sixpack.Alternative;
import com.seatgeek.sixpack.ConversionError;
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

        final ParticipatingExperiment participatingExperiment = buttonColor.participate();

        if (SixpackModule.BUTTON_COLOR_RED.equals(participatingExperiment.selectedAlternative.name)) {
            colorfulButton.setBackgroundColor(Color.RED);
        } else if (SixpackModule.BUTTON_COLOR_BLUE.equals(participatingExperiment.selectedAlternative.name)) {
            colorfulButton.setBackgroundColor(Color.BLUE);
        }

        colorfulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setOnClickListener(null);

                try {
                    participatingExperiment.convert();

                    Toast.makeText(SixpackActivity.this, "Converted!", Toast.LENGTH_SHORT).show();
                } catch (ConversionError error) {
                    Toast.makeText(SixpackActivity.this, "Nope!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        colorfulButton.setVisibility(View.VISIBLE);
    }
}
