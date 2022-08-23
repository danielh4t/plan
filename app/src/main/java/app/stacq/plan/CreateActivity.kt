package app.stacq.plan

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.ActivityCreateBinding
import app.stacq.plan.ui.tasks.TasksViewModel
import app.stacq.plan.ui.tasks.TasksViewModelFactory
import coil.load
import kotlinx.coroutines.Dispatchers

class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = requireNotNull(this).application
        val database = PlanDatabase.getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)

        val tasksRepository = TasksRepository(localDataSource, Dispatchers.Main)
        val taskViewModelFactory = TasksViewModelFactory(tasksRepository)
        val tasksViewModel = ViewModelProvider(this, taskViewModelFactory)[TasksViewModel::class.java]

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create)
        binding.lifecycleOwner = this
        binding.viewmodel = tasksViewModel

        binding.createFab.setOnClickListener {
            val title: String = binding.title.text.toString()
            val task = Task(title = title, category = Category.CODE)
            tasksViewModel.save(task)
            startActivity(Intent(this, MainActivity::class.java))
        }


        binding.createImage.load("https://plan-node-api.herokuapp.com/images/image.png") {
            placeholder(R.color.purple_500)
        }

    }
}