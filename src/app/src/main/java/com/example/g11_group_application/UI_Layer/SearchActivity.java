package com.example.g11_group_application.UI_Layer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.g11_group_application.R;
import com.example.g11_group_application.firebase_connection_DAO.*;
import com.example.g11_group_application.Service_layer.SearchParser;
import com.example.g11_group_application.Service_layer.SearchTokenizer;
import com.example.g11_group_application.Service_layer.User;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
/**
 * @Author: Aditya Iyengar (u7670692)
 * @Edited: Saksham Gupta(u7726995), Omair Soomro (u7517790) and Divyesh Srivastava (u7726856)
 * Created: 06-May-2024
 * Comments: This is the Search Activity Java class. Handles the search functionality within the
 * application, allowing users to search for other users, apply filters, and view search results.
 */
public class SearchActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private PopupWindow filterPopup;
    private PopupWindow threeDotPopup;
    private PopupWindow listPopup;
    MainActivity mainActivity = new MainActivity();
    firebase_service_layer serviceLayer = new firebase_service_layer();
    List<User> filteredList;
    List<User> filteredListCopy;
    List<String> filteredUserNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Initializing UI components
        ImageButton homeButton = findViewById(R.id.button_home);
        ImageButton dashboardButton = findViewById(R.id.button_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MyApplication), (v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return WindowInsetsCompat.CONSUMED;
        });

        // Setting up onClick listeners for home and dashboard buttons
        dashboardButton.setOnClickListener(v -> {
            Intent getintent = getIntent();
            String userID = getintent.getStringExtra("username");
            Intent intent = new Intent(this, AppMainPage.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        });
        homeButton.setOnClickListener(v -> {
            Intent getintent = getIntent();
            String userID = getintent.getStringExtra("username");
            Intent intent = new Intent(this, HomePage.class);
            intent.putExtra("username", userID);
            startActivity(intent);
        });

        Button filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this::showPopup);
        ImageButton threeDotButton = findViewById(R.id.more_button);
        ListView filterList = findViewById(R.id.filtered_list);
        filterList.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // Set the choice mode
        filterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Perform the same action as the threeDotButton
                showPopupListOptions(view);
            }
        });
        threeDotButton.setOnClickListener(this::showPopupThreeDot);
        EditText searchBar = findViewById(R.id.search_bar);
        ImageButton searchButton = findViewById(R.id.search_button_image);

        try {
            Log.d("Search Activity AVLTree: ", mainActivity.userAVLTreeMain.display());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Initializing the user lists
        filteredList = (ArrayList<User>) mainActivity.userAVLTreeMain.getUsers();
        for(User user:filteredList){
            user.saveOrUpdateUser();
        }
        filteredListCopy = filteredList;
        User.displayUsers(filteredList);
        filteredUserNames = convertToUsername((ArrayList<User>) filteredList);

        // Setting up the adapter for the filtered user list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredUserNames);
        filterList.setAdapter(adapter);

        // Setting up the search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchBar.getText().toString();
                System.out.println("Search Text: " + searchText);
                if (searchText.equals(" ") || searchText.isEmpty()){
                    searchBar.setError("Search field cannot be blank");
                    return;
                }
                else if(!searchText.matches("(\\s*(?i)(from:|to:)\\s*(\\d{4}-\\d{2}-\\d{2})){0,2}|([a-zA-Z]+(\\s+[a-zA-Z]+){0,2})(?![^\\s:]*:)")){
                    searchBar.setError("Invalid search entry");
                    return;
                }
                SearchTokenizer tokenizer = new SearchTokenizer(searchBar.getText().toString());
                SearchParser parser = new SearchParser(tokenizer.getTokens());
                filteredList = parser.parse().execute(filteredList);
                System.out.println("Filtered: ");
                User.displayUsers(filteredList);
                filteredUserNames.removeAll(filteredUserNames);
                filteredUserNames.addAll(convertToUsername((ArrayList<User>) filteredList));
                adapter.notifyDataSetChanged();  // Refresh the ListView
                searchBar.setText("");  // Clear the input field
                filteredListCopy = filteredList;
                filteredList = (ArrayList<User>) mainActivity.userAVLTreeMain.getUsers();
            }
        });
    }

    /**
     * Shows the filter popup window.
     * @param anchor The view to anchor the popup window to.
     */
    private void showPopup(View anchor) {
        if (filterPopup == null) {
            View popupView = LayoutInflater.from(this).inflate(R.layout.list_view_popup_filter, null);
            ListView listView = popupView.findViewById(R.id.list_view_popup);
            EditText editText = findViewById(R.id.search_bar);  // Make sure to replace 'your_edit_text_id' with the actual ID of your EditText
            String[] items = {"From: ", "To: "}; // Your filter options

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adapter);

            filterPopup = new PopupWindow(popupView,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            filterPopup.setOutsideTouchable(true);
            filterPopup.setFocusable(true); // to get focus, and dismiss when clicked outside

            // Dismiss when clicked outside
            filterPopup.setTouchInterceptor((v, event) -> {
                if (!v.equals(listView)) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        filterPopup.dismiss();
                        return true;
                    }
                }
                return false;
            });

            // Set an item click listener for the listView
            listView.setOnItemClickListener((parent, view, position, id) -> {
                String selectedItem = adapter.getItem(position);  // Get the clicked item
                int start = editText.getSelectionStart();  // Get the cursor starting position
                int end = editText.getSelectionEnd();  // Get the cursor ending position

                // Build the new text by inserting the selected item at the cursor's position
                Editable text = editText.getText();
                if (start < 0 || start >= text.length()) {
                    text.append(" " + selectedItem );
                } else {
                    text.replace(Math.min(start, end), Math.max(start, end), selectedItem);
                }

                // Set the cursor just after the inserted text
                editText.setSelection(start + selectedItem.length() + 1);

                filterPopup.dismiss();  // Optionally dismiss the popup after an item is clicked
            });
        }

        filterPopup.showAsDropDown(anchor, 0, 0);
    }

    /**
     * Shows the three-dot popup window.
     * @param anchor The view to anchor the popup window to.
     */
    private void showPopupThreeDot(View anchor) {
        Intent get_intent = getIntent();
        String username = get_intent.getStringExtra("username");
        if (threeDotPopup == null) {
            View popupView = LayoutInflater.from(this).inflate(R.layout.list_view_popup_filter, null);
            ListView listView = popupView.findViewById(R.id.list_view_popup);
            String[] items = {"Log Out", "Profile Page"}; // Options for the popup
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adapter);

            threeDotPopup = new PopupWindow(popupView,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            threeDotPopup.setOutsideTouchable(true);
            threeDotPopup.setFocusable(true);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (position == 0) {
                    new AlertDialog.Builder(this)
                            .setTitle("Log out")
                            .setMessage("Are you sure you want to log out?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // User wants to log out
                                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    threeDotPopup.dismiss();
                } else if (position == 1) {
                    Intent intent = new Intent(this, ProfilePage.class);
                    intent.putExtra("username", username);// Change as per your logout handling
                    startActivity(intent);
                    threeDotPopup.dismiss();
                }
            });

            // Dismiss the popup when clicked outside
            threeDotPopup.setTouchInterceptor((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    threeDotPopup.dismiss();
                    return true;
                }
                return false;
            });
        }

        threeDotPopup.showAsDropDown(anchor, 0, 0);
    }

    /**
     * Shows the list popup options.
     * @param anchor The view to anchor the popup window to.
     */
    private void showPopupListOptions(View anchor) {
        Intent get_intent = getIntent();
        String username = get_intent.getStringExtra("username");
        if (listPopup == null) {

            View popupView = LayoutInflater.from(this).inflate(R.layout.list_view_popup_filter, null);
            ListView listView = popupView.findViewById(R.id.list_view_popup);
            String[] items = {"Chat"}; // Options for the popup
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adapter);

            listPopup = new PopupWindow(popupView,
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            listPopup.setOutsideTouchable(true);
            listPopup.setFocusable(true);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (position == 0) {
                    ListView temp = findViewById(R.id.filtered_list);

                    int selectedUserPosition = ((ListView) findViewById(R.id.filtered_list)).getCheckedItemPosition();
                    System.out.println("Selected User Position: " + selectedUserPosition);
                    if (selectedUserPosition != -1) {
                        for(User user: filteredListCopy){
                            System.out.println(user.getUserName());
                        }
                        String selectedUserName = filteredUserNames.get(selectedUserPosition);
                        User selectedUser = filteredListCopy.get(selectedUserPosition);
                        System.out.println("Selected User: " + selectedUser.getId());

                        //If the current user is the same as the selected user
                        if (selectedUser.getId().equals(username)) {
                            Toast.makeText(this, "Can't chat with yourself. Try again", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(this, ChatRoom.class);
                            intent.putExtra("username", username);
                            intent.putExtra("usernameOfChat", selectedUserName);
                            intent.putExtra("ChatUser",selectedUser);
                            startActivity(intent);
                            listPopup.dismiss();
                        }

                    }
                }
            });

            // Dismiss the popup when clicked outside
            listPopup.setTouchInterceptor((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    listPopup.dismiss();
                    return true;
                }
                return false;
            });
        }

        listPopup.showAsDropDown(anchor, 0, 0);
    }

    /**
     * Converts a list of User objects to a list of usernames.
     * @param userList The list of User objects.
     * @return The list of usernames.
     */
    private ArrayList<String> convertToUsername(ArrayList<User> userList){
        ArrayList<String> nameList = new ArrayList<>();
        for(User user: userList){
            String name = user.getFirstName();
            if(user.getMiddleName()!=null || (user.getMiddleName().equals(" "))){
                name = name + " " + user.getMiddleName();
            }
            if(user.getLastName()!=null){
                name = name + " " + user.getLastName();
            }
            nameList.add(name);
        }
        return nameList;
    }
}
