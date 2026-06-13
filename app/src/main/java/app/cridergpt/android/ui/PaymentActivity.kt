package app.cridergpt.android.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.cridergpt.android.payment.PaymentManager

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(app.cridergpt.android.R.layout.activity_payment)

        PaymentManager.init(applicationContext)

        val buyPlus = findViewById<Button>(app.cridergpt.android.R.id.buyPlus)
        val buyPro = findViewById<Button>(app.cridergpt.android.R.id.buyPro)

        buyPlus.setOnClickListener {
            PaymentManager.purchase(this, "cridergpt_plus_monthly")
        }

        buyPro.setOnClickListener {
            PaymentManager.purchase(this, "cridergpt_pro_monthly")
        }
    }
}
