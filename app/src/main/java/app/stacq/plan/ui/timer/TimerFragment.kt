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
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.PlanApiService
import app.stacq.plan.data.source.remote.task.TasksRemoteDataSource
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentTimerBinding
import kotlinx.coroutines.Dispatchers


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
        val taskId: String = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
        val remoteDataSource =
            TasksRemoteDataSource(PlanApiService.planApiService, Dispatchers.Main)
        val tasksRepository = TasksRepository(localDataSource, remoteDataSource, Dispatchers.Main)

        viewModelFactory = TimerViewModelFactory(application, tasksRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[TimerViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


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