package apass.creditcardrecognition.mlkitimpl

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import apass.creditcardcognition.core.contracts.CreditCardScanner
import apass.creditcardrecognition.core.contracts.DI
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import models.CreditCardInfo

class MLKitImpl(private val context: Context) : CreditCardScanner {

    var creditCardImg: Bitmap? = null

    val creditCardInfo = MutableLiveData<CreditCardInfo>()

    override fun scan() {
        val image = FirebaseVisionImage.fromBitmap(this.creditCardImg!!)
        val firebaseVisionTextDetector = FirebaseVision.getInstance().cloudTextRecognizer

        var cardNumber = ""
        var cardExpiry = ""

        firebaseVisionTextDetector.processImage(image)
            .addOnSuccessListener {
                val words = it.text.split("\n")
                for (word in words) {
                    //REGEX for detecting a credit card
                    if (word.replace(" ", "")
                            .matches(Regex("^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})\$"))
                    )
                        cardNumber = word
                    //Find a better way to do this
                    if (word.contains("/")) {
                        for (year in word.split(" ")) {
                            if (year.contains("/"))
                                cardExpiry = year
                        }
                    }
                }
                creditCardInfo.value = CreditCardInfo(cardNumber, cardExpiry)

                Log.i("creditcard-info", "Number: ${cardNumber}, expiry: ${cardExpiry}")
            }
            .addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(context, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }

}