package com.example.worker_wearos

import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class heart_rate_max : AppCompatActivity() {

    private lateinit var realTime: TextView
    private lateinit var realTimeBpm: TextView
    private lateinit var warningMessage: TextView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate_max)

        realTime = findViewById(R.id.realTime)
        realTimeBpm = findViewById(R.id.realTimeBpm)
        warningMessage = findViewById(R.id.warning)

        fetchHeartRate()
    }

    private fun fetchHeartRate() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val documentRef = db.collection("workers").document(userId)

            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val heartRate = document.getLong("heartRate") ?: 0L

                        updateUI(heartRate)
                    } else {
                        warningMessage.text = "심박수를 가져오는 데 실패했습니다."
                    }
                }
                .addOnFailureListener { exception ->
                    warningMessage.text = "심박수를 가져오는 데 실패했습니다."
                }
        } else {
            warningMessage.text = "로그인된 사용자가 없습니다."
        }
    }

    private fun updateUI(heartRate: Long) {
        val currentTime = Calendar.getInstance().time
        val formattedTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", currentTime).toString()

        realTime.text = "현재 시간: ${formattedTime}"
        realTimeBpm.text = "현재 심박수: ${heartRate} BPM"
        warningMessage.text = "심박수가 임계치를 초과했습니다. 휴식을 취하세요"
    }
}
