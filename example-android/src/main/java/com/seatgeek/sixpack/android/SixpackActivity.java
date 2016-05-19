package com.seatgeek.sixpack.android;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seatgeek.sixpack.ConversionError;
import com.seatgeek.sixpack.Experiment;
import com.seatgeek.sixpack.ParticipatingExperiment;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SixpackActivity extends AppCompatActivity {
    @Inject @Named(SixpackModule.BUTTON_COLOR) Experiment buttonColor;

    @InjectView(R.id.colorful_button) Button colorfulButton;

    private ParticipatingExperiment mParticipatingExperiment = null;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SixpackApplication) getApplication()).inject(this);

        setContentView(R.layout.activity_sixpack);
        ButterKnife.inject(this);

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    mParticipatingExperiment = buttonColor.participate();
                    return true;
                } catch (Exception e) {}
                return false;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    if (SixpackModule.BUTTON_COLOR_RED.equals(mParticipatingExperiment.selectedAlternative.name)) {
                        colorfulButton.setBackgroundColor(Color.RED);
                    } else if (SixpackModule.BUTTON_COLOR_BLUE.equals(mParticipatingExperiment.selectedAlternative.name)) {
                        colorfulButton.setBackgroundColor(Color.BLUE);
                    }
                    colorfulButton.setVisibility(View.VISIBLE);

                    colorfulButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setOnClickListener(null);

                            new AsyncTask<Void, Void, Boolean>() {

                                @Override
                                protected Boolean doInBackground(Void... voids) {
                                    try {
                                        mParticipatingExperiment.convert("click");
                                        return true;
                                    } catch (ConversionError error) {}
                                    return false;
                                }

                                @Override
                                protected void onPostExecute(Boolean success) {
                                    if (success) {
                                        Toast.makeText(SixpackActivity.this, "Converted!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SixpackActivity.this, "Nope!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }.execute();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Participation failed!", Toast.LENGTH_SHORT).show();
                }

            }

        }.execute();
    }
}
