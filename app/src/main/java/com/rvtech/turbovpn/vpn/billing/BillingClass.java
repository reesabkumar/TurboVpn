package com.rvtech.turbovpn.vpn.billing;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.rvtech.turbovpn.vpn.Config;

import java.util.ArrayList;
import java.util.List;

public class BillingClass implements PurchasesUpdatedListener, BillingClientStateListener {

    private BillingClient billingClient;
    private List<String> skuList;
    private List<SkuDetails> skuListFromStore;

    //others
    private boolean isAvailable = false;
    private boolean isListGot = false;
    private Activity refActivity;

    private BillingErrorHandler mCallback;

    //step-1 init
    public BillingClass(Activity activity) {

        refActivity = activity;

        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        skuList = new ArrayList<>();

        //add all products here
        skuList.add(Config.remove_ads_one_month);
        skuList.add(Config.remove_ads_three_month);
        skuList.add(Config.remove_ads_six_month);
        skuList.add(Config.remove_ads_one_year);
        skuList.add(Config.unlock_servers_one_month);
        skuList.add(Config.unlock_servers_three_month);
        skuList.add(Config.unlock_servers_six_month);
        skuList.add(Config.unlock_servers_one_year);
    }

    //step-2 make connection to store
    public void startConnection() {
        billingClient.startConnection(this);
    }

    public void setmCallback(BillingErrorHandler mCallback) {
        if (this.mCallback == null) {
            this.mCallback = mCallback;
        }
    }

    //purchase update listener
    //step-5
    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && list != null) {
            for (Purchase purchase : list) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.

        } else {
            // Handle any other error codes.
        }

    }

    //step-3
    //state listener
    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

            mCallback.displayErrorMessage("done");

            //proceed, Client is ready
            isAvailable = true;

            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
            billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {

                    skuListFromStore = list;
                    isListGot = true;
                }
            });
        }
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE || billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR)
        {
            mCallback.displayErrorMessage("error");
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        //restart, No connection to billing service
        isAvailable = false;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isListGot() {
        return isListGot;
    }

    //step-4
    public String purchaseItemByPos(int itemIndex) {
//
//        BillingResult result = billingClient.isFeatureSupported(skuList.get(itemIndex));
//
//        if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {

        if (billingClient.isReady()) {

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuListFromStore.get(itemIndex))
                    .build();

            BillingResult responseCode = billingClient.launchBillingFlow(refActivity, billingFlowParams);

            switch (responseCode.getResponseCode()) {

                case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                    mCallback.displayErrorMessage("Billing not supported for type of request");
                    return "Billing not supported for type of request";


                case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                    return "";

                case BillingClient.BillingResponseCode.ERROR:
                    mCallback.displayErrorMessage("Error completing request");
                    return "Error completing request";

                case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                    return "Error processing request.";

                case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                    return "Selected item is already owned";

                case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                    return "Item not available";

                case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                    return "Play Store service is not connected now";

                case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                    return "Timeout";

                case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                    mCallback.displayErrorMessage("Network error.");
                    return "Network Connection down";

                case BillingClient.BillingResponseCode.USER_CANCELED:
                    mCallback.displayErrorMessage("Request Canceled");
                    return "Request Canceled";

                case BillingClient.BillingResponseCode.OK:
                    return "Subscribed Successfully";

                //}

            }
//        else {
//            mCallback.displayErrorMessage("Item type not supported");
//
        }

        return "";
    }

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            AcknowledgePurchaseParams acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                @Override
                public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {

                }
            });
        }
    }


    public boolean isSubscribed(String sku) {
        if (skuList != null) {
            Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.SUBS);

            if (result.getResponseCode() == BillingClient.BillingResponseCode.OK && result.getPurchasesList() != null) {
                for (Purchase purchase :
                        result.getPurchasesList()) {

                    if (purchase.getSku().equals(sku)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        return false;
    }

    //message interface
    public interface BillingErrorHandler {
        void displayErrorMessage(String message);
    }
}

