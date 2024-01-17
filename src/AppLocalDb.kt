import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Product::class, User::class], version = 13)
@TypeConverters([Convertors::class])
internal abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun productDao(): ProductDao?
    abstract fun userDao(): UserDao?
}

object AppLocalDb {
    var db: AppLocalDbRepository = Room.databaseBuilder(MyApplication.getContext(),
            AppLocalDbRepository::class.java,
            "dbFile.db")
            .fallbackToDestructiveMigration()
            .build()
}
