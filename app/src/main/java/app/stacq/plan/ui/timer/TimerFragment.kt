package app.stacq.plan.ui.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentTimerBinding
import app.stacq.plan.ui.timer.TimerConstants.MINUTE_IN_MILLIS
import java.time.Instant


class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTimerBinding.inflate(inflater, container, false)

        val args = TimerFragmentArgs.fromBundle(requireArguments())
        val finishAt = args.finishAt

        val millisInFuture: Long = (finishAt - Instant.now().epochSecond) * 1000L

        object : CountDownTimer(millisInFuture, MINUTE_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timeText.text = "${millisUntilFinished / MINUTE_IN_MILLIS}"
            }

            override fun onFinish() {
                binding.timeText.text = getString(R.string.completed)
            }
        }.start()

        alarmMgr = this.context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, TimerReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        alarmMgr?.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            finishAt,
            alarmIntent
        )

        binding.timerAlarm.setOnCheckedChangeListener { _, checked ->
            if (checked) alarmMgr?.cancel(alarmIntent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}