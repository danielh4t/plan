package app.stacq.plan.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import app.stacq.plan.R
import app.stacq.plan.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private lateinit var authStateListener: AuthStateListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.outputWorkInfo.observe(viewLifecycleOwner, workInfoObserver())

        viewModel.taskAnalysis.observe(viewLifecycleOwner) { tasks ->
            binding.monthGrid.removeAllViews()
            if (tasks != null) {
                val daysMap = tasks.associate { it.day to it.completed }
                for (day in 0..viewModel.days) {

                    val params = GridLayout.LayoutParams()
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT
                    params.width = GridLayout.LayoutParams.WRAP_CONTENT
                    params.marginStart = 16
                    params.marginEnd = 16
                    params.topMargin = 8
                    params.bottomMargin = 8
                    val imageView = ImageView(context)
                    imageView.layoutParams = params
                    imageView.setImageResource(R.drawable.ic_circle)
                    imageView.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.color_plan_green_50),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    // check if in list
                    if (daysMap.containsKey(day)) {
                        val completed = daysMap[day]
                        if (completed != null) {
                            val color = when (completed) {
                                in 0..4 -> R.color.color_plan_green_50
                                in 5..9 -> R.color.color_plan_green_75
                                else -> R.color.plan_green
                            }
                            imageView.setColorFilter(
                                ContextCompat.getColor(requireContext(), color),
                                android.graphics.PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                    binding.monthGrid.addView(imageView)
                    binding.percentageText.text = viewModel.calculatePercentage(daysMap.size)
                }
            }
        }

        binding.syncButton.setOnClickListener {
            viewModel.sync()
        }

        authStateListener = AuthStateListener {
            it.currentUser.let { user ->
                if (user == null) {
                    binding.syncButton.visibility = View.GONE
                } else {
                    binding.syncButton.visibility = View.VISIBLE
                }
            }
        }
        Firebase.auth.addAuthStateListener(authStateListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Firebase.auth.removeAuthStateListener(authStateListener)
        _binding = null
    }

    private fun workInfoObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->
            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            val workInfo = listOfWorkInfo[0]
            if (workInfo.state.isFinished) {
                Toast.makeText(context, R.string.sync_complete, Toast.LENGTH_SHORT).show()
            }
        }
    }

}