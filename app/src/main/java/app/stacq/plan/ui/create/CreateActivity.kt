package app.stacq.plan.ui.create

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.stacq.plan.ui.main.MainActivity
import app.stacq.plan.R
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSource
import app.stacq.plan.data.source.local.task.TasksLocalDataSource
import app.stacq.plan.data.source.repository.CategoryRepository
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
        val tasksLocalDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
        val categoryLocalDataSource = CategoryLocalDataSource(database.categoryDao(), Dispatchers.Main)

        val tasksRepository = TasksRepository(tasksLocalDataSource, Dispatchers.Main)
        val categoryRepository = CategoryRepository(categoryLocalDataSource, Dispatchers.Main)
        val createViewModelFactory = CreateViewModelFactory(tasksRepository, categoryRepository)
        val createViewModel =
            ViewModelProvider(this, createViewModelFactory)[CreateViewModel::class.java]

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create)
        binding.lifecycleOwner = this
        binding.viewmodel = createViewModel


        createViewModel.categories.observe(this) {
            it?.let {
                val categories = it.map { category -> category.name }
                val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_menu_item, categories)
                binding.category.setAdapter(arrayAdapter)
            }
        }

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

            val categories: List<Category>? = createViewModel.categories.value;
            val category: Category? = categories?.firstOrNull { it.name == categoryName }
            if (category != null) {
                val task = Task(title = title, categoryId = category.id)
                createViewModel.createTask(task)
            }

            startActivity(Intent(this, MainActivity::class.java))
        }


    }



}