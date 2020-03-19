
package com.samebits.beacon.mubser.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samebits.beacon.mubser.R;
import com.samebits.beacon.mubser.databinding.ItemDetectedBeaconBinding;
import com.samebits.beacon.mubser.model.DetectedBeacon;
import com.samebits.beacon.mubser.ui.fragment.BeaconFragment;
import com.samebits.beacon.mubser.viewModel.DetectedBeaconViewModel;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

public class DetectedBeaconAdapter extends BeaconAdapter<DetectedBeaconAdapter.BindingHolder> {

    public DetectedBeaconAdapter(BeaconFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemDetectedBeaconBinding beaconBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_detected_beacon,
                parent,
                false);
        return new BindingHolder(beaconBinding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, final int position) {
        ItemDetectedBeaconBinding beaconBinding = holder.binding;
        holder.setOnLongClickListener(v -> {
            if (onBeaconLongClickListener != null) {
                onBeaconLongClickListener.onBeaconLongClick(position);
            }
            return false;
        });
        beaconBinding.setViewModel(new DetectedBeaconViewModel(mFragment, (DetectedBeacon) getItem(position)));
    }


    public void insertBeacons(Collection<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            DetectedBeacon dBeacon = new DetectedBeacon(beacon);
            dBeacon.setTimeLastSeen(System.currentTimeMillis());
            this.mBeacons.put(dBeacon.getId(), dBeacon);
        }
        notifyDataSetChanged();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ItemDetectedBeaconBinding binding;

        public BindingHolder(ItemDetectedBeaconBinding binding) {
            super(binding.contentView);
            this.binding = binding;
        }

        public void setOnLongClickListener(View.OnLongClickListener listener) {
            binding.contentView.setOnLongClickListener(listener);
        }
    }

}
