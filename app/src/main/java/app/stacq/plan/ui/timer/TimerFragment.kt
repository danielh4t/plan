package app.stacq.plan.ui.timer

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentTimerBinding

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

        object : CountDownTimer(30000, MINUTE_IN_MILLIS) {

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