package com.dbd.market.screens.fragments.introduction

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.dbd.market.R
import com.dbd.market.databinding.FragmentLoginBinding
import com.dbd.market.screens.activities.MarketActivity
import com.dbd.market.utils.*
import com.dbd.market.utils.Constants.REQUEST_CODE_GOOGLE_SIGN_IN
import com.dbd.market.utils.Constants.SUCCESSFULLY_ACCOUNT_LOGIN_TOAST_MESSAGE
import com.dbd.market.viewmodels.introduction.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUnderlineToLinkTextView(getString(R.string.forgotPasswordString), binding.forgotPasswordLinkTextView)
        loginUserByEmailAndPassword()
        onGoogleSignInImageViewClick()
        observeLoginUserWithGoogleState()
        observeLoginState()
        observeLoginValidationEditTextsState()
        navigateToRegisterFragment()
        resetPasswordViaEmail()
        observeResetPasswordState()
    }

    private fun loginUserByEmailAndPassword() {
        binding.apply {
            appButtonLogin.setOnClickListener {
                val email = emailLoginEditText.text.toString().trim()
                val password = passwordLoginEditText.text.toString().trim()
                loginViewModel.loginUserWithEmailAndPassword(email, password)
            }
        }
    }

    private fun getGoogleSingInClient(): GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder()
            .requestIdToken(resources.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(requireActivity(), googleSignInOptions)
    }

    private fun onGoogleSignInImageViewClick() {
        binding.googleSignInImageView.setOnClickListener {
            val signInIntent = getGoogleSingInClient().signInIntent
            startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN)
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onActivityResult(requestCode, resultCode, data)",
        "androidx.fragment.app.Fragment"
    )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            val signInWithGoogleTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val googleSignInAccount = signInWithGoogleTask.result
                loginViewModel.signInWithGoogle(googleSignInAccount.idToken!!)
            } catch (e: Exception) { showToast(requireContext(), binding.root, R.drawable.ic_error_icon, e.message.toString()) }
        }
    }

    private fun observeLoginUserWithGoogleState() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginUserWithGoogle.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect {
                when(it) {
                    is Resource.Success -> {
                        binding.appButtonLogin.revertAnimation()
                        val intent = Intent(requireActivity(), MarketActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(intent)
                    }
                    is Resource.Loading -> binding.appButtonLogin.startAnimation()
                    is Resource.Error -> {
                        binding.appButtonLogin.revertAnimation()
                        showToast(requireContext(), binding.root, R.drawable.ic_error_icon, it.message.toString())
                    }
                    is Resource.Undefined -> Unit
                }
            }
        }
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginUser.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect {
                when (it) {
                    is Resource.Success -> {
                        binding.appButtonLogin.revertAnimation()
                        showToast(requireActivity(), binding.root, R.drawable.ic_done_icon, SUCCESSFULLY_ACCOUNT_LOGIN_TOAST_MESSAGE)
                        val intent = Intent(requireActivity(), MarketActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    is Resource.Error -> {
                        binding.appButtonLogin.revertAnimation()
                        showToast(requireContext(), binding.root, R.drawable.ic_error_icon, it.message.toString())
                    }
                    is Resource.Loading -> binding.appButtonLogin.startAnimation()
                    is Resource.Undefined -> Unit
                }
            }
        }
    }

    private fun observeLoginValidationEditTextsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginValidationState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect {
                if (it.email is ValidationStatus.Error) {
                    withContext(Dispatchers.Main) {
                        binding.emailLoginEditText.apply {
                            requestFocus()
                            error = it.email.errorMessage
                        }
                    }
                }
                if (it.password is ValidationStatus.Error) {
                    withContext(Dispatchers.Main) {
                        binding.passwordLoginEditText.apply {
                            requestFocus()
                            error = it.password.errorMessage
                        }
                    }
                }
            }
        }
    }

    private fun navigateToRegisterFragment() {
        navigateToAnotherFragmentWithoutArguments(binding.registerLinkTextView, R.id.action_loginFragment_to_registerFragment)
    }

    private fun resetPasswordViaEmail() {
        binding.forgotPasswordLinkTextView.setOnClickListener {
            showDialogForResettingPassword(
                requireActivity(),
                getString(R.string.resetPasswordTitleDialogTextViewString),
                getString(R.string.resetPasswordDescriptionDialogTextViewString),
                getString(R.string.appCancelResetPasswordButtonTextString),
                getString(R.string.appSendResetPasswordButtonTextString),
                onPositiveButtonClick = { inputEmailAddress ->
                    loginViewModel.resetPasswordViaEmail(inputEmailAddress)
                }
            )
        }
    }

    private fun observeResetPasswordState() {
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.resetPassword.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED).collect {
                when(it) {
                    is Resource.Success -> showToast(requireActivity(), binding.root, R.drawable.ic_done_icon, getString(R.string.successResetPasswordToastMessageString))
                    is Resource.Error -> showToast(requireContext(), binding.root, R.drawable.ic_error_icon, it.message.toString())
                    is Resource.Loading -> Unit
                    is Resource.Undefined -> Unit
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.appButtonLogin.dispose()
    }
}