package app.stacq.plan.ui.timer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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
import app.stacq.plan.ui.timer.TimerConstants.TIMER_TICK_IN_MILLIS
import app.stacq.plan.ui.timer.TimerConstants.TIMER_TICK_IN_SECONDS
import app.stacq.plan.ui.timer.TimerConstants.TIME_MILLIS_TO_SECONDS
import app.stacq.plan.util.TimeUtil


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
            binding.timerAlarmCheckbox.visibility = View.VISIBLE
        } else {
            binding.timerAlarmCheckbox.visibility = View.GONE
        }

        viewModelFactory = TimerViewModelFactory(taskRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TimerViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.task.observe(viewLifecycleOwner) { task ->
            task?.let {
                if (task.timerFinishAt == 0L) {
                    // timer not set
                    viewModel.setTaskTimerFinish()
                } else {
                    // timer set
                    if (TimeUtil().millisInFuture(it.timerFinishAt) > 0L) {
                        // timer not finished
                        startTimer(task.timerFinishAt)
                        val requestCode: Int = task.timerFinishAt.toInt()
                        val triggerTime = TimeUtil().alarmTriggerTimer(task.timerFinishAt)
                        // set alarm to post notification
                        if (canPostNotifications(requireActivity()) && task.timerAlarm) {
                            setAlarm(requireActivity(), requestCode, task.name, triggerTime)
                        } else {
                            cancelAlarm(requireActivity(), requestCode, task.name)
                        }
                    } else {
                        // timer finished
                        (binding.timerImage.drawable as Animatable).start()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun startTimer(timerFinishAt: Long) {
        val millisInFuture: Long = TimeUtil().millisInFuture(timerFinishAt)
        val millisInterval: Long = TIMER_TICK_IN_MILLIS

        countDownTimer = object : CountDownTimer(millisInFuture, millisInterval) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("Millis", millisUntilFinished.toString())
                // convert to minutes
                var time = millisUntilFinished / (TIMER_TICK_IN_MILLIS * TIMER_TICK_IN_SECONDS)
                if (time < 1L) {
                    // convert to seconds below 1 minute
                    time = millisUntilFinished / TIME_MILLIS_TO_SECONDS
                }
                Log.d("Time", time.toString())
                viewModel.time.postValue(time)

            }

            override fun onFinish() {
                viewModel.time.postValue(0L)
            }
        }.start()
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