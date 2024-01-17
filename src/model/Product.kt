import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.util.HashMap
import java.util.Map
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@Getter
@Setter
@NoArgsConstructor
@Entity
class Product(@field:NonNull @field:PrimaryKey var id: String?, var title: String?, var description: String?, var gender: String?,
              var productCondition: String?, var productCategory: String?, var price: String?,
              var contactId: String?, var latitude: Double?, var longitude: Double?, isDeleted: Boolean, isSold: Boolean) {
    var imageUrl: String? = null
    var updateDate = Long(0)
    var isDeleted = false
    var isSold = false

    init {
        this.isDeleted = isDeleted
        this.isSold = isSold
    }

    fun toJson(): Map<String, Object> {
        val json: Map<String, Object> = HashMap<String, Object>()
        json.put("id", id)
        json.put("title", title)
        json.put("description", description)
        json.put("gender", gender)
        json.put("condition", productCondition)
        json.put("productCategory", productCategory)
        json.put("imageUrl", imageUrl)
        json.put("contactId", contactId)
        json.put("longitude", longitude)
        json.put("latitude", latitude)
        json.put("updateDate", FieldValue.serverTimestamp())
        json.put("price", price)
        json.put("isDeleted", isDeleted)
        json.put("isSold", isSold)
        return json
    }

    companion object {
        const val PRODUCTS_COLLECTION_NAME = "products"
        fun create(json: Map<String?, Object?>): Product {
            val id = json["id"] as String?
            val title = json["title"] as String?
            val description = json["description"] as String?
            val price = json["price"] as String?
            val gender = json["gender"] as String?
            val condition = json["condition"] as String?
            val productCategory = json["productCategory"] as String?
            val imageUrl = json["imageUrl"] as String?
            val contactId = json["contactId"] as String?
            val ts: Timestamp? = json["updateDate"] as Timestamp?
            val updateDate: Long = ts.getSeconds()
            val latitude = json["latitude"] as Double?
            val longitude = json["longitude"] as Double?
            val isDeleted = json.containsKey("isDeleted") && json["isDeleted"]
            val isSold = json.containsKey("isSold") && json["isSold"]
            val product = Product(id, title, description, gender, condition,
                    productCategory, price, contactId, latitude, longitude, isDeleted, isSold)
            product.setImageUrl(imageUrl)
            product.setUpdateDate(updateDate)
            return product
        }
    }
}
