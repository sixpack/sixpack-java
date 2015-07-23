package com.seatgeek.sixpack;

@FunctionalInterface
public interface OnConvertSuccess {

    void onConverted(ConvertedExperiment convertedExperiment);
}
