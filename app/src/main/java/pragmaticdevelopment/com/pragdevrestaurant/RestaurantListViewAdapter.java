package pragmaticdevelopment.com.pragdevrestaurant;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RestaurantListViewAdapter extends ArrayAdapter<Restaurant> {
    public RestaurantListViewAdapter(Context context, Restaurant[] objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Restaurant current = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_restaurants, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.restaurantName);
        TextView description = (TextView) convertView.findViewById(R.id.restaurantDescription);
        TextView tags = (TextView) convertView.findViewById(R.id.restaurantTags);

        String[] allTags = current.getTags(getContext());
        String tagString = "";

        if (allTags.length > 3) {
            tagString = allTags[0];
            for (int i = 1; i < 3; i++) {
                tagString += ", " + allTags[i];
            }
            tagString += " and " + (allTags.length - 3) + " more...";
        } else if (allTags.length == 0)
            tagString = "This restaurant has no tags";
        else {
            tagString = allTags[0];
            for (int i = 1; i < allTags.length; i++) {
                tagString += ", " + allTags[i];
            }
        }

        name.setText(current.getName());
        description.setText(current.getDescription());
        tags.setText(tagString);

        return convertView;
    }
}
