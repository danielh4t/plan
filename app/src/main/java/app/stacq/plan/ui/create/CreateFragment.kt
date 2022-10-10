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
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.task.TasksRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.FragmentCreateBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

        val localDataSource = TasksLocalDataSource(database.taskDao())
        val remoteDataSource = TasksRemoteDataSource(Firebase.firestore)
        val tasksRepository = TasksRepository(localDataSource, remoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRepository = CategoryRepository(categoryLocalDataSource)

        viewModelFactory = CreateViewModelFactory(tasksRepository, categoryRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[CreateViewModel::class.java]

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.categories.observe(viewLifecycleOwner) {
            it?.let {
                val categories = it.map { category -> category.name }
                val arrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, categories)
                binding.category.setAdapter(arrayAdapter)
            }
        }

        binding.category.setOnFocusChangeListener { focusedView, _ ->
            val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }

        binding.createFab.setOnClickListener { clickedView ->
            val title: String = binding.title.text.toString()
            val categoryName: String = binding.category.text.toString()

            if (title.isEmpty() or categoryName.isEmpty()) {
                Snackbar.make(clickedView, R.string.empty_details, Snackbar.LENGTH_LONG)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            viewModel.createTask(title, categoryName)
            val action = CreateFragmentDirections.actionNavCreateToNavTasks()
            this.findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}