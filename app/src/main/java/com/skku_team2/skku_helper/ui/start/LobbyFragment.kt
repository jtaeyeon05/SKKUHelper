package com.skku_team2.skku_helper.ui.start

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.skku_team2.skku_helper.R
import com.skku_team2.skku_helper.databinding.FragmentLobbyBinding
import com.skku_team2.skku_helper.key.IntentKey
import com.skku_team2.skku_helper.navigation.StartScreen
import com.skku_team2.skku_helper.ui.main.MainActivity
import com.skku_team2.skku_helper.utils.getColorAttr
import kotlinx.coroutines.launch

/**
 * 앱을 실행했을 떄 보여지는 StartActivity 내 Fragment
 */

class LobbyFragment : Fragment() {
    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!

    // StartActivity 단위 ViewModel
    private val startViewModel: StartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Log In Button을 눌렀을 때 처리 (저장된 토큰이 옳으면 MainActivity, 저장된 토큰이 없거나 옳지 않으면 LogInFragment)
        binding.buttonLogin.setOnClickListener {
            if (startViewModel.uiState.value.isTokenVerified == true) {
                val mainActivityIntent = Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(IntentKey.EXTRA_TOKEN, startViewModel.uiState.value.token)
                    putExtra(IntentKey.EXTRA_IS_AUTO_LOGIN, true)
                }
                startActivity(mainActivityIntent)
            } else {
                findNavController().navigate(
                    route = StartScreen.LogIn,
                    NavOptions.Builder()
                        .setEnterAnim(R.anim.fade_in)
                        .setExitAnim(R.anim.fade_out)
                        .build()
                )
            }
        }

        // ViewModel 관측
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // ViewModel 기반 버튼 상태 관리
                startViewModel.uiState.collect { state ->
                    binding.buttonLogin.isEnabled = state.isTokenVerified != null
                    if (state.isTokenVerified != null) {
                        binding.buttonLogin.setBackgroundColor(context?.getColorAttr(com.google.android.material.R.attr.colorPrimaryContainer) ?: Color.BLACK)
                        binding.buttonLogin.setTextColor(context?.getColorAttr(com.google.android.material.R.attr.colorOnPrimaryContainer) ?: Color.WHITE)
                    } else {
                        binding.buttonLogin.setBackgroundColor(context?.getColorAttr(com.google.android.material.R.attr.colorSurfaceContainerHighest) ?: Color.GRAY)
                        binding.buttonLogin.setTextColor(context?.getColorAttr(com.google.android.material.R.attr.colorOnSurfaceVariant) ?: Color.WHITE)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
