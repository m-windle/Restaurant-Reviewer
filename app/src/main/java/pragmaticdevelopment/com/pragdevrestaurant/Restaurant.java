package pragmaticdevelopment.com.pragdevrestaurant;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Admin on 12/7/2015.
 */
public class Restaurant {
    private long id;
    private String name;
    private String address;
    private String description;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Restaurant(long id, String name, String address, String description){
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
    }

    public String[] getTags(Context context){
        DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(context);
        return helper.getRestaurantTags(id);
    }

    public boolean addTag(Context context, String tagName){
        DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(context);
        return helper.addRestaurantTag(id, tagName);
    }

    public boolean removeTag(Context context, String tagName){
        DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(context);
        return helper.removeRestaurantTag(id, tagName);
    }

    public boolean update(Context context, String name, String address, String description){
        DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(context);
        boolean result = helper.updateRestaurant(this.id, name, address, description);

        if(result){
            this.name = name;
            this.address = address;
            this.description = description;
        }

        return result;
    }

    public void setRating(Context context, float rating){
        SharedPreferences.Editor editor = context.getSharedPreferences("restaurant_ratings", Context.MODE_PRIVATE).edit();
        editor.putFloat("rating_" + id, rating);
        editor.apply();
        editor.commit();
    }

    public float getRating(Context context){
        SharedPreferences preferences = context.getSharedPreferences("restaurant_ratings", Context.MODE_PRIVATE);
        float rating = preferences.getFloat("rating_" + id, 0);
        return rating;
    }
}
