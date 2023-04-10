package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "channel_id_test_01"
    private var notificationId = 102
    private var listOfMemories = "fdsaf"
    private var empty = false

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        listOfMemories = readData()
        empty = false
        Log.d("TAG", listOfMemories.toString())
        if (listOfMemories == "null") { // by default the readData() returns null. this stops it from being in the list for later
            listOfMemories = ""
            empty = true
        }
        if (listOfMemories == "") {
            Log.d("tag", "is nothing")
            empty = true
        }
        if (listOfMemories.length < 2) {
            Log.d("TAG", "less than 2")
            empty = true
        }
        Log.d("TAG", "onCreate:asdfjlndjsanfjldasnjfndskjafnjkdsanfjkndslakjfn ")
        Log.d("TAG", listOfMemories.toString())
        Log.d("TAG", empty.toString())
        if (empty == false) {
            DrawSavedMemories(listOfMemories)
        }
        val notif_button = findViewById<Button>(R.id.button_create_notif)
        notif_button.text = "test"

        notif_button.setOnClickListener{
            val Title = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.text_input)
            val desc = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.Description_text)

            notif_button.text = "clicked!"
            sendNotification()
            createMemory(Title.text.toString(), desc.text.toString())
            Log.d("TAG", Title.text.toString())
            listOfMemories = listOfMemories + Title.text.toString() + "," +  desc.text.toString() + ","
            SaveMemory(listOfMemories)

            // sends the notif after 10 seconds
            delayednotif(desc.text.toString())

        }



    }

    private fun readData(): String {
        val sharedPrefs = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("test7", null).toString()


    }

    private fun DrawSavedMemories(memories: String) {
        var testthung: MutableList<String> = memories.split(",") as MutableList<String>
        testthung.removeLast() // due to comma being at the end, the lists last index is a blank
        for (element in testthung) {
            Log.d("TAG", element.toString())
        }

        for (i in 0..testthung.size-1 step 2) {
            Log.d("TAG", i.toString())
            createMemory(testthung.elementAt(i), testthung.elementAt(i+1))
        }
    }

    private fun SaveMemory(listofMemories: String) {

        val sharedPrefs = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear()
        sharedPrefs.edit().putString("test7", listofMemories).apply()

    }

    private fun removeIndex(word:String) { // as of right now, likely to be errors when there are 2 titles with the same name
        if (empty == false) {
            var memories = listOfMemories
            var testthung: MutableList<String> = memories.split(",") as MutableList<String>
            testthung.removeLast() // due to comma being at the end, the lists last index is a blank
            val index = testthung.indexOf(word)
            testthung.removeAt(index)
            testthung.removeAt(index)
            var newstring = ""
            for (element in testthung) {
                newstring = newstring + element + ","
            }
            Log.d("TAG", newstring)
            SaveMemory(newstring)
        }
    }

    private fun createMemory(Title: String, Desc:String) {

        val vertical_layout = findViewById<LinearLayout>(R.id.vertical_layout)

        // title text
        val dynamicTextview = TextView(this)
        dynamicTextview.text = Title
        dynamicTextview.setTypeface(null, Typeface.BOLD)
        dynamicTextview.setTextSize(24f)

        val descTextView = TextView(this)
        descTextView.text = Desc

        val dynamicLayout = LinearLayout(this)
        dynamicLayout.orientation = LinearLayout.VERTICAL
        dynamicLayout.addView(dynamicTextview)
        dynamicLayout.addView(descTextView)
        dynamicLayout.setPadding(0, 16, 0, 16)
        dynamicLayout.id = View.generateViewId()

        val removeButton = Button(this)
        dynamicLayout.addView(removeButton)

        // the way this is done leaves gaps when new layouts are added
        removeButton.setOnClickListener {
            val parentView = it.parent as? LinearLayout
            //parentView?.removeAllViews()
            removeIndex(dynamicTextview.text.toString())
            vertical_layout.removeView(parentView)


        }

        vertical_layout.addView(dynamicLayout)

    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notfication Title"
            val descriptionText = "notif desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

    }

    private fun sendNotification() {
        val text = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.text_input)
        val desc = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.Description_text)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Remember!")
            .setContentText(text.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(desc.text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            notificationId = notificationId.inc()
            println(notificationId.toString())
            notify(notificationId, builder.build())
        }
    }

    private fun delayednotif(Desc: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(this, MyNotificationReceiver::class.java)
        notificationIntent.putExtra("notification", Desc)

        val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val triggerTime = System.currentTimeMillis() + (10000)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)

    }



}

