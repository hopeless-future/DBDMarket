package com.dbd.market.viewmodels.market.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbd.market.data.PagingInfo
import com.dbd.market.data.Product
import com.dbd.market.repositories.market.categories.suits.SuitsCategoryRepository
import com.dbd.market.utils.Resource
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuitsCategoryViewModel @Inject constructor(private val suitsCategoryRepository: SuitsCategoryRepository): ViewModel() {

    private val _suitsProfitableProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val suitsProfitableProducts = _suitsProfitableProducts.asStateFlow()

    private val _suitsOtherProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val suitsOtherProducts = _suitsOtherProducts.asStateFlow()

    private val suitsOtherProductsPagingInfoState = MutableStateFlow(PagingInfo())

    init {
        getSuitsProfitableProducts()
        getSuitsOtherProducts()
    }

    private fun getSuitsProfitableProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            suitsCategoryRepository.getSuitsProfitableCategoryFromFirebaseFirestore(onSuccess = { suitsProfitableQuerySnapshot ->
                val convertSuitsProfitableQuerySnapshotToSuitsProductObject = suitsProfitableQuerySnapshot.toObjects<Product>()
                _suitsProfitableProducts.emit(Resource.Success(convertSuitsProfitableQuerySnapshotToSuitsProductObject))
            },
            onFailure = { exception ->
                _suitsProfitableProducts.emit(Resource.Error(exception.message.toString()))
            })
        }
    }

    fun getSuitsOtherProducts() {
        if (!suitsOtherProductsPagingInfoState.value.isEndOfPaging) {
            viewModelScope.launch(Dispatchers.IO) {
                suitsCategoryRepository.getSuitsOtherCategoryFromFirebaseFirestore(onSuccess = { suitsOtherQuerySnapshot ->
                    val convertSuitsOtherSnapshotToSuitsProductObject = suitsOtherQuerySnapshot.toObjects<Product>()
                    _suitsOtherProducts.emit(Resource.Success(convertSuitsOtherSnapshotToSuitsProductObject))
                    suitsOtherProductsPagingInfoState.value.isEndOfPaging = convertSuitsOtherSnapshotToSuitsProductObject == suitsOtherProductsPagingInfoState.value.oldProducts
                    suitsOtherProductsPagingInfoState.value.oldProducts = convertSuitsOtherSnapshotToSuitsProductObject
                    suitsOtherProductsPagingInfoState.value.pageNumber++
                },
                onFailure = { exception ->
                    _suitsOtherProducts.emit(Resource.Error(exception.message.toString()))
                },
                pageNumber = suitsOtherProductsPagingInfoState.value.pageNumber)
            }
        }
    }
}