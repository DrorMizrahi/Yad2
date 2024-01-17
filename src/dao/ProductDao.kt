import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.List

@Dao
interface ProductDao {
    @get:Query("select * from Product where isDeleted=0 and isSold=0 order by updateDate desc")
    val all: List<Any?>?

    @Query("select * from Product where contactId=:id and isDeleted=0 order by updateDate desc")
    fun getProductsByContactId(id: String?): List<Product?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg products: Product?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(products: List<Product?>?)

    @Delete
    fun delete(product: Product?)
}
