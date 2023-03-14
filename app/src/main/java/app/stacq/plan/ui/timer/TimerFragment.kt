package app.stacq.plan.ui.timer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentTimerBinding
import app.stacq.plan.util.CalendarUtil
import app.stacq.plan.util.constants.TimerConstants
import app.stacq.plan.util.constants.TimerConstants.TIMER_TICK_IN_MILLIS
import app.stacq.plan.util.constants.TimerConstants.TIMER_TICK_IN_SECONDS
import app.stacq.plan.util.constants.TimerConstants.TIME_MILLIS_TO_SECONDS
import app.stacq.plan.util.time.TimeUtil
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


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

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        viewModelFactory = TimerViewModelFactory(taskRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TimerViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.timerAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.timerAppBar.setupWithNavController(navController, appBarConfiguration)

        if (canPostNotifications(requireActivity())) {
            binding.timerAlarmCheckbox.visibility = View.VISIBLE
        } else {
            binding.timerAlarmCheckbox.visibility = View.GONE
        }

        binding.timerButtonToggleGroup.addOnButtonCheckedListener { _, checkedId, _ ->
            // Respond to button selection
            when (checkedId) {
                binding.twentyFiveMinutesButton.id -> {
                    val finishAt = TimeUtil().plusSecondsEpoch(TimerConstants.TIMER_TIME_IN_SECONDS_25)
                    viewModel.setTaskTimerFinish(finishAt)
                }
                binding.fiftyTwoMinutesButton.id -> {
                    val finishAt = TimeUtil().plusSecondsEpoch(TimerConstants.TIMER_TIME_IN_SECONDS_52)
                    viewModel.setTaskTimerFinish(finishAt)
                }
                binding.customButton.id -> {
                    val isSystem24Hour = is24HourFormat(context)
                    val clockFormat =
                        if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
                    val picker =
                        MaterialTimePicker.Builder()
                            .setTimeFormat(clockFormat)
                            .setHour(CalendarUtil().hour())
                            .setMinute(CalendarUtil().minute())
                            .setTitleText(getString(R.string.timer_select_time))
                            .setInputMode(INPUT_MODE_CLOCK)
                            .build()
                    picker.show(requireActivity().supportFragmentManager, "timer_tag")

                    picker.addOnPositiveButtonClickListener {
                        val time = TimeUtil().timeSeconds(picker.hour, picker.minute)
                        val finishAt = TimeUtil().plusSecondsEpoch(time)
                        viewModel.setTaskTimerFinish(finishAt)
                    }
                }
            }
        }

        viewModel.task.observe(viewLifecycleOwner) { task ->
            task?.let {
                if (task.timerFinishAt != 0L) {
                    // timer set
                    binding.timerButtonToggleGroup.isEnabled = false

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
                // convert to minutes
                var time = millisUntilFinished / (TIMER_TICK_IN_MILLIS * TIMER_TICK_IN_SECONDS)
                if (time < 1L) {
                    // convert to seconds below 1 minute
                    time = millisUntilFinished / TIME_MILLIS_TO_SECONDS
                }
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