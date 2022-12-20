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
import app.stacq.plan.util.alarmTriggerTimer
import app.stacq.plan.util.createTimerChannel
import app.stacq.plan.util.isTimeInFuture
import app.stacq.plan.util.millisInFuture


class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TimerViewModelFactory
    private lateinit var viewModel: TimerViewModel


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

        val canNotify = hasPostNotificationsPermission(application)
        if (!canNotify) binding.timerAlarm.visibility = View.GONE

        viewModelFactory = TimerViewModelFactory(taskRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TimerViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.task.observe(viewLifecycleOwner) {
            if (it != null) {
                // timer finished show animation
                if (!isTimeInFuture(it.timerFinishAt)) {
                    binding.timerImage.visibility = View.VISIBLE
                    (binding.timerImage.drawable as Animatable).start()
                }
            }
        }

        val task = viewModel.task.value
        task?.let {
            if (isTimeInFuture(it.timerFinishAt)) {
                startTimer(it.timerFinishAt)

                val requestCode: Int = it.timerFinishAt.toInt()
                val triggerTime = alarmTriggerTimer(it.timerFinishAt)

                if (canNotify) {
                    setAlarm(application, requestCode, task.name, triggerTime)
                } else {
                    cancelAlarm(application, requestCode, task.name)
                }
            } else {
                // timer not started
                viewModel.updateTaskTimerFinish()
            }
        }

        createTimerChannel(application)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startTimer(timerFinishAt: Long) {
        val millisInFuture: Long = millisInFuture(timerFinishAt)
        val millisInterval: Long = TimerConstants.TIMER_TICK_IN_SECONDS * 1000L

        object : CountDownTimer(millisInFuture, millisInterval) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerText.text = "${millisUntilFinished / millisInterval}"
            }

            override fun onFinish() {
                binding.timerText.visibility = View.INVISIBLE
            }
        }.start()
    }

    private fun hasPostNotificationsPermission(applicationContext: Context): Boolean {
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