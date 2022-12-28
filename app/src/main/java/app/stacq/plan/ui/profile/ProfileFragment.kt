package app.stacq.plan.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ProfileViewModelFactory
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val taskLocalDataSource = TaskLocalDataSource(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSource()
        val taskRepository = TaskRepository(taskLocalDataSource, taskRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepository =
            CategoryRepository(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = ProfileViewModelFactory(taskRepository, categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        viewModel.categories.observe(viewLifecycleOwner) {
            it?.let { categories ->
                if (categories.isNotEmpty()) {
                    categories.map { category ->
                        viewModel.getCategoryProfileCompleted(category.id)
                    }
                }
            }
        }

        viewModel.completed.observe(viewLifecycleOwner) {
            it?.let { days ->

                if(days.isEmpty()) return@observe

                Log.d("days", days.toString())
                for (day in 1 until days.size) {
                    val completed = days[day].toInt()
                    val params = GridLayout.LayoutParams(
                        GridLayout.spec(GridLayout.UNDEFINED, 1f),
                        GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    )
                    params.height = 8
                    params.width = 8
                    val imageView = ImageView(context)
                    if (completed == 0) {
                        imageView.setImageResource(R.drawable.ic_circle_outline)
                        imageView.setColorFilter(
                            ContextCompat.getColor(requireContext(), R.color.plan_empty),
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    } else {
                        val color = when (completed) {
                            in 1..3 -> R.color.color_plan_green_75
                            in 4..6 -> R.color.color_plan_green_85
                            in 7..10 -> R.color.color_plan_green_95
                            else -> R.color.plan_green
                        }
                        imageView.setImageResource(R.drawable.ic_circle)
                        imageView.setColorFilter(
                            ContextCompat.getColor(requireContext(), color),
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                    }

                    binding.yearGrid.addView(imageView, params)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}