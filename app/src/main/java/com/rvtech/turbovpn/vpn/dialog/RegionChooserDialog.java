package com.rvtech.turbovpn.vpn.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.rvtech.turbovpn.vpn.adapter.RegionListAdapter;
import com.google.firebase.events.Event;
import com.northghost.hydraclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegionChooserDialog extends DialogFragment implements RegionListAdapter.RegionListAdapterInterface {

    public static final int DIALOG_QUEST_CODE = 300;

    public static final String TAG = RegionChooserDialog.class.getSimpleName();

    @BindView(R.id.regions_recycler_view)
    RecyclerView regionsRecyclerView;

    @BindView(R.id.regions_progress)
    ProgressBar regionsProgressBar;

    private RegionListAdapter regionAdapter;
    private RegionChooserInterface regionChooserInterface;

    public RegionChooserDialog() {
    }

    public static RegionChooserDialog newInstance() {
        RegionChooserDialog frag = new RegionChooserDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        Dialog dialog = super.onCreateDialog(savedInstanceState);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void displayDataResult(Event event) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_region_chooser, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        regionsRecyclerView.setHasFixedSize(true);
        regionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        regionAdapter = new RegionListAdapter(getContext(), this);
        regionsRecyclerView.setAdapter(regionAdapter);

        loadServers();
    }

    private void loadServers() {
        showProgress();

        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull final AvailableCountries countries) {
                hideProress();
                regionAdapter.setRegions(countries.getCountries());
            }

            @Override
            public void failure(VpnException e) {
                hideProress();
                dismiss();
            }
        });
    }

    private void showProgress() {
        regionsProgressBar.setVisibility(View.VISIBLE);
        regionsRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideProress() {
        regionsProgressBar.setVisibility(View.GONE);
        regionsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCountrySelected(Country item) {
        regionChooserInterface.onRegionSelected(item);
        dismiss();
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof RegionChooserInterface) {
            regionChooserInterface = (RegionChooserInterface) ctx;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        regionChooserInterface = null;
    }

    public interface RegionChooserInterface {
        void onRegionSelected(Country item);
    }
}
