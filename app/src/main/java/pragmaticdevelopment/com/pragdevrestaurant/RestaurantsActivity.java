package pragmaticdevelopment.com.pragdevrestaurant;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class RestaurantsActivity extends AppCompatActivity {
    RestaurantListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        setTitle("Restaurants List");
        Button search = (Button)findViewById(R.id.btnSearch);

        final ListView restaurantsList = ((ListView)findViewById(R.id.lvRestaurants));

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchText = (EditText) findViewById(R.id.editSearch);
                DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(RestaurantsActivity.this);
                Cursor restaurants = helper.getRestaurantsBySearchQuery(searchText.getText().toString());
                Restaurant[] objects = helper.cursorToRestaurantArray(restaurants);

                adapter = new RestaurantListViewAdapter(RestaurantsActivity.this, objects);
                restaurantsList.setAdapter(adapter);
            }
        });


        restaurantsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent details = new Intent(RestaurantsActivity.this, DetailActivity.class);
                details.putExtra("restaurantId", adapter.getItem(position).getId());
                startActivity(details);
            }
        });

        DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(RestaurantsActivity.this);
        Cursor restaurants = helper.getAllRestaurants();
        Restaurant[] objects = helper.cursorToRestaurantArray(restaurants);

        adapter = new RestaurantListViewAdapter(RestaurantsActivity.this, objects);
        restaurantsList.setAdapter(adapter);
        registerForContextMenu(restaurantsList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            return false;
        }

        if(item.getItemId() == R.id.delete_restaurant){
            DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(this);
            helper.removeRestaurant(adapter.getItem(info.position).getId());

            EditText searchText = (EditText)findViewById(R.id.editSearch);
            Cursor restaurants = helper.getRestaurantsBySearchQuery(searchText.getText().toString());

            Restaurant[] restaurantObjects = helper.cursorToRestaurantArray(restaurants);

            adapter = new RestaurantListViewAdapter(this, restaurantObjects);
            ((ListView)findViewById(R.id.lvRestaurants)).setAdapter(adapter);
        }
        return true;
    }
}
