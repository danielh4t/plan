package app.stacq.plan.ui.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentTimerBinding
import java.time.Instant

private const val MINUTE_IN_MILLIS: Long = 60000

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}