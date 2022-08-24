package app.stacq.plan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.IMAGES_MAX
import app.stacq.plan.data.source.remote.REMOTE_ENDPOINT
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
        val tasksViewModel =
            ViewModelProvider(this, taskViewModelFactory)[TasksViewModel::class.java]

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create)
        binding.lifecycleOwner = this
        binding.viewmodel = tasksViewModel

        binding.createFab.setOnClickListener {
            val title: String = binding.title.text.toString()
            val task = Task(title = title, category = Category.CODE)
            tasksViewModel.save(task)
            startActivity(Intent(this, MainActivity::class.java))
        }

        val seed = (0..IMAGES_MAX).random()
        val image = imageUrl(seed)
        val placeholder = placeHolder(seed)
        binding.createImage.load(image) {
            crossfade(true)
            placeholder(placeholder)
        }
    }

    private fun imageUrl(seed: Int): String {
        val endpoint = REMOTE_ENDPOINT
        val file = "$seed.png"
        return "$endpoint/images/$file"
    }

    private fun placeHolder(seed: Int): Int {
        return when (seed) {
            1 -> R.color.placeholder_1
            2 -> R.color.placeholder_2
            3 -> R.color.placeholder_3
            4 -> R.color.placeholder_4
            5 -> R.color.placeholder_5
            else -> R.color.placeholder_0
        }
    }


}