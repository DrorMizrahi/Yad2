import com.example.old2gold.MyApplication.getContext
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.old2gold.MyApplication
import com.google.firebase.auth.FirebaseAuth
import java.util.ArrayList
import java.util.Comparator
import java.util.List
import java.util.Optional
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.stream.Collectors

class Model private constructor() {
    var executor: Executor = Executors.newFixedThreadPool(1)
    var modelFirebase: ModelFirebase = ModelFirebase()
    var productsList: MutableLiveData<List<Product>> = MutableLiveData<List<Product>>()
    var favoriteProductsByUserList: MutableLiveData<List<Product>> = MutableLiveData<List<Product>>()
    var productsByUserList: MutableLiveData<List<Product>> = MutableLiveData<List<Product>>()
    var productListLoadingState: MutableLiveData<ProductsListLoadingState> = MutableLiveData<ProductsListLoadingState>()
    var userProductsLoadingState: MutableLiveData<ProductsListLoadingState> = MutableLiveData<ProductsListLoadingState>()
    var favoriteProductsLoadingState: MutableLiveData<ProductsListLoadingState> = MutableLiveData<ProductsListLoadingState>()
    var categoriesFilterList: MutableLiveData<List<String>> = MutableLiveData()
    var loggedUser: MutableLiveData<User> = MutableLiveData<User>()
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()

    enum class ProductsListLoadingState {
        loading,
        loaded
    }

    interface AddProductListener {
        fun onComplete()
    }

    interface GetLoggedUserListener {
        fun onComplete(user: User?)
    }

    interface UpdateDataListener {
        fun onComplete()
    }

    interface SaveImageListener {
        fun onComplete(url: String?)
    }

    fun getProductListLoadingState(): LiveData<ProductsListLoadingState> {
        return productListLoadingState
    }

    fun getUserProductsLoadingState(): LiveData<ProductsListLoadingState> {
        return userProductsLoadingState
    }

    val favoritesProductsLoadingState: LiveData<ProductsListLoadingState>
        get() = favoriteProductsLoadingState

    init {
        productListLoadingState.setValue(ProductsListLoadingState.loaded)
        userProductsLoadingState.setValue(ProductsListLoadingState.loaded)
        favoriteProductsLoadingState.setValue(ProductsListLoadingState.loaded)
    }

    val all: LiveData<List<Product>>
        get() {
            if (productsList.getValue() == null) {
                categoriesFilterList.postValue(ArrayList())
                refreshProductsList()
            }
            return productsList
        }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getLoggedUser(): LiveData<User> {
        if (loggedUser.getValue() == null) {
            refreshLoggedUser()
        }
        return loggedUser
    }

    @get:RequiresApi(api = Build.VERSION_CODES.N)
    val allFavoriteProductsByUser: LiveData<List<Product>>
        get() {
            if (favoriteProductsByUserList.getValue() == null) {
                refreshProductsILikedByUserList()
            }
            return favoriteProductsByUserList
        }

    @get:RequiresApi(api = Build.VERSION_CODES.N)
    val productOfUser: LiveData<List<Product>>
        get() {
            if (productsByUserList.getValue() == null) {
                refreshProductsByMyUser()
            }
            return productsByUserList
        }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getProductListByTypeFilter(selectedCategories: List<String?>?) {
        categoriesFilterList.postValue(selectedCategories)
        refreshProductsList()
    }

    fun isLoggedUser(product: Product): Boolean {
        val loggedUserId: String? = if (mAuth != null) mAuth.getUid() else null
        return product.contactId != null && product.contactId.equals(loggedUserId)
    }

    fun isInFilters(product: Product): Boolean {
        val categories: List<String> = if (categoriesFilterList.getValue() != null) categoriesFilterList.getValue() else ArrayList()
        return categories.contains(product.productCategory) || categories.isEmpty()
    }

    fun refreshProductsList() {
        productListLoadingState.setValue(ProductsListLoadingState.loading)
        val lastUpdateDate: Long = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE).getLong("lastUpdate", 0)
        modelFirebase.getAllProducts(lastUpdateDate) { allProducts ->
            executor.execute(object : Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                fun run() {
                    var lud = Long(0)
                    Log.d("TAG", "fb returned " + allProducts.size())
                    lud = getProductsLastUpdateDate(lud, allProducts)
                    updateLastLocalUpdateDate(lud)
                    val productList: List<Product> = AppLocalDb.db.productDao().getAll()
                            .stream().filter { product -> !isLoggedUser(product) && isInFilters(product) }
                            .collect(Collectors.toList())
                    productsList.postValue(productList)
                    productListLoadingState.postValue(ProductsListLoadingState.loaded)
                }
            })
        }
    }

    private fun getProductsLastUpdateDate(lud: Long, allProducts: List<Product>): Long {
        var lud = lud
        for (product in allProducts) {
            AppLocalDb.db.productDao().insertAll(product)
            if (lud < product.getUpdateDate()) {
                lud = product.getUpdateDate()
            }
        }
        return lud
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun refreshProductsILikedByUserList() {
        favoriteProductsLoadingState.setValue(ProductsListLoadingState.loading)
        modelFirebase.getAllLikedProductsByUser(mAuth.getUid(), object : GetLikedProductsListener() {
            @Override
            fun onComplete(products: List<Product?>) {
                executor.execute(object : Runnable() {
                    @Override
                    fun run() {
                        val lud = Long(0)
                        Log.d("TAG", "fb returned " + products.size())
                        updateLastLocalUpdateDate(lud)
                        favoriteProductsByUserList.postValue(products)
                        favoriteProductsLoadingState.postValue(ProductsListLoadingState.loaded)
                    }
                })
            }
        })
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun refreshProductsByMyUser() {
        userProductsLoadingState.setValue(ProductsListLoadingState.loading)
        val id: String = mAuth.getUid()
        modelFirebase.getProductsByUser(id) { products ->
            executor.execute(object : Runnable() {
                @Override
                fun run() {
                    val lastUpdateDate = Long(0)
                    Log.d("TAG", "fb returned " + products.size())
                    updateLastLocalUpdateDate(getProductsLastUpdateDate(lastUpdateDate, products))
                    val productList: List<Product> = AppLocalDb.db.productDao().getProductsByContactId(id)
                    productsByUserList.postValue(productList)
                    userProductsLoadingState.postValue(ProductsListLoadingState.loaded)
                }
            })
        }
    }

    private fun updateLastLocalUpdateDate(lastUpdateDate: Long) {
        getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .edit()
                .putLong("updateDate", lastUpdateDate)
                .commit()
    }

    fun saveProduct(product: Product?, listener: AddProductListener) {
        modelFirebase.saveProduct(product) {
            listener.onComplete()
            refreshProductsList()
        }
    }

    fun saveProductImage(imageBitmap: Bitmap?, imageName: String?, listener: SaveImageListener?) {
        modelFirebase.saveImage(imageBitmap, imageName, listener, "products")
    }

    fun saveUserImage(imageBitmap: Bitmap?, imageName: String?, listener: SaveImageListener?) {
        modelFirebase.saveImage(imageBitmap, imageName, listener, "users")
    }

    fun saveUser(user: User?, id: String?) {
        modelFirebase.addUser(user, id)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun updateUser(user: User?, listener: UpdateDataListener?) {
        modelFirebase.updateUser(user, listener)
        refreshLoggedUser()
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getUser(id: String?, optionalListener: GetLoggedUserListener?): User {
        return modelFirebase.getUser(id, optionalListener)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun refreshLoggedUser(): User {
        return modelFirebase.getUserO(mAuth.getUid()) { user -> loggedUser.postValue(user) }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getProductSellerUser(id: String?, optionalListener: GetLoggedUserListener?): User {
        return modelFirebase.getProductSellerUser(id, optionalListener)
    }

    interface GetProductById {
        fun onComplete(product: Product?)
    }

    fun getProductById(productId: String?, listener: GetProductById?): Product? {
        modelFirebase.getProductById(productId, listener)
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun removeFromLikedProducts(productId: String?, listener: ModelFirebase.RemoveLikedProductsListener) {
        modelFirebase.removeFromFavoriteList(productId) { listener.onComplete() }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun addToLikedProducts(productId: String?, likedProductListener: ModelFirebase.AddLikedProductListener) {
        modelFirebase.addToLikedProducts(productId) { likedProductListener.onComplete() }
    }

    companion object {
        val instance = Model()
    }
}
