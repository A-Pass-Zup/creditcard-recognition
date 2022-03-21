package apass.creditcardrecognition.scanning

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import apass.creditcardcognition.core.contracts.CreditCardScanner
import apass.creditcardrecognition.App
import apass.creditcardrecognition.R
import apass.creditcardrecognition.mlkitimpl.MLKitImpl

class ScannerActivity : AppCompatActivity() {

   companion object {
       var creditCardScanner: CreditCardScanner? = null
   }

    private val imageVw: ImageView by lazy { findViewById(R.id.image_view) }

    private val takePictureBtn: Button by lazy { findViewById(R.id.takePictureBtn) }

    private val vwCreditCardNumber: TextView by lazy { findViewById(R.id.activity_scanner_creditcard_number) }
    private val vwCreditCardExpiry: TextView by lazy { findViewById(R.id.activity_scanner_creditcard_expiry) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        initViews()
    }

    private fun initViews() {
        this.setupTakePictureButton()
    }

    private fun setupTakePictureButton() {
        takePictureBtn!!.setOnClickListener { view: View? ->
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        258
                    )
                }
            }
            dispatchTakePictureIntent()

        }
    }

    private fun dispatchTakePictureIntent() =
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).run(takePictureResult::launch)

    private val takePictureResult = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val extras = result.data!!.extras
            val imageBitmap = extras!!["data"] as Bitmap
            imageVw!!.setImageBitmap(imageBitmap)

            val mlkitScan = MLKitImpl(this)
            mlkitScan.creditCardInfo.observe(this, {
                vwCreditCardNumber.text = it.number
                vwCreditCardExpiry.text = it.expiry
            })

            mlkitScan.creditCardImg = imageBitmap
            mlkitScan.scan()
        }
    }
}