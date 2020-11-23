package com.rvtech.turbovpn.vpn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.rvtech.turbovpn.vpn.Config;
import com.rvtech.turbovpn.vpn.MainApplication;
import com.rvtech.turbovpn.vpn.adapter.FBNativeAdapter;
import com.northghost.hydraclient.R;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FreeServersFragment extends Fragment {

    @BindView(R.id.freeServersRecyclerview)
    RecyclerView freeServersRecyclerview;

    private PassServerData mCallback;
    private FreeServersAdapter adapter = new FreeServersAdapter();
    private List<Country> list = new ArrayList<>();

    public static Fragment createInstance()
    {
        FreeServersFragment freeServersFragment = new FreeServersFragment();
        return freeServersFragment;
    }

    public void setFreeServersList(List<Country> servers) {
        list = servers;
        adapter.notifyDataSetChanged();
    }

    public void registerCallback(PassServerData passServerData) {
        mCallback = passServerData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_free_servers, container, false);
        ButterKnife.bind(this, view);
        freeServersRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));


        if (Config.ads_subscription) {
            freeServersRecyclerview.setAdapter(adapter);
        }
        if (!Config.ads_subscription && getResources().getBoolean(R.bool.ads_flag)) {
            FBNativeAdapter nativeAdapter = FBNativeAdapter.Builder.with(getString(R.string.fb_native_banner_id), adapter)
                    .adItemInterval(4)
                    .build();
            freeServersRecyclerview.setAdapter(nativeAdapter);
        }

        return view;
    }

    class FreeServersAdapter extends RecyclerView.Adapter<FreeServersViewHolder> {

        @NonNull
        @Override
        public FreeServersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.region_list_item, parent, false);
            return new FreeServersViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FreeServersViewHolder holder, int position) {
            holder.populateView(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    /**
     * RecyclerView viewHolder
     */
    class FreeServersViewHolder extends RecyclerView.ViewHolder {

        private TextView mRegionTitle;
        private ImageView mRegionImage;
        private RelativeLayout mItemView;
        private CustomCheckBox checkBox_server;

        public FreeServersViewHolder(@NonNull View itemView) {
            super(itemView);

            mRegionTitle = itemView.findViewById(R.id.region_title);
            mRegionImage = itemView.findViewById(R.id.region_image);
            mItemView = itemView.findViewById(R.id.itemView);
            checkBox_server = itemView.findViewById(R.id.server_selection_checkbox);
        }

        public void populateView(Country country) {
            Locale locale = new Locale("", country.getCountry());
            mRegionTitle.setText(locale.getDisplayCountry());
            mRegionImage.setImageResource(MainApplication.getStaticContext().getResources().getIdentifier("drawable/" + country.getCountry(), null, MainApplication.getStaticContext().getPackageName()));

            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.getSelectedServer(country);
                }
            });
            checkBox_server.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.getSelectedServer(country);
                }
            });
        }
    }
}