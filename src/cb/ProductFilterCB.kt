package com.example.old2gold.model

import com.example.old2gold.enums.ProductCategory
import java.util.ArrayList
import java.util.List

class ProductFilterCB private constructor(var productType: String) {
    fun setProductTypes(productType: String) {
        this.productType = productType
    }

    var isFlag = false

    companion object {
        val allCheckboxTypes: List<ProductFilterCB>
            get() {
                val productSizeFilterCBList: List<ProductFilterCB> = ArrayList()
                val productTypes: Array<ProductCategory> = ProductCategory.values()
                for (i in productTypes.indices) {
                    productSizeFilterCBList.add(i, ProductFilterCB(productTypes[i].toString()))
                }
                return productSizeFilterCBList
            }
    }
}