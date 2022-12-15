package app.stacq.plan.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.goal.GoalLocalDataSource
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSource
import app.stacq.plan.data.source.repository.GoalRepository
import app.stacq.plan.databinding.FragmentGoalsBinding

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val database = PlanDatabase.getDatabase(application)

        val localDataSource = GoalLocalDataSource(database.goalDao())
        val remoteDataSource = GoalRemoteDataSource()
        val goalRepository = GoalRepository(localDataSource, remoteDataSource)

        val viewModel: GoalsViewModel by activityViewModels {
            GoalsViewModelFactory(
                goalRepository
            )
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}