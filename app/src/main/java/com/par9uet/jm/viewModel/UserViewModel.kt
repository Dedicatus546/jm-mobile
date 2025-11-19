package com.par9uet.jm.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.par9uet.jm.data.models.LoginResponseData
import com.par9uet.jm.retrofit.model.LikeComicResponse
import com.par9uet.jm.retrofit.model.LoginResponse
import com.par9uet.jm.retrofit.model.NetWorkResult
import com.par9uet.jm.retrofit.repository.GlobalRepository
import com.par9uet.jm.retrofit.repository.UserRepository
import com.par9uet.jm.utils.createUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

class UserViewModel(
    private val userRepository: UserRepository,
    private val globalRepository: GlobalRepository
) : ViewModel() {

    fun login(username: String, password: String) {
        viewModelScope.launch {
            globalRepository.userLoading = true
            when (val data = withContext(Dispatchers.IO) {
                userRepository.login(username, password)
            }) {
                is NetWorkResult.Error<*> -> {
                    Log.v("api", data.message)
                }

                is NetWorkResult.Loading<*> -> {
                    Log.v("api", "loading")
                }

                is NetWorkResult.Success<LoginResponse> -> {
                    globalRepository.user = globalRepository.user.copy(
                        id = data.data.uid,
                        username = data.data.username,
                        avatar = data.data.photo,
                        level = data.data.level,
                        levelName = data.data.level_name,
                        currentLevelExp = data.data.exp,
                        nextLevelExp = data.data.nextLevelExp,
                        currentCollectCount = data.data.album_favorites,
                        maxCollectCount = data.data.album_favorites_max,
                        jCoin = data.data.coin.toInt(),
                    )
                }
            }

            globalRepository.userLoading = false
        }
    }
}