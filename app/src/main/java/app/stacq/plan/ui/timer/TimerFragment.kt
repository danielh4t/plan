package app.stacq.plan.ui.timer

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.R
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.PlanApiService
import app.stacq.plan.data.source.remote.task.TasksRemoteDataSource
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentTimerBinding
import app.stacq.plan.util.createNotificationChannel
import kotlinx.coroutines.Dispatchers


class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TimerViewModelFactory
    private lateinit var viewModel: TimerViewModel

    private var alarmManager: AlarmManager? = null
    private var notificationPendingIntent: PendingIntent? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTimerBinding.inflate(inflater, container, false)

        val args = TimerFragmentArgs.fromBundle(requireArguments())
        val task: TaskCategory = args.taskCategory

        val application = requireNotNull(this.activity).application

        val database = PlanDatabase.getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
        val remoteDataSource =
            TasksRemoteDataSource(PlanApiService.planApiService, Dispatchers.Main)
        val tasksRepository = TasksRepository(localDataSource, remoteDataSource, Dispatchers.Main)

        viewModelFactory = TimerViewModelFactory(tasksRepository, task)
        viewModel = ViewModelProvider(this, viewModelFactory)[TimerViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.timerAlarm.observe(viewLifecycleOwner) {
            val canPostNotifications = viewModel.notificationPermission.value == true
            if (it && canPostNotifications) {
                setAlarm(application, task.timerFinishAt, task.title)
            } else {
                cancelAlarm()
            }
        }

        viewModel.timerFinished.observe(viewLifecycleOwner) {
            if (it) {
                (binding.timerImage.drawable as Animatable).start()
            }
        }

        createTimerChannel(application)

        return binding.root
    }

    private fun createTimerChannel(applicationContext: Context) {
        val channelId = applicationContext.getString(R.string.timer_channel_id)
        val channelName = applicationContext.getString(R.string.timer_channel_name)
        val description = applicationContext.getString(R.string.timer_channel_description)
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channelId, channelName, description)
    }

    private fun setAlarm(applicationContext: Context, finishAt: Long, title: String) {

        val notificationIntent: Intent = Intent(applicationContext, TimerReceiver::class.java)
            .putExtra(TimerConstants.TIMER_RECEIVER_ID_KEY, finishAt)
            .putExtra(TimerConstants.TIMER_RECEIVER_TEXT_KEY, title)


        notificationPendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val triggerTime = SystemClock.elapsedRealtime() + viewModel.millisInFuture(finishAt)
        alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    notificationPendingIntent
                )
            }
        } else {
            val intent = Intent().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                }
            }
            startActivity(intent)
        }

    }

    private fun cancelAlarm() {
        if (notificationPendingIntent != null) {
            alarmManager?.cancel(notificationPendingIntent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}