package app.stacq.plan.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentEditBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: EditViewModelFactory
    private lateinit var viewModel: EditViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = EditFragmentArgs.fromBundle(requireArguments())
        val taskId = args.taskId

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val localDataSource = TaskLocalDataSource(database.taskDao())
        val remoteDataSource = TaskRemoteDataSource(Firebase.firestore)

        val tasksRepository = TasksRepository(localDataSource, remoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepository = CategoryRepository(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = EditViewModelFactory(tasksRepository, categoryRepository, taskId)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditViewModel::class.java]
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.task.observe(viewLifecycleOwner) { task ->
            binding.editTitle.setText(task.title)
            binding.editCategory.setText(task.categoryName)
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            it?.let {
                val categories = it.map { category -> category.name }
                val arrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, categories)
                binding.editCategory.setAdapter(arrayAdapter)
            }
        }

        binding.editFab.setOnClickListener { clickedView ->
            val title: String = binding.editTitle.text.toString()
            val categoryName: String = binding.editCategory.text.toString()

            if (title.isEmpty() or categoryName.isEmpty()) {
                Snackbar.make(clickedView, R.string.empty_details, Snackbar.LENGTH_LONG)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            val categories: List<Category>? = viewModel.categories.value
            val category: Category? = categories?.firstOrNull { it.name == categoryName }
            if (category != null) {
                viewModel.editTask(title, category.id)
            }
            val action = EditFragmentDirections.actionNavEditToNavTasks()
            this.findNavController().navigate(action)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}