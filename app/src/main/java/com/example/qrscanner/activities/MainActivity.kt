package com.example.qrscanner.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrscanner.R
import com.example.qrscanner.adapter.PreviousScansAdapter
import com.example.qrscanner.databinding.ActivityMainBinding
import com.example.qrscanner.model.QrCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mauth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var previousScansList: ArrayList<QrCode>
    private lateinit var previousScansAdapter: PreviousScansAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mauth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        checkIsLoggedIn()
        setOnBackPressed()
        retrieveAllPreviousScans()

        val qrScanResultLauncher = registerForActivityResult(StartActivityForResult()){ result ->
            val intentResult = IntentIntegrator.parseActivityResult(result.resultCode,result.data)
            if(intentResult!=null){
                val contents = intentResult.contents
                if(contents!=null){
                    Toast.makeText(this@MainActivity,contents,Toast.LENGTH_LONG).show()
                    addContentToFirestore(contents)
                }
                else{
                    Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()
            }
        }

        binding.apply{

            btnScanQr.setOnClickListener {

                val intentIntegrator = IntentIntegrator(this@MainActivity)
                intentIntegrator.setOrientationLocked(false)
                    .setPrompt("Scan QR in Landscape mode")
                    .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)

                qrScanResultLauncher.launch(intentIntegrator.createScanIntent())

            }

        }

    }

    private fun retrieveAllPreviousScans(){
        checkIsLoggedIn()
        binding.progressBarRecyclverView.visibility = View.VISIBLE
        previousScansList = ArrayList()
        db.collection(mauth.currentUser?.uid!!).get().addOnCompleteListener {getAllScans ->
            if(getAllScans.isSuccessful){
                val result = getAllScans.result
                result.forEach {qrCodeDoc ->
                    val qrCode = QrCode(qrCodeDoc["content"].toString(),qrCodeDoc["time"].toString().toLong())
                    previousScansList.add(qrCode)
                }
                loadScansIntoRecyclerView()
            }
            else{
                Toast.makeText(this@MainActivity,"Error Retrieving Previous Scans",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadScansIntoRecyclerView(){
        previousScansAdapter = PreviousScansAdapter(previousScansList)
        binding.apply{
            previousScansRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            previousScansRecyclerView.adapter = previousScansAdapter
            progressBarRecyclverView.visibility = View.GONE
        }
    }

    private fun addContentToFirestore(contents: String){
        checkIsLoggedIn()
        val data = HashMap<String, String>()
        data["content"]=contents
        data["time"]=System.currentTimeMillis().toString()
        db.collection(mauth.currentUser?.uid!!).add(data).addOnCompleteListener{addData ->
            if(addData.isSuccessful){
                retrieveAllPreviousScans()
            }
            else{
                Toast.makeText(this@MainActivity,"Error Uploading to Server",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIsLoggedIn(){
        if(mauth.currentUser==null){
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun setOnBackPressed(){
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

}