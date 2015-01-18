package com.seatgeek.sixpack;

@FunctionalInterface
public interface OnConvertSuccess {
    public void onConverted(ConvertedExperiment convertedExperiment);
}
