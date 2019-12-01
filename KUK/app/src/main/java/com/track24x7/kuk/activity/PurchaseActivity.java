package com.track24x7.kuk.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.util.Log;

import com.track24x7.kuk.R;
import com.track24x7.kuk.util.IabHelper;
import com.track24x7.kuk.util.IabResult;
import com.track24x7.kuk.util.Inventory;
import com.track24x7.kuk.util.Pref;
import com.track24x7.kuk.util.Purchase;
import com.track24x7.kuk.util.StringUtils;

public class PurchaseActivity extends BaseMenuActivity {

    private static final String TAG =
            "com.track24x7.kuk.InAppPayment";
    IabHelper mHelper;
    static final String ITEM_SKU = "pro_version";

    private Button clickButton;
    private Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        buyButton = (Button)findViewById(R.id.buyButton);
        clickButton = (Button)findViewById(R.id.clickButton);
        //clickButton.setEnabled(false);

        String base64EncodedPublicKey =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkknLH2SMRD2v75wGrnM6AyDfSu93xKYcTDSd+LES21cojQjRKXIyvip64l/cQDk1Rk2ATqPTu4v5VDBAvMjBa02O3Q2XIdo5sdktW3L1SfUWQArzkOeD1/vQaAYFMaz8t6uQSCpidJQesn1K/Xw2at0TtSDZSmvZrgsI7p1M+QmKTUwWdqeXmt3HSUyVSHr/Kr6Tt7dJ14pFuMCI76iHJbohR1k4znWQWZapkFFuMWjUjFk/AmI8meJRBJ+76pKaXO//NZ8Sdp/zcFqWn9URmiaQr/OuplGRYT8xgboB2fxiVTfXVU+bdAokGFXBEsJnsHAeat1Az7mwq0nX7n2YUQIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
    }

    public void buttonClicked (View view)
    {
        finish();
    }

    public void buyClick(View view) {
        mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                mPurchaseFinishedListener, "mypurchasetoken");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // 에러 처리 코드
                alert("Purchase failed");
                return;
            }
            else if (purchase.getSku().equals(ITEM_SKU)) {
                Pref.SetBooleanPref(getApplicationContext(), StringUtils.IS_PAID, true);
                //consumeItem();
                alert("Thank you for purchase");
                buyButton.setEnabled(false);
            }
        }
    };

    public void alert(String str){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!").setMessage(str).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }
    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // 에러 처리 코드
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        clickButton.setEnabled(true);
                    } else {
                        // 에러 처리 코드
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}
