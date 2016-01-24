package pragmaticdevelopment.com.pragdevrestaurant;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddEditActivity extends AppCompatActivity {
    private long restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        restaurantId = getIntent().getLongExtra("restaurantId", 0);

        final EditText etName = (EditText)findViewById(R.id.editRestName);
        final EditText etAddress = (EditText)findViewById(R.id.editRestAddress);
        final EditText etDescription = (EditText)findViewById(R.id.editRestDescription);
        final EditText etTags = (EditText)findViewById(R.id.editRestTags);

        if(restaurantId != 0) {
            DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(AddEditActivity.this);
            Cursor c = helper.getRestaurantById(restaurantId);
            c.moveToFirst();
            Restaurant current = helper.cursorToRestaurant(c);

            etName.setText(current.getName());
            etAddress.setText(current.getAddress());
            etDescription.setText(current.getDescription());

            String[] tags = current.getTags(this);
            String tagString = "";

            if(tags.length > 0){
                tagString = tags[0];
                for(int i = 1; i < tags.length; i++)
                    tagString += "," + tags[i];
            }

            etTags.setText(tagString);

            ((Button)findViewById(R.id.btnAction)).setText("Edit Restaurant");
        }
        ((Button)findViewById(R.id.btnCancelAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((Button)findViewById(R.id.btnAction)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String address = etAddress.getText().toString();
                String description = etDescription.getText().toString();
                String[] tags = etTags.getText().toString().split(",");

                if(name.equals("") || address.equals(""))
                {
                    if(name.equals(""))
                        etName.setError("Name cannot be empty.");

                    if(address.equals(""))
                        etAddress.setError("Address cannot be empty.");

                    return;
                }

                DatabaseContract.DBHelper helper = new DatabaseContract.DBHelper(AddEditActivity.this);
                if(restaurantId == 0)
                {
                    long id = helper.addRestaurant(name, address, description);

                    for(String i : tags)
                        if(i.trim().length() > 0) helper.addRestaurantTag(id, i.trim());

                    finish();
                    Toast.makeText(AddEditActivity.this, "Restaurant successfully added!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    helper.updateRestaurant(restaurantId, name, address, description);
                    for(String i : helper.getRestaurantTags(restaurantId))
                        helper.removeRestaurantTag(restaurantId, i);

                    for(String i : tags)
                        if(i.trim().length() > 0) helper.addRestaurantTag(restaurantId, i.trim());

                    finish();
                    Toast.makeText(AddEditActivity.this, "Restaurant successfully edited!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
