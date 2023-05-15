package app.stacq.plan.ui.biteModify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import app.stacq.plan.R
import app.stacq.plan.data.repository.bite.BiteRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.bite.BiteLocalDataSourceImpl
import app.stacq.plan.data.source.local.task.TaskLocalDataSourceImpl
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.task.TaskRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentBiteModifyBinding
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BiteModifyFragment : Fragment() {

    private var _binding: FragmentBiteModifyBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: BiteModifyViewModelFactory
    private lateinit var viewModel: BiteModifyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBiteModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = BiteModifyFragmentArgs.fromBundle(requireArguments())
        val taskId: String = args.taskId
        val biteId: String? = args.biteId

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)

        val biteLocalDataSource = BiteLocalDataSourceImpl(database.biteDao())
        val biteRemoteDataSource = BiteRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val biteRepository = BiteRepositoryImpl(biteLocalDataSource, biteRemoteDataSource)

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.taskDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        viewModelFactory =
            BiteModifyViewModelFactory(biteRepository, taskRepository, taskId, biteId)
        viewModel = ViewModelProvider(this, viewModelFactory)[BiteModifyViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.biteModifyAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.biteModifyAppBar.setupWithNavController(navController, appBarConfiguration)

        viewModel.bite.observe(viewLifecycleOwner) {
            binding.bite = it
        }

        binding.biteModifySaveButton.setOnClickListener {
            val name: String = binding.biteModifyNameEditText.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), R.string.bite_name_required, Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (biteId == null) {
                viewModel.create(name)
            } else {
                viewModel.update(name)
            }

            val action = BiteModifyFragmentDirections.actionNavBiteModifyToNavTask(taskId)
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}