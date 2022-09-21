package app.stacq.plan.ui.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.databinding.FragmentTimerBinding


class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TimerViewModelFactory
    private lateinit var viewModel: TimerViewModel

    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTimerBinding.inflate(inflater, container, false)

        val args = TimerFragmentArgs.fromBundle(requireArguments())
        val finishAt: Long = args.finishAt

        viewModelFactory = TimerViewModelFactory(finishAt)
        viewModel = ViewModelProvider(this, viewModelFactory)[TimerViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.millisInFuture.observe(viewLifecycleOwner) {
            if (it > 0) {
                setAlarm(finishAt, it)
            }
        }

        viewModel.timerFinished.observe(viewLifecycleOwner) {
            if (it!!) {
                (binding.timerImage.drawable as Animatable).start()
            }
        }

        binding.timerAlarm.setOnCheckedChangeListener { _, checked ->
            if (checked) alarmManager.cancel(alarmIntent)
        }

        return binding.root
    }


    private fun setAlarm(finishAt: Long, millisInFuture: Long) {
        alarmManager = (this.context?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager)!!

        alarmIntent = Intent(context, TimerReceiver::class.java).let { intent ->
            intent.putExtra("finishAt", finishAt)
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + millisInFuture,
            alarmIntent
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}