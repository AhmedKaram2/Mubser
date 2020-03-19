
package com.samebits.beacon.mubser.ui.adapter;

import android.support.v7.widget.RecyclerView;

import com.samebits.beacon.mubser.model.IManagedBeacon;
import com.samebits.beacon.mubser.ui.fragment.BeaconFragment;
import com.samebits.beacon.mubser.util.BeaconUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class BeaconAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Map<String, IManagedBeacon> mBeacons = new LinkedHashMap();
    protected BeaconFragment mFragment;
    protected OnBeaconLongClickListener onBeaconLongClickListener;


    public void insertBeacon(IManagedBeacon beacon) {
        this.mBeacons.put(beacon.getId(), beacon);
        notifyDataSetChanged();
    }

    public void insertBeacons(List<? extends IManagedBeacon> beacons) {
        for (IManagedBeacon beacon :
                beacons) {
            this.mBeacons.put(beacon.getId(), beacon);
        }
        notifyDataSetChanged();
    }

    public void sort(final int sortMode) {
        this.mBeacons = BeaconUtil.sortBecons(mBeacons, sortMode);
    }

    public void removeBeacon(int position) {
        IManagedBeacon beacon = (IManagedBeacon) getItem(position);
        if (beacon != null) {
            this.mBeacons.remove(beacon.getId());
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mBeacons.size();
    }

    public Object getItem(int idx) {
        int i = 0;
        for (Map.Entry<String, IManagedBeacon> entry : this.mBeacons.entrySet()) {
            if (i == idx) {
                return entry.getValue();
            }
            i += 1;
        }
        return null;
    }

    public void removeAll() {
        this.mBeacons.clear();
        notifyDataSetChanged();
    }

    public boolean isItemExists(String id) {
        return this.mBeacons.containsKey(id);
    }

    public void removeBeaconById(String beaconId) {
        this.mBeacons.remove(beaconId);
        notifyDataSetChanged();
    }

    public void setOnBeaconLongClickListener(OnBeaconLongClickListener onBeaconLongClickListener) {
        this.onBeaconLongClickListener = onBeaconLongClickListener;
    }

    public interface OnBeaconLongClickListener {
        void onBeaconLongClick(int position);
    }
}
