

package com.samebits.beacon.mubser.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.samebits.beacon.mubser.BuildConfig;
import com.samebits.beacon.mubser.R;
import com.samebits.beacon.mubser.model.TrackedBeacon;
import com.samebits.beacon.mubser.ui.activity.MainNavigationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BeaconFragment extends Fragment {

    protected Unbinder unbinder;

    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    private OnTrackedBeaconSelectedListener mBeaconSelectedListener;

    public interface OnTrackedBeaconSelectedListener {
        void onBeaconSelected(TrackedBeacon beacon);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof MainNavigationActivity) {
            ((MainNavigationActivity) getActivity()).swappingFloatingIcon();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mBeaconSelectedListener = (OnTrackedBeaconSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTrackedBeaconSelectedListener");
        }
    }


    public class EmptyView {

        @BindView(R.id.empty_text)
        TextView text;

        public EmptyView(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void selectBeacon(TrackedBeacon trackedBeacon) {
        if (mBeaconSelectedListener != null) {
            mBeaconSelectedListener.onBeaconSelected(trackedBeacon);
        }
    }

}