package app.stacq.plan.ui.profile

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSource
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: ProfileViewModelFactory
    private lateinit var viewModel: ProfileViewModel

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authStateListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                // signed in
                binding.yearGrid.visibility = View.VISIBLE
                binding.syncImage.visibility = View.INVISIBLE
                binding.syncText.visibility = View.INVISIBLE
            } else {
                // signed out
                binding.yearGrid.visibility = View.INVISIBLE
                binding.syncImage.visibility = View.VISIBLE
                binding.syncText.visibility = View.VISIBLE
                (binding.syncImage.drawable as Animatable).start()
            }
        }

        Firebase.auth.addAuthStateListener(authStateListener)

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val taskLocalDataSourceImpl = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSourceImpl = TaskRemoteDataSourceImpl()
        val taskRepositoryImpl = TaskRepositoryImpl(taskLocalDataSourceImpl, taskRemoteDataSourceImpl)

        val categoryLocalDataSourceImpl = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource = CategoryRemoteDataSource()
        val categoryRepositoryImpl =
            CategoryRepositoryImpl(categoryLocalDataSourceImpl, categoryRemoteDataSource)

        viewModelFactory = ProfileViewModelFactory(taskRepositoryImpl, categoryRepositoryImpl)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { categories ->
                    categories.map {
                        it?.let {
                            it.id?.let { it1 -> viewModel.getCategoryProfileCompleted(it1) }
                        }
                    }

                }
            }
        }

        viewModel.completedMap.observe(viewLifecycleOwner) {
            binding.yearGrid.removeAllViews()
            it?.let { map ->
                if (map.isEmpty()) return@observe
                val completedTotal = viewModel.combine(map)
                Log.d("ProfileFragment", completedTotal.toString())

                completedTotal.forEach { completed ->
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
        Firebase.auth.removeAuthStateListener(authStateListener)
    }
}