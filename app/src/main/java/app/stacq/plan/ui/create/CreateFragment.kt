package app.stacq.plan.ui.create

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentCreateBinding
import app.stacq.plan.util.sentenceCase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateBinding.inflate(inflater, container, false)

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)
        val tasksLocalDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
        val categoryLocalDataSource =
            CategoryLocalDataSource(database.categoryDao(), Dispatchers.Main)

        val tasksRepository = TasksRepository(tasksLocalDataSource, Dispatchers.Main)
        val categoryRepository = CategoryRepository(categoryLocalDataSource, Dispatchers.Main)
        val createViewModelFactory = CreateViewModelFactory(tasksRepository, categoryRepository)
        val createViewModel =
            ViewModelProvider(this, createViewModelFactory)[CreateViewModel::class.java]

        binding.viewmodel = createViewModel
        binding.lifecycleOwner = this

        createViewModel.categories.observe(viewLifecycleOwner) {
            it?.let {
                val categories = it.map { category -> category.name }
                val arrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, categories)
                binding.category.setAdapter(arrayAdapter)
            }
        }

        binding.category.setOnFocusChangeListener { view, _ ->
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        binding.createFab.setOnClickListener { view ->
            val title: String = binding.title.text.toString()
            val categoryName: String = binding.category.text.toString()

            if (title.isEmpty() or categoryName.isEmpty()) {
                Snackbar.make(view, R.string.text_empty_create, Snackbar.LENGTH_LONG)
                    .setAnchorView(view)
                    .show()
                return@setOnClickListener
            }

            val categories: List<Category>? = createViewModel.categories.value
            val category: Category? = categories?.firstOrNull { it.name == categoryName }
            if (category != null) {
                val task = Task(title = title, categoryId = category.id)
                createViewModel.createTask(task)
            }
            val action = CreateFragmentDirections.actionNavCreateToNavTasks()
            this.findNavController().navigate(action)

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}