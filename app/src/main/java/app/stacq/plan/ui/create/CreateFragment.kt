package app.stacq.plan.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentCreateBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: CreateViewModelFactory
    private lateinit var viewModel: CreateViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val localDataSource = TaskLocalDataSource(database.taskDao())
        val remoteDataSource = TaskRemoteDataSource()
        val tasksRepository = TasksRepository(localDataSource, remoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepository =
            CategoryRepository(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = CreateViewModelFactory(tasksRepository, categoryRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[CreateViewModel::class.java]

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.categoryChipGroup.removeAllViews()
            categories.map { category ->
                val chip = layoutInflater.inflate(
                    R.layout.chip_layout,
                    binding.categoryChipGroup,
                    false
                ) as Chip
                chip.text = category.name
                chip.tag = category.id
                binding.categoryChipGroup.addView(chip)
            }
        }

        binding.createFab.setOnClickListener { clickedView ->
            val title: String = binding.title.text.toString()

            val checkedId: Int = binding.categoryChipGroup.checkedChipId
            val checkedChip = binding.categoryChipGroup.findViewById<Chip>(checkedId)
            val categoryId = checkedChip.tag as String

            if (title.isEmpty()) {
                Snackbar.make(clickedView, R.string.empty_task_details, Snackbar.LENGTH_LONG)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            viewModel.createTask(title, categoryId)

            val action = CreateFragmentDirections.actionNavCreateToNavTasks()
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}