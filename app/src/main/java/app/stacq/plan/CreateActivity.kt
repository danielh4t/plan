package app.stacq.plan

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.REMOTE_ENDPOINT
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.ActivityCreateBinding
import app.stacq.plan.ui.tasks.TasksViewModel
import app.stacq.plan.ui.tasks.TasksViewModelFactory
import app.stacq.plan.util.titleCase
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

        val categories = Category.values().map { it.name.titleCase() }.toTypedArray()
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_menu_item, categories)
        binding.category.setAdapter(arrayAdapter)

        binding.createFab.setOnClickListener {
            val title: String = binding.title.text.toString()
            val category: Category = Category.valueOf(binding.category.text.toString())
            val task = Task(title = title, category = category)
            tasksViewModel.save(task)
            startActivity(Intent(this, MainActivity::class.java))
        }

        val max = Category.values().size - 1
        val seed = (0..max).random()
        val category = Category.values()[seed]
        val image = imageUrl(category)
        val placeholder = placeHolder(category)
        binding.createImage.load(image) {
            crossfade(true)
            placeholder(placeholder)
        }
    }

    private fun imageUrl(category: Category): String {
        val endpoint = REMOTE_ENDPOINT
        val file = "${category.name.lowercase()}.png"
        return "$endpoint/images/$file"
    }

    private fun placeHolder(category: Category): Int {
        return when (category) {
            Category.Code -> R.color.placeholder_code
            Category.Hack -> R.color.placeholder_hack
            Category.Life -> R.color.placeholder_life
            Category.Work -> R.color.placeholder_work
        }
    }


}