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

    init {
        val now: Long = Instant.now().epochSecond
        _timerFinished.value = now > finishAt
    }


}