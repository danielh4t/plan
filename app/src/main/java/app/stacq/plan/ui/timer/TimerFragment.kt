package app.stacq.plan.ui.timer

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentTimerBinding
import app.stacq.plan.util.cancelAlarm
import app.stacq.plan.util.createTimerChannel
import app.stacq.plan.util.setAlarm


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
                cancelAlarm(
                    application,
                    task.timerFinishAt,
                    task.name
                )

            }
        }

        viewModel.timerFinished.observe(viewLifecycleOwner) {
            if (it) {
                (binding.timerImage.drawable as Animatable).start()
            }
        }

        createTimerChannel(application)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}