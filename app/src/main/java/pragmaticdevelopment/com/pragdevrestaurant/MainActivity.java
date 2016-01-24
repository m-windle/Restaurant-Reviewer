package pragmaticdevelopment.com.pragdevrestaurant;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnRestaurantList)
            startActivity(new Intent(MainActivity.this, RestaurantsActivity.class));
        else if(v.getId() == R.id.btnAddRestaurant)
            startActivity(new Intent(MainActivity.this, AddEditActivity.class));
        else if (v.getId() == R.id.btnAbout)
            startActivity(new Intent(MainActivity.this, About.class));
    }
}
