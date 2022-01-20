package james.odike.projects.alarmapp

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import james.odike.projects.alarmapp.TimeConstant.alarmReceiverCode
import james.odike.projects.alarmapp.TimeConstant.alarmTime
import james.odike.projects.alarmapp.databinding.ActivityMainBinding
import james.odike.projects.alarmapp.databinding.DatePickerBinding
import james.odike.projects.alarmapp.databinding.SaveAlarmBinding
import james.odike.projects.alarmapp.databinding.TimePickerBinding
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var view: ActivityMainBinding
    private var selectedDate = ""
    private var selectedTime = ""
    private var selectedDateInMillis = 0L
    private var hourOfDay = 0
    private var minuteOfDay = 0
    private var year = 0
    private var month = 0
    private var day = 0
    private var setDateTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        view.selectDate.setOnClickListener(this)
        view.selectTime.setOnClickListener(this)
        view.setAlarm.setOnClickListener(this)
    }

    override fun onClick(clickedButton: View?) {
        when (clickedButton) {
            view.selectDate -> {
                startDateDialog()
            }
            view.selectTime -> {
                startTimeDialog()
            }
            view.setAlarm -> {
                showSaveDialog()
            }
        }
    }

    private fun startTimeDialog() {
        val dialog = Dialog(this)
        val timeView = TimePickerBinding.inflate(LayoutInflater.from(this))
        timeView.timePicker.setOnTimeChangedListener { _, hour, minute ->
            hourOfDay = hour
            minuteOfDay = minute
        }
        timeView.finish.setOnClickListener {
            selectedTime = "${hourOfDay}:$minuteOfDay"
            setTimeText()
            dialog.dismiss()
        }
        dialog.setContentView(timeView.root)
        dialog.show()
    }

    private fun startDateDialog() {
        val dialog = Dialog(this)
        val dateView = DatePickerBinding.inflate(LayoutInflater.from(this))
        dateView.root.minDate = System.currentTimeMillis()
        dateView.root.setOnDateChangeListener { _, year, month, day ->
            this.year = year
            this.month = month
            this.day = day
            selectedDate = "${year}/${month+1}/$day"
            setTimeText()
            dialog.dismiss()
        }
        dialog.setContentView(dateView.root)
        dialog.show()
    }

    private fun setTimeText() {
        var hourDisplay = ""
        var minuteDisplay = ""
        var amPm = ""

        if (selectedTime != "") {
            if (hourOfDay > 11) {
                amPm = getString(R.string.pm)
                hourDisplay = if (hourOfDay != 12)
                    "${hourOfDay - 12}"
                else
                    "$hourOfDay"
            } else {
                hourDisplay = "0$hourOfDay"
                amPm = getString(R.string.am)
            }
            minuteDisplay = if (minuteOfDay < 10){
                "0$minuteOfDay"
            } else {
                "$minuteOfDay"
            }
        }
        setDateTime = "$selectedDate $hourDisplay:$minuteDisplay $amPm"
        val displayTime = "Alarm time: $setDateTime"
        view.alarmTime.text = displayTime
    }

    private fun showSaveDialog() {
        val dialog = Dialog(this)
        val dialogView = SaveAlarmBinding.inflate(LayoutInflater.from(this))
        dialogView.timeText.text = setDateTime
        dialogView.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialogView.saveBtn.setOnClickListener {
            if (selectedDate != "" && selectedTime != "") {
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day, hourOfDay, minuteOfDay, 0)
                selectedDateInMillis = calendar.timeInMillis
                alarmTime = selectedDateInMillis
                val alarmIntent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this,
                    alarmReceiverCode, alarmIntent, 0)
                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(RTC_WAKEUP, alarmTime, pendingIntent)
                }
            } else {
                val snackView = Snackbar.make(view.root,getString(R.string.alarm_set_error), Snackbar.LENGTH_SHORT)
                snackView.setBackgroundTint(Color.RED)
                snackView.show()
            }
            dialog.dismiss()
        }
        dialog.setContentView(dialogView.root)
        dialog.show()
    }
}