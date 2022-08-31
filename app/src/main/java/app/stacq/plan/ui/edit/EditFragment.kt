package app.stacq.plan.ui.edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.stacq.plan.databinding.FragmentEditBinding

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: EditViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}