package app.stacq.plan.ui.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentInputBinding
import app.stacq.plan.util.ViewModelFactory
import kotlinx.coroutines.Dispatchers

class InputFragment : Fragment() {


    private var _binding: FragmentInputBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)

        val tasksRepository = TasksRepository(localDataSource, Dispatchers.Main)
        val viewModelFactory = ViewModelFactory(tasksRepository)
        val inputViewModel = ViewModelProvider(this, viewModelFactory)[InputViewModel::class.java]

        _binding = FragmentInputBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.lifecycleOwner = this
        binding.viewmodel = inputViewModel
        inputViewModel.saved.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.save.findNavController()
                    .navigate(InputFragmentDirections.actionInputFragmentToNavTasks())
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}