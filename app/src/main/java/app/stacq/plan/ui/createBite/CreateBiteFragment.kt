package app.stacq.plan.ui.createBite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import app.stacq.plan.R
import app.stacq.plan.data.source.local.PlanDatabase.Companion.getDatabase
import app.stacq.plan.data.source.local.bite.BiteLocalDataSourceImpl
import app.stacq.plan.data.source.remote.bite.BiteRemoteDataSourceImpl
import app.stacq.plan.data.repository.bite.BiteRepositoryImpl
import app.stacq.plan.databinding.FragmentCreateBiteBinding
import com.google.android.material.snackbar.Snackbar

class CreateBiteFragment : Fragment() {

    private var _binding: FragmentCreateBiteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: CreateBiteViewModelFactory
    private lateinit var viewModel: CreateBiteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateBiteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = CreateBiteFragmentArgs.fromBundle(requireArguments())
        val taskId = args.taskId

//        val application = requireNotNull(this.activity).application
//        val database = getDatabase(application)
//
//        val biteLocalDataSource = BiteLocalDataSourceImpl(database.biteDao())
//        val biteRemoteDataSourceImpl = BiteRemoteDataSourceImpl()
//        val biteRepositoryImpl = BiteRepositoryImpl(biteLocalDataSource, biteRemoteDataSourceImpl)
//
//        viewModelFactory = CreateBiteViewModelFactory(biteRepositoryImpl)
        viewModel = ViewModelProvider(this, viewModelFactory)[CreateBiteViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.createFab.setOnClickListener { clickedView ->
            val name: String = binding.biteName.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(clickedView, R.string.bite_name_required, Snackbar.LENGTH_SHORT)
                    .setAnchorView(clickedView)
                    .show()
                return@setOnClickListener
            }

            viewModel.create(name, taskId)

            val action = CreateBiteFragmentDirections.actionNavCreateBiteToNavTask(taskId)
            this.findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}