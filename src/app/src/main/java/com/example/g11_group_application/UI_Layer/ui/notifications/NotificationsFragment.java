package com.example.g11_group_application.UI_Layer.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.g11_group_application.R;
import com.example.g11_group_application.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private PopupWindow popupWindow;
    private ArrayAdapter<String> adapter;
    private String[] filterOptions = {"Option 1", "Option 2", "Option 3"}; // replace with your filter options

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.searchBar;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        // Access the search bar and search button
        EditText searchBar = root.findViewById(R.id.search_bar);
        ImageButton searchButton = root.findViewById(R.id.search_button);
        Button filterButton = root.findViewById(R.id.filter_button); // assuming you have a filter button

        // Create the adapter for the ListView in the PopupWindow
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, filterOptions);

        // Add functionality to the filter button
        filterButton.setOnClickListener(v -> {
            showPopupWindow(filterButton);
        });

        // Add functionality to the search button
        searchButton.setOnClickListener(v -> {
            String searchText = searchBar.getText().toString();

            // Perform search using searchText
        });
        return root;
    }
    private void showPopupWindow(View view) {
        // Create a ListView
        ListView listView = new ListView(getActivity());
        listView.setAdapter(adapter);

        // Create a PopupWindow
        popupWindow = new PopupWindow(listView, view.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set an OnItemClickListener for the ListView
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            // Perform action when an item is clicked
            String selectedItem = filterOptions[position];
            popupWindow.dismiss();
        });

        // Show the PopupWindow
        popupWindow.showAsDropDown(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}