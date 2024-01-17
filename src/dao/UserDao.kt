import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.List

@Dao
interface UserDao { /*    @Query("select * from User where id=:id")
    List<Product> getUserById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);

    @Insert
    public void insertProducts(List<User> users);

    @Delete
    void delete(Product product);*/
}
