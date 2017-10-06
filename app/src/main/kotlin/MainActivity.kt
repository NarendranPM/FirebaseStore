package com.fstore.narendran.firebasestore

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId

import java.util.HashMap

class MainActivity : AppCompatActivity() {

    var data: TextView? = null

    private val docRef = FirebaseFirestore.getInstance().document("sampleData/map")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        data = findViewById<View>(R.id.data) as TextView

        //Geneate FCM token
        triggerFCMToken(this@MainActivity)
    }

    override fun onStart() {
        super.onStart()

        docRef.addSnapshotListener(this) { documentSnapshot, e ->
            if (documentSnapshot.exists()) {
                Log.i(TAG, "onEvent: ")
                val nameStr = documentSnapshot.getString(NAME)
                val valueStr = documentSnapshot.getString(VALUE)
                data?.text = nameStr + " - " + valueStr
            } else if (e != null) {
                Log.w(TAG, "Exception :", e)
            }
        }
    }

    fun saveQuote(view: View) {
        val name = findViewById<EditText>(R.id.name)
        val value = findViewById<EditText>(R.id.value)

        val nameStr = name.text.toString()
        val valueStr = value.text.toString()

        if (nameStr.isEmpty() || valueStr.isEmpty())
            return

        val dataToSave = HashMap<String, Any>()
        dataToSave.put(NAME, nameStr)
        dataToSave.put(VALUE, valueStr)
        docRef.set(dataToSave).addOnSuccessListener { Log.i(TAG, "onSuccess: Document saved!") }.addOnFailureListener { e -> Log.i(TAG, "onFailure: " + e.toString()) }
    }

    fun fetchData(view: View) {
        docRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val nameStr = documentSnapshot.getString(NAME)
                val valueStr = documentSnapshot.getString(VALUE)
                data?.text = nameStr + " - " + valueStr
            }
        }.addOnFailureListener { Log.i(TAG, "onFailure: ") }
    }

    fun triggerFCMToken(context: Context) {
        FCMRegisterTask(context).execute()
    }

    private inner class FCMRegisterTask internal constructor(internal var lContext: Context) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void): String? {

            val lFCMToken: String
            try {
                lFCMToken = FirebaseInstanceId.getInstance().token.toString()
                Log.i(TAG, "FCM Token Response - " + lFCMToken)
            } catch (e: Exception) {
                e.printStackTrace()
                //                updateFCMFailureInPreference("IOERROR",lContext);
                return "Unable to register"
            }

            try {
                if ("TOO_MANY_REGISTRATIONS".equals(lFCMToken, ignoreCase = true)) {
                    //                    updateFCMFailureInPreference(lFCMToken, lContext);
                    return "too many apps registered"
                } else if ("ACCOUNT_MISSING".equals(lFCMToken, ignoreCase = true)) {
                    //                    updateFCMFailureInPreference(lFCMToken, lContext);
                    return "no google account"
                } else {
                    //                    uploadFCMToken(lFCMToken, lContext);
                    return "success"
                }
            } catch (e: Exception) {
                Log.i(TAG, "Exception while registering with FCM Server", e)
                return null
            }

        }

        override fun onPostExecute(pResponse: String) {

        }

    }

    companion object {

        private val TAG = MainActivity::class.java.getName()
        val NAME = "name"
        val VALUE = "value"
    }
}
