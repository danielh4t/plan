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
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentTimerBinding
import app.stacq.plan.util.createNotificationChannel
import app.stacq.plan.util.millisInFuture


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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = TimerFragmentArgs.fromBundle(requireArguments())
        val task: Task = args.task
        val notify: Boolean = args.notify

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val localDataSource = TaskLocalDataSource(database.taskDao())
        val remoteDataSource = TaskRemoteDataSource()
        val taskRepository = TaskRepository(localDataSource, remoteDataSource)

        viewModelFactory = TimerViewModelFactory(taskRepository, task, notify)
        viewModel = ViewModelProvider(this, viewModelFactory)[TimerViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.timerAlarm.observe(viewLifecycleOwner) {
            if (it && notify) {
                setAlarm(application, task.timerFinishAt, task.name)
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
                finishAt.toInt(),
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        val triggerTime = SystemClock.elapsedRealtime() + millisInFuture(finishAt)
        alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == true) {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    triggerTime,
                    notificationPendingIntent
                )
            } else {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                }
                startActivity(intent)
            }

        } else {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                notificationPendingIntent
            )
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