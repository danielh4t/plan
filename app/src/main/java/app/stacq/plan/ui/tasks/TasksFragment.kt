package app.stacq.plan.ui.tasks

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TaskLocalDataSource
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.databinding.FragmentTasksBinding
import app.stacq.plan.util.ui.MarginItemDecoration
import java.util.*


class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: TasksViewModelFactory
    private lateinit var viewModel: TasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)

        val taskLocalDataSource = TaskLocalDataSource(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSource()
        val taskRepository = TaskRepository(taskLocalDataSource, taskRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepository =
            CategoryRepository(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = TasksViewModelFactory(taskRepository, categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val tasksAdapter = TasksAdapter(viewModel)
        binding.tasksList.adapter = tasksAdapter
        binding.tasksList.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.list_margin)))

        val taskItemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP, ItemTouchHelper.LEFT,
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val adapter = recyclerView.adapter as TasksAdapter
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                val tasks = adapter.currentList.toMutableList()
                Collections.swap(tasks, fromPosition, toPosition)

                val higherTask = tasks[fromPosition]!!
                val lowerTask = tasks[toPosition]!!
                // if equal or lower than low priority increment
                if (higherTask.priority <= lowerTask.priority) {
                    higherTask.priority = lowerTask.priority++
                    viewModel.updatePriority(higherTask)
                }

                adapter.submitList(tasks)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = viewModel.tasksCategory.value?.get(position)
                if (task != null) {
                    viewModel.delete(task)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView


                val background = ColorDrawable(Color.RED)

                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)
            }
        }

        ItemTouchHelper(taskItemTouchHelperCallback).attachToRecyclerView(binding.tasksList)

        binding.createTaskFab.setOnClickListener {
            if (viewModel.categories > 0) {
                val action = TasksFragmentDirections.actionNavTasksToNavCreate()
                this.findNavController().navigate(action)
            } else {
                // empty categories navigate to create category first
                val action = TasksFragmentDirections.actionNavTasksToNavCreateCategory()
                this.findNavController().navigate(action)
            }
        }

        viewModel.tasksCategory.observe(viewLifecycleOwner) {
            it?.let {
                tasksAdapter.submitList(it)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}