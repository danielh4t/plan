package app.stacq.plan.ui.timer

import android.graphics.drawable.Animatable
import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.stacq.plan.data.source.repository.TasksRepository

class TimerViewModel(private val finishAt: Long): ViewModel() {

    private val _timerTime = MutableLiveData<String?>()
    val timerTime: LiveData<String?> = _timerTime

    private val _timerFinished = MutableLiveData<Boolean?>()
    val timerFinished: LiveData<Boolean?> = _timerFinished

    fun isTimerFinished(now: Long, finishAt: Long): Boolean {
        return now > finishAt
    }

    init {

    }

    fun timer(millisInFuture: Long, millisInterval: Long){
    object : CountDownTimer(millisInFuture, millisInterval) {
        override fun onTick(millisUntilFinished: Long) {
            _timerTime.value = "${millisUntilFinished / millisInterval}"
        }

        override fun onFinish() {
            _timerFinished.value = true
        }
    }.start()

}