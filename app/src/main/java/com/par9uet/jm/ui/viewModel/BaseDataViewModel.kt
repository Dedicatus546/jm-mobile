package com.par9uet.jm.ui.viewModel

import androidx.lifecycle.ViewModel

open class BaseDataViewModel : ViewModel() {

//    fun <T, E> executeApi(api: () -> NetWorkResult<T>, uiState: UIState<E>) {
//        viewModelScope.launch {
//            when (val data = withContext(Dispatchers.IO) {
//                api()
//            }) {
//                is NetWorkResult.Error<*> -> {
//                    Log.v("api", data.message)
//                    comicDetailUIStatus = UIState.LoadFailure(data.message)
//                }
//
//                is NetWorkResult.Success<ComicDetailResponse> -> {
//                    Log.v("api", data.data.toString())
//                    comicDetailUIStatus = UIState.LoadComplete(data.data.toComic())
//                }
//            }
//        }
//    }
}