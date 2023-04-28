package app.stacq.plan.ui.goals

import android.app.Activity
import android.content.IntentSender
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.MainNavDirections
import app.stacq.plan.R
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.goal.GoalRepositoryImpl
import app.stacq.plan.data.source.local.PlanDatabase
import app.stacq.plan.data.source.local.category.CategoryLocalDataSourceImpl
import app.stacq.plan.data.source.local.goal.GoalLocalDataSourceImpl
import app.stacq.plan.data.source.remote.category.CategoryRemoteDataSourceImpl
import app.stacq.plan.data.source.remote.goal.GoalRemoteDataSourceImpl
import app.stacq.plan.databinding.FragmentGoalsBinding
import app.stacq.plan.util.handleSignInWithFirebase
import app.stacq.plan.util.launchSignIn
import app.stacq.plan.util.ui.BottomMarginItemDecoration
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: GoalsViewModelFactory
    private lateinit var viewModel: GoalsViewModel

    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var oneTapClient: SignInClient
    private var showOneTapUI: Boolean = true
    private var hasCategories: Boolean = false

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

        val goalLocalDataSource = GoalLocalDataSourceImpl(database.goalDao())
        val goalRemoteDataSource = GoalRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val goalRepository = GoalRepositoryImpl(goalLocalDataSource, goalRemoteDataSource)

        val categoryLocalDataSource = CategoryLocalDataSourceImpl(database.categoryDao())
        val categoryRemoteDataSource =
            CategoryRemoteDataSourceImpl(Firebase.auth, Firebase.firestore)
        val categoryRepository =
            CategoryRepositoryImpl(categoryLocalDataSource, categoryRemoteDataSource)

        viewModelFactory = GoalsViewModelFactory(goalRepository, categoryRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[GoalsViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.goalsAppBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(context)

        binding.goalsAccountImageView.setOnClickListener {
            val message = if (Firebase.auth.currentUser == null) {
                resources.getString(R.string.sign_in_sync)
            } else {
                resources.getString(R.string.sign_out_sync)
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.backup_sync))
                .setMessage(message)
                .setNegativeButton(resources.getString(R.string.no_thanks)) { _, _ ->
                    // Respond to negative button press
                    Toast.makeText(requireContext(), R.string.no_backup, Toast.LENGTH_SHORT).show()
                }
                .setPositiveButton(resources.getString(R.string.yes_please)) { _, _ ->
                    // Respond to positive button press
                    handleAuthentication()
                }
                .show()
        }

        val navController = findNavController()

        oneTapClient = Identity.getSignInClient(requireActivity())
        authStateListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                // signed in
                if (user.photoUrl !== null) {
                    binding.goalsAccountImageView.load(user.photoUrl) {
                        crossfade(true)
                        size(ViewSizeResolver(binding.goalsAccountImageView))
                        transformations(CircleCropTransformation())
                    }
                }
            } else {
                // signed out
                binding.goalsAccountImageView.setImageResource(R.drawable.ic_account_circle)
            }
        }

        Firebase.auth.addAuthStateListener(authStateListener)

        val goalNavigateListener = GoalNavigateListener {
            val action = GoalsFragmentDirections.actionNavGoalsToNavGoal(it)
            navController.navigate(action)
        }

        val goalCompletedListener = GoalCompletedListener { viewModel.complete(it) }

        val adapter = GoalsAdapter(goalNavigateListener, goalCompletedListener)
        binding.goalsList.adapter = adapter

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val goal = adapter.getGoal(position)
                viewModel.delete(goal)
                Snackbar.make(view, R.string.goal_deleted, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addGoalFab)
                    .setAction(R.string.undo) {
                        viewModel.undoDelete(goal)
                    }
                    .show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {

                    // Set the background color to red
                    val background = ColorDrawable(Color.RED)

                    // Set the bounds of the background
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                    // Draw the background
                    background.draw(c)
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.goalsList)

        binding.goalsList.addItemDecoration(
            BottomMarginItemDecoration(
                resources.getDimensionPixelSize(
                    R.dimen.vertica_list_margin
                )
            )
        )

        binding.addGoalFab.setOnClickListener()
        {
            if (hasCategories) {
                // Navigate to create goal
                val action = GoalsFragmentDirections.actionNavGoalsToNavGoalModify(null)
                navController.navigate(action)
            } else {
                Snackbar.make(it, R.string.no_categories, Snackbar.LENGTH_LONG)
                    .setAnchorView(it)
                    .setAction(R.string.create) {
                        val action = MainNavDirections.actionGlobalNavCategories()
                        navController.navigate(action)
                    }
                    .show()
            }
        }

        viewModel.goals.observe(viewLifecycleOwner)
        {
            it?.let {
                adapter.submitList(it)
            }
        }

        viewModel.categories.observe(viewLifecycleOwner)
        {
            it?.let {
                hasCategories = it.isNotEmpty()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Firebase.auth.removeAuthStateListener(authStateListener)
    }

    private fun handleAuthentication() {
        // if user isn't signed in and hasn't already declined to use One Tap sign-in
        if (showOneTapUI && Firebase.auth.currentUser == null) {
            val clientId = getString(R.string.default_web_client_id)
            oneTapClient.launchSignIn(clientId)
                .addOnSuccessListener { result ->
                    try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        signInLauncher.launch(intentSenderRequest)

                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("Plan", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d("Plan", "Failure: ${e.localizedMessage}")
                }
            // don't
            showOneTapUI = false
        } else {
            // sign out
            Firebase.auth.signOut()
            oneTapClient.signOut()
            showOneTapUI = true
        }
    }

    private val signInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                oneTapClient.handleSignInWithFirebase(it.data)
            }
        }
}