package apass.creditcardrecognition.cardioimpl

import android.R.attr.data
import android.app.Activity
import android.content.Intent
import apass.creditcardcognition.core.contracts.CreditCardScanner
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard


class CardIOImpl(private val activity: Activity) :
    CreditCardScanner {

    companion object {
        const val CARD_IO_SCAN_REQUEST_CODE = 23
    }

    fun handlerResult(data: Intent?):String {
        var resultDisplayStr: String
        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            val scanResult: CreditCard = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)!!

            // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
            resultDisplayStr = """
                Card Number: ${scanResult.redactedCardNumber}
                
                """.trimIndent()

            // Do something with the raw number, e.g.:
            // myService.setCardNumber( scanResult.cardNumber );
            if (scanResult.isExpiryValid) {
                resultDisplayStr += """
            Expiration Date: ${scanResult.expiryMonth}/${scanResult.expiryYear}
            
            """.trimIndent()
            }
            if (scanResult.cvv != null) {
                // Never log or display a CVV
                resultDisplayStr += """CVV has ${scanResult.cvv.length} digits.
"""
            }
            if (scanResult.postalCode != null) {
                resultDisplayStr += """
            Postal Code: ${scanResult.postalCode}
            
            """.trimIndent()
            }
        } else {
            resultDisplayStr = "Scan was canceled."
        }

        return resultDisplayStr
    }

    override fun scan() {
        val scanIntent = Intent(activity, CardIOActivity::class.java)

        // customize these values to suit your needs.
        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true) // default: false

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false) // default: false

        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false) // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        activity.startActivityForResult(scanIntent, CARD_IO_SCAN_REQUEST_CODE)
    }


}