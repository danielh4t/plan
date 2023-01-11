package app.stacq.plan.ui.timer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentTimerBinding
import app.stacq.plan.util.TimeUtil
import app.stacq.plan.util.createTimerChannel


class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TimerViewModelFactory
    private lateinit var viewModel: TimerViewModel

    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = TimerFragmentArgs.fromBundle(requireArguments())
        val taskId: String = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val localDataSource = TaskLocalDataSource(database.taskDao())
        val remoteDataSource = TaskRemoteDataSource()
        val taskRepository = TaskRepository(localDataSource, remoteDataSource)

        if (canPostNotifications(requireActivity())) {
            binding.timerAlarm.visibility = View.VISIBLE
        } else {
            binding.timerAlarm.visibility = View.GONE
        }

        viewModelFactory = TimerViewModelFactory(taskRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TimerViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.task.observe(viewLifecycleOwner) {
            it?.let {
                if (it.timerFinishAt == 0L) {
                    // timer not started
                    viewModel.updateTaskTimerFinish()
                } else {
                    // timer started
                    if (TimeUtil().millisInFuture(it.timerFinishAt) > 0L) {
                        startTimer(it.timerFinishAt)
                        val requestCode: Int = it.timerFinishAt.toInt()
                        val triggerTime = TimeUtil().alarmTriggerTimer(it.timerFinishAt)
                        // set alarm
                        if (canPostNotifications(requireActivity()) && it.timerAlarm) {
                            setAlarm(requireActivity(), requestCode, it.name, triggerTime)
                        } else {
                            cancelAlarm(requireActivity(), requestCode, it.name)
                        }
                    } else {
                        showTimerImage()
                    }
                }
            }
        }

        createTimerChannel(application)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun startTimer(timerFinishAt: Long) {
        val millisInFuture: Long = TimeUtil().millisInFuture(timerFinishAt)
        val millisInterval: Long = TimerConstants.TIMER_TICK_IN_SECONDS * 1000L

        countDownTimer = object : CountDownTimer(millisInFuture, millisInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val time = "${millisUntilFinished / millisInterval}"
                viewModel.time.postValue(time)
            }

            override fun onFinish() {
                showTimerImage()
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    private fun showTimerImage() {
        binding.timerAlarm.visibility = View.GONE
        binding.timerText.visibility = View.GONE
        binding.timerImage.visibility = View.VISIBLE
        (binding.timerImage.drawable as Animatable).start()
    }

    private fun canPostNotifications(applicationContext: Context): Boolean {
        return when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                true
            }
            else -> {
                false
            }
        }
    }
}