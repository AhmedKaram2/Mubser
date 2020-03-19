

package com.samebits.beacon.mubser.viewModel;

import android.support.annotation.NonNull;

import com.samebits.beacon.mubser.model.IManagedBeacon;
import com.samebits.beacon.mubser.model.TrackedBeacon;
import com.samebits.beacon.mubser.ui.fragment.BeaconFragment;

public class DetectedBeaconViewModel extends BeaconViewModel {

    public DetectedBeaconViewModel(@NonNull BeaconFragment fragment, @NonNull IManagedBeacon managedBeacon) {
        super(fragment, managedBeacon);
    }

    protected void clickBeacon() {
        mFragment.selectBeacon(new TrackedBeacon(mManagedBeacon));
    }
}
