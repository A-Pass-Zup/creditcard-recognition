package apass.creditcardrecognition.paymentsclientimpl

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import apass.creditcardcognition.core.contracts.CreditCardScanner
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.PaymentCardRecognitionIntentRequest
import com.google.android.gms.wallet.PaymentCardRecognitionResult
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants.ENVIRONMENT_PRODUCTION
import com.google.android.gms.wallet.WalletConstants.ENVIRONMENT_TEST
import models.CreditCardInfo

class PaymentsClientImpl(private val context: Activity) : CreditCardScanner {

    val creditCardInfo = MutableLiveData<CreditCardInfo>()

    companion object {
        const val PAYMENT_CARD_RECOGNITION_REQUEST_CODE: Int = 22
    }


    private lateinit var cardRecognitionPendingIntent: PendingIntent


    private val paymentsClient: PaymentsClient
    init {
        paymentsClient = this.createPaymentsClient(context)
    }

    override fun scan() {
        possiblyShowPaymentCardOcrButton()
    }

    fun handlePaymentCardRecognitionSuccess(
        intent: Intent
    ): String {
        val cardRecognitionResult = PaymentCardRecognitionResult.getFromIntent(intent)
        val creditCardExpirationDate = cardRecognitionResult?.creditCardExpirationDate
        val expirationDate = creditCardExpirationDate?.let { "%02d/%d".format(it.month, it.year) }
        val cardResultText = "PAN: ${cardRecognitionResult?.pan}\nExpiration date: $expirationDate"
        return cardResultText
    }

    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(ENVIRONMENT_TEST)
            .build()

        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    private fun startPaymentCardOcr() {
        try {
            ActivityCompat.startIntentSenderForResult(
                this.context,
                cardRecognitionPendingIntent.intentSender,
                PAYMENT_CARD_RECOGNITION_REQUEST_CODE,
                null, 0, 0, 0, null
            )
        } catch (e: IntentSender.SendIntentException) {
            throw RuntimeException("Failed to start payment card recognition.", e)
        }
    }

    private fun possiblyShowPaymentCardOcrButton() {
        // The request can be used to configure the type of the payment card recognition. Currently
        // the only supported type is card OCR, so it is sufficient to call the getDefaultInstance()
        // method.
        val request = PaymentCardRecognitionIntentRequest.getDefaultInstance()
        paymentsClient
            .getPaymentCardRecognitionIntent(request)
            .addOnSuccessListener { intentResponse ->
                cardRecognitionPendingIntent = intentResponse.paymentCardRecognitionPendingIntent
                startPaymentCardOcr()
                //paymentCardOcrButton.visibility = View.VISIBLE
            }
            .addOnFailureListener { e ->
                // The API is not available either because the feature is not enabled on the device
                // or because your app is not registered.
                Log.e("orc-fail", "Payment card ocr not available.", e)
            }
    }

}