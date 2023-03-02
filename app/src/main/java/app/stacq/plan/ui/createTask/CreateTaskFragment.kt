package app.stacq.plan.ui.createTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.databinding.FragmentCreateTaskBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: CreateTaskViewModelFactory
    private lateinit var viewModel: CreateTaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)

        val taskLocalDataSourceImpl = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSourceImpl = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepositoryImpl = TaskRepositoryImpl(taskLocalDataSourceImpl, taskRemoteDataSourceImpl)

        val categoryLocalDataSourceImpl = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepositoryImpl =
            CategoryRepositoryImpl(categoryLocalDataSourceImpl, categoryRemoteDataSource)

        viewModelFactory = CreateTaskViewModelFactory(taskRepositoryImpl, categoryRepositoryImpl)
        viewModel = ViewModelProvider(this, viewModelFactory)[CreateTaskViewModel::class.java]

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.createCategoryChipGroup.removeAllViews()
            categories?.let {
                it.map { category ->
                    val chip = layoutInflater.inflate(
                        R.layout.chip_layout,
                        binding.createCategoryChipGroup,
                        false
                    ) as Chip
                    chip.text = category.name
                    chip.contentDescription = getString(R.string.content_category_chip, category.name)
                    chip.tag = category.id
                    binding.createCategoryChipGroup.addView(chip)
                }
            }
        }

        binding.createFab.setOnClickListener { clickedView ->
            val name: String = binding.name.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(clickedView, R.string.task_name_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val checkedId: Int = binding.createCategoryChipGroup.checkedChipId
            if (checkedId == View.NO_ID) {
                Snackbar.make(clickedView, R.string.empty_category_details, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val checkedChip = binding.createCategoryChipGroup.findViewById<Chip>(checkedId)
            val categoryId = checkedChip.tag as String

            val taskId = viewModel.create(name, categoryId)

            val action = CreateTaskFragmentDirections.actionNavCreateTaskToNavTask(taskId)
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}