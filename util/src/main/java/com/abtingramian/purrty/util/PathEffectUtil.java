package com.abtingramian.purrty.util;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for constructing {@link android.graphics.PathEffect} objects.
 */
public class PathEffectUtil {

    public static class DashPathEffectBuilder {
        private Context context;
        private List<Float> intervalsList = new ArrayList<>();
        private float phase = 0f;

        public DashPathEffectBuilder(@NonNull Context context) {
            this.context = context;
        }

        public DashPathEffectBuilder addInterval(float intervalLength) {
            intervalsList.add(intervalLength);
            return this;
        }

        public DashPathEffectBuilder intervalsFromDimenResIdArray(@ArrayRes int arrayResId) {
            int[] intervals = context.getResources().getIntArray(arrayResId);
            for (int i : intervals) {
                intervalsList.add((float) DimensionUtil.dpToPx(context, i));
            }
            return this;
        }

        public DashPathEffect build() {
            // use default interval lengths if nothing was specified with the builder
            if (intervalsList.isEmpty()) {
                intervalsList.add(context.getResources().getDimension(R.dimen.dash_interval_on));
                intervalsList.add(context.getResources().getDimension(R.dimen.dash_interval_off));
            }
            if (intervalsList.size() < 2 || intervalsList.size() % 2 != 0) {
                throw new IllegalArgumentException("A DashPathEffect requires at least 2 and an even number of interval length entries!");
            }
            float[] intervalsArray = new float[intervalsList.size()];
            for (int i = 0; i < intervalsList.size(); i++) {
                intervalsArray[i] = intervalsList.get(i);
            }
            return new DashPathEffect(intervalsArray, phase);
        }
    }

}
