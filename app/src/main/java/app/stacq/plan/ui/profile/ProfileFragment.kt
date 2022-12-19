package app.stacq.plan.ui.profile

import android.os.Bundle
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
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
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

        viewModelFactory = ProfileViewModelFactory(taskRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        viewModel.taskAnalysis.observe(viewLifecycleOwner) { tasks ->
            binding.yearGrid.removeAllViews()
            if (tasks != null) {
                for (day in 1..viewModel.days) {
                    val params = GridLayout.LayoutParams(
                        GridLayout.spec(GridLayout.UNDEFINED, 1f),
                        GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    )
                    params.height = 8
                    params.width = 8
                    val imageView = ImageView(context)
                    imageView.setImageResource(R.drawable.ic_circle_outline)
                    imageView.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.color_plan_green_50),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    // check if in list
                    val daysMap = tasks.associate { it.day to it.completed }
                    if (daysMap.containsKey(day)) {
                        val completed = daysMap[day]
                        if (completed != null) {
                            val color = when (completed) {
                                in 1..5 -> R.color.color_plan_green_50
                                in 6..10 -> R.color.color_plan_green_75
                                else -> R.color.plan_green
                            }
                            imageView.setImageResource(R.drawable.ic_circle)
                            imageView.setColorFilter(
                                ContextCompat.getColor(requireContext(), color),
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
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