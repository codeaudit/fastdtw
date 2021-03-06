/*
 * PAA.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package com.fastdtw.timeseries;

import java.util.ArrayList;
import java.util.List;

public class PAA implements TimeSeries {
    private final int[] aggPtSize;
    private final TimeSeries base;

    public PAA(TimeSeries ts, int shrunkSize) {
        validate(ts, shrunkSize);
        List<TimeSeriesItem> items = new ArrayList<TimeSeriesItem>(ts.size());

        // Initialize private data.
        this.aggPtSize = new int[shrunkSize];

        // Determine the size of each sampled point. (may be a fraction)
        final double reducedPtSize = (double) ts.size() / (double) shrunkSize;

        // Variables that keep track of the range of points being averaged into
        // a single point.
        int ptToReadFrom = 0;
        int ptToReadTo;

        // Keep averaging ranges of points into aggregate points until all of
        // the data is averaged.
        while (ptToReadFrom < ts.size()) {
            // determine end of current range
            ptToReadTo = (int) Math.round(reducedPtSize * (items.size() + 1)) - 1;
            final int ptsToRead = ptToReadTo - ptToReadFrom + 1;

            // Keep track of the sum of all the values being averaged to create
            // a single point.
            double timeSum = 0.0;
            final double[] measurementSums = new double[ts.numOfDimensions()];

            // Sum all of the values over the range ptToReadFrom...ptToReadFrom.
            for (int pt = ptToReadFrom; pt <= ptToReadTo; pt++) {
                final double[] currentPoint = ts.getMeasurementVector(pt);

                timeSum += ts.getTimeAtNthPoint(pt);

                for (int dim = 0; dim < ts.numOfDimensions(); dim++)
                    measurementSums[dim] += currentPoint[dim];
            } 

            // Determine the average value over the range
            // ptToReadFrom...ptToReadFrom.
            timeSum = timeSum / ptsToRead;
            for (int dim = 0; dim < ts.numOfDimensions(); dim++)
                // find the average of each measurement
                measurementSums[dim] = measurementSums[dim] / ptsToRead;

            // Add the computed average value to the aggregate approximation.
            this.aggPtSize[items.size()] = ptsToRead;
            items.add(new TimeSeriesItem(timeSum,new TimeSeriesPoint(measurementSums)));
            ptToReadFrom = ptToReadTo + 1; // next window of points to average
                                           // startw where the last window ended
        }
        base = new TimeSeriesBase(items);
    }

    private static int validate(TimeSeries ts, int shrunkSize) {
        if (shrunkSize > ts.size())
            throw new RuntimeException(
                    "ERROR:  The size of an aggregate representation may not be largerr than the \n"
                            + "original time series (shrunkSize=" + shrunkSize + " , origSize="
                            + ts.size() + ").");

        if (shrunkSize <= 0)
            throw new RuntimeException(
                    "ERROR:  The size of an aggregate representation must be greater than zero and \n"
                            + "no larger than the original time series.");
        return shrunkSize;
    }

    public int aggregatePtSize(int ptIndex) {
        return aggPtSize[ptIndex];
    }

    @Override
    public int size() {
        return base.size();
    }

    @Override
    public int numOfDimensions() {
        return base.numOfDimensions();
    }

    @Override
    public double getTimeAtNthPoint(int n) {
        return base.getTimeAtNthPoint(n);
    }

    @Override
    public double getMeasurement(int pointIndex, int valueIndex) {
        return base.getMeasurement(pointIndex, valueIndex);
    }

    @Override
    public double[] getMeasurementVector(int pointIndex) {
        return base.getMeasurementVector(pointIndex);
    }

}
