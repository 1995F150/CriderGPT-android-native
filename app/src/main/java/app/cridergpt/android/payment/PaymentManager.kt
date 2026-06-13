package app.cridergpt.android.payment

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import com.android.billingclient.api.*

object PaymentManager : PurchasesUpdatedListener {
    private const val TAG = "PaymentManager"
    private lateinit var billingClient: BillingClient
    private var initialized = false
    private var cachedSkuDetails: Map<String, SkuDetails> = emptyMap()

    fun init(context: Context) {
        if (initialized) return
        billingClient = BillingClient.newBuilder(context.applicationContext)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.i(TAG, "Billing setup finished")
                    initialized = true
                    queryAvailableSubscriptions()
                } else {
                    Log.e(TAG, "Billing setup failed: ${result.debugMessage}")
                }
            }
        })
    }

    private fun queryAvailableSubscriptions() {
        val skuList = listOf("cridergpt_plus_monthly", "cridergpt_pro_monthly")
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                cachedSkuDetails = skuDetailsList.associateBy { it.sku }
                Log.i(TAG, "Queried ${skuDetailsList.size} subscription products")
            } else {
                Log.e(TAG, "Failed to query sku details: ${billingResult.debugMessage}")
            }
        }
    }

    @MainThread
    fun purchase(activity: Activity, skuId: String) {
        val details = cachedSkuDetails[skuId]
        if (details == null) {
            Log.w(TAG, "SkuDetails not available for $skuId; refreshing and aborting purchase")
            queryAvailableSubscriptions()
            return
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(details)
            .build()

        billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.i(TAG, "Purchase canceled by user")
        } else {
            Log.e(TAG, "Purchase failed: ${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        // Acknowledge the purchase and persist token
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(params) { ackResult ->
                    if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.i(TAG, "Purchase acknowledged: ${purchase.sku}")
                        persistPurchaseToken(purchase.sku, purchase.purchaseToken)
                    } else {
                        Log.e(TAG, "Acknowledge failed: ${ackResult.debugMessage}")
                    }
                }
            } else {
                persistPurchaseToken(purchase.sku, purchase.purchaseToken)
            }
        }
    }

    private fun persistPurchaseToken(sku: String, token: String) {
        try {
            val ctx = app.cridergpt.android.CriderGPTApplication.instance.applicationContext
            val prefs = ctx.getSharedPreferences("cridergpt_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("purchase_token_$sku", token).apply()
            Log.i(TAG, "Persisted purchase token for $sku")
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to persist purchase token", e)
        }
    }
}
package app.cridergpt.android.payment

object PaymentManager {
    // Placeholder for payments/subscriptions integration (Stripe/Firebase)
}
