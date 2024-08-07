package com.example.worker_wearos.presentation

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import com.example.worker_wearos.R

class heart_rate : AppCompatActivity() {

    private lateinit var worker: TextView
    private lateinit var workspace: TextView
    private lateinit var bpm: TextView
    private lateinit var realTime: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var threshold: Long = 150

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate)

        // TextView 초기화
        worker = findViewById(R.id.worker)
        workspace = findViewById(R.id.workspace)
        bpm = findViewById(R.id.bpm)
        realTime = findViewById(R.id.realTime)

        fetchHeartRate()
    }

    private fun fetchHeartRate() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            fetchThreshold { threshold ->
                val documentRef = db.collection("workers").document(userId)

                documentRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val name = document.getString("name") ?: "Unknown Worker"
                            val location = document.getString("location") ?: "Unknown Location"
                            val heartRate = document.getLong("heartRate") ?: 0L

                            updateUI(name, location, heartRate)

                            if (heartRate > threshold) {
                                val intent = Intent(this, heart_rate_max::class.java)
                                startActivity(intent)
                            }
                        } else {
                            Log.d("HeartRateActivity", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("HeartRateActivity", "Error getting documents: ", exception)
                    }
            }
        } else {
            Log.d("HeartRateActivity", "No logged-in user")
        }
    }

    private fun fetchThreshold(callback: (Long) -> Unit) {
        val thresholdRef = db.collection("settings").document("threshold")

        thresholdRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val fetchedThreshold = document.getLong("value") ?: 100
                    threshold = fetchedThreshold
                    callback(threshold)
                } else {
                    Log.d("HeartRateActivity", "No threshold document found")
                    callback(threshold)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("HeartRateActivity", "Error getting threshold: ", exception)
                callback(threshold)
            }
    }

    private fun updateUI(name: String, location: String, heartRate: Long) {
        worker.text = "근무자: $name"
        workspace.text = "위치: $location"
        bpm.text = "현재 심박수는 $heartRate 입니다."
        realTime.text = "현재 시간: ${System.currentTimeMillis()}"
    }
}




