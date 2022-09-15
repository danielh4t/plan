package app.stacq.plan.ui.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.stacq.plan.databinding.FragmentTimerBinding
import app.stacq.plan.ui.timer.TimerConstants.TIMER_TICK_IN_SECONDS
import java.time.Instant


class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private lateinit var alarmIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTimerBinding.inflate(inflater, container, false)

        val args = TimerFragmentArgs.fromBundle(requireArguments())
        val finishAt: Long = args.finishAt

        val now: Long = Instant.now().epochSecond
        val millisInFuture: Long = (finishAt - now) * 1000L
        val millisInterval: Long = TIMER_TICK_IN_SECONDS * 1000L

        object : CountDownTimer(millisInFuture, millisInterval) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timeText.text = "${millisUntilFinished / millisInterval}"
            }

            override fun onFinish() {
                binding.timeText.text = ":)"
            }
        }.start()

        alarmIntent = Intent(context, TimerReceiver::class.java).let { intent ->
            intent.putExtra("finishAt", finishAt)
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        val alarmManager = this.context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + millisInFuture,
            alarmIntent
        )

        binding.timerAlarm.setOnCheckedChangeListener { _, checked ->
            if (checked && alarmManager != null) alarmManager.cancel(alarmIntent)
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}