package pragmaticdevelopment.com.pragdevrestaurant;

import android.content.Intent;
import android.database.Cursor;
import android.media.Rating;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity{
    private String restoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String name, description;
        String[] tags;

        final long restaurantId = getIntent().getLongExtra("restaurantId", 0);

        DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(this);

        Cursor c = helper.getRestaurantById(restaurantId);
        c.moveToFirst();
        final Restaurant restaurant = helper.cursorToRestaurant(c);
        name = restaurant.getName();
        restoName = name;
        description = restaurant.getDescription();
        final String address = restaurant.getAddress();
        tags = restaurant.getTags(this);

        String tagsText = "";
        if(tags.length > 0){
            tagsText = tags[0];
            for(int i = 1; i < tags.length; i++)
                tagsText += ", " + tags[i];
        }

        ((TextView)findViewById(R.id.txtName)).setText(name);
        ((TextView)findViewById(R.id.txtDescription)).setText(description);
        ((TextView)findViewById(R.id.txtAddress)).setText(address);
        ((TextView)findViewById(R.id.txtTags)).setText(tagsText);

        // Maps Button
        Button map = (Button)findViewById(R.id.btnMap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + restaurant.getAddress());
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);

                Intent maps = new Intent(DetailActivity.this, MapsActivity.class);
                maps.putExtra("address", address);
                maps.putExtra("name", restoName);
                startActivity(maps);

            }
        });

        // Edit Button
        Button edit = (Button) findViewById(R.id.btnEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(DetailActivity.this, AddEditActivity.class);
                edit.putExtra("restaurantId", restaurantId);
                startActivityForResult(edit, 0);
            }
        });

        // Email
        Button email = (Button) findViewById(R.id.btnEmail);
        email.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("message/rfc822");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{((TextView)findViewById(R.id.txtEmail)).getText().toString()});
                email.putExtra(Intent.EXTRA_SUBJECT, "You've got to try this restaurant!");
                email.putExtra(Intent.EXTRA_TEXT, "Name: " + ((TextView)findViewById(R.id.txtName)).getText().toString() + "\n" +
                        "Description: " + ((TextView)findViewById(R.id.txtDescription)).getText().toString() + "\n" +
                        "Address: " + ((TextView)findViewById(R.id.txtAddress)).getText().toString()
                );

                try {
                    startActivity(Intent.createChooser(email, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Ratings
        RatingBar ratingBar = (RatingBar) findViewById(R.id.barRating);

        // Display current rating
        ratingBar.setRating(restaurant.getRating(getApplicationContext()));

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                restaurant.setRating(getApplicationContext(), ratingBar.getRating());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
        startActivity(getIntent());
    }
}
