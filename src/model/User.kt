import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.Map
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
class User(var firstName: String?, var lastName: String?, var email: String?, var phoneNumber: String?, var address: String?, favoriteProducts: ArrayList<String?>) : Serializable {
    @NonNull
    @PrimaryKey
    var id: String? = null
    var userImageUrl: String? = null
    var favoriteProducts: ArrayList<String>

    init {
        this.favoriteProducts = favoriteProducts
    }

    fun toJson(): Map<String, Object> {
        val json: Map<String, Object> = HashMap<String, Object>()
        json.put("firstName", firstName)
        json.put("lastName", lastName)
        json.put("email", email)
        json.put("phoneNumber", phoneNumber)
        json.put("address", address)
        json.put("userImageUrl", userImageUrl)
        json.put("favoriteProducts", Collections.emptyList())
        return json
    }

    companion object {
        const val COLLECTION_NAME = "users"
        fun create(json: Map<String?, Object?>): User {
            val currentUser = JSONObject(json)
            val id = json["id"] as String?
            val firstName = json["firstName"] as String?
            val lastName = json["lastName"] as String?
            val address = json["address"] as String?
            val email = json["email"] as String?
            val phoneNumber = json["phoneNumber"] as String?
            val userImageUrl = json["userImageUrl"] as String?
            val favoriteProducts: ArrayList<String> = ArrayList()
            try {
                val userFavoriteProducts: JSONArray = currentUser.getJSONArray("favoriteProducts")
                for (index in 0 until userFavoriteProducts.length()) {
                    favoriteProducts.add(userFavoriteProducts.get(index) as String)
                }
            } catch (e: JSONException) {
                Log.d("error", "failed getting user favorite product")
            }
            return User(id, firstName, lastName, email, phoneNumber, address, userImageUrl, favoriteProducts)
        }
    }
}
