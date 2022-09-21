package app.stacq.plan.ui.timer

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.Instant


class TimerViewModel(finishAt: Long) : ViewModel() {

    private val _timerTime = MutableLiveData<String?>()
    val timerTime: LiveData<String?> = _timerTime

    private val _timerFinished = MutableLiveData<Boolean?>()
    val timerFinished: LiveData<Boolean?> = _timerFinished


    private val _millisInFuture = MutableLiveData<Long>()
    val millisInFuture: LiveData<Long> = _millisInFuture


    init {
        val now: Long = Instant.now().epochSecond

        _millisInFuture.value = (finishAt - now) * 1000L
        val millisInterval: Long = TimerConstants.TIMER_TICK_IN_SECONDS * 1000L

        object : CountDownTimer(_millisInFuture.value!!, millisInterval) {
            override fun onTick(millisUntilFinished: Long) {
                _timerTime.value = "${millisUntilFinished / millisInterval}"
            }

            override fun onFinish() {
                _timerFinished.value = true
            }
        }.start()

        val time: Long =  _millisInFuture.value!! / millisInterval
        _timerTime.value = "$time"
        _timerFinished.value = now > finishAt
    }


}