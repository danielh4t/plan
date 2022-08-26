package app.stacq.plan.ui.create

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.MainActivity
import app.stacq.plan.R
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.remote.REMOTE_ENDPOINT
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.databinding.ActivityCreateBinding
import app.stacq.plan.util.sentenceCase
import coil.load
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers

class CreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = requireNotNull(this).application
        val database = PlanDatabase.getDatabase(application)
        val localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)

        val tasksRepository = TasksRepository(localDataSource, Dispatchers.Main)
        val createViewModelFactory = CreateViewModelFactory(tasksRepository)
        val createViewModel =
            ViewModelProvider(this, createViewModelFactory)[CreateViewModel::class.java]

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create)
        binding.lifecycleOwner = this
        binding.viewmodel = createViewModel

        val categories = Category.values().map { it.name.sentenceCase() }.toTypedArray()
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_menu_item, categories)
        binding.category.setAdapter(arrayAdapter)
        binding.category.setOnFocusChangeListener { view, _ ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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
            val category: Category = Category.valueOf(categoryName)
            val task = Task(title = title, category = category)
            createViewModel.createTask(task)
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