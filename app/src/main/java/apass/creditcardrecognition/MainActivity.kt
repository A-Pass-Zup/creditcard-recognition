package apass.creditcardrecognition

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import apass.creditcardcognition.core.contracts.CreditCardScanner
import apass.creditcardrecognition.cardioimpl.CardIOImpl
import apass.creditcardrecognition.mlkitimpl.MLKitImpl
import apass.creditcardrecognition.paymentsclientimpl.PaymentsClientImpl
import apass.creditcardrecognition.scanning.ScannerActivity


class MainActivity : AppCompatActivity() {

    private var scannerClient: CreditCardScanner? = null

    private val mlkitBt: Button by lazy { findViewById(R.id.activity_main_bt_mlkit) }
    private val cardIOBt: Button by lazy { findViewById(R.id.activity_main_bt_cardio) }
    private val gPaymentsClientBt: Button by lazy { findViewById(R.id.activity_main_bt_googlepaymentsclient) }

    private val resultTv: TextView by lazy { findViewById(R.id.activity_main_tv_result) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews();
    }

    private fun initViews() {
        setupMLKitBt()
        setupCardIOBt()
        setupGPaymentsClientBt()
    }

    private fun setupCardIOBt() {
        cardIOBt.setOnClickListener {
            scannerClient = CardIOImpl(this)
            scannerClient!!.scan()
        }
    }

    private fun setupMLKitBt() {
        mlkitBt.setOnClickListener {
            Log.i("feature-test", "start MLKit feature test.")
            goToSacanner(MLKitImpl(this))
        }
    }

    private fun setupGPaymentsClientBt() {
        gPaymentsClientBt.setOnClickListener {
            Log.i("feature-test", "start GooglePaymentsClient feature test.")

            scannerClient = PaymentsClientImpl(this)
            scannerClient!!.scan()

        }
    }

    private fun goToSacanner(creditCardScanner: CreditCardScanner) {
        ScannerActivity.creditCardScanner = creditCardScanner
        Intent(this, ScannerActivity::class.java).run(::startActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

       // if (resultCode == RESULT_OK) {
            when (requestCode) {
                PaymentsClientImpl.PAYMENT_CARD_RECOGNITION_REQUEST_CODE -> {
                    resultTv.text =
                        (scannerClient!! as PaymentsClientImpl).handlePaymentCardRecognitionSuccess(
                            data!!
                        )
                }
                CardIOImpl.CARD_IO_SCAN_REQUEST_CODE -> {
                    resultTv.text = (scannerClient!! as CardIOImpl).handlerResult(data)
                }
            }
        //} else {
        //    resultTv.text = "Falha"
        //}

    }
}