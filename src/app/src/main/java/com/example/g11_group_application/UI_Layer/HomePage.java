package com.example.g11_group_application.UI_Layer;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.g11_group_application.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.example.g11_group_application.firebase_connection_DAO.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
/**
 * @Author: Divyesh Srivastava (u7726856)
 * @Edited: Aditya Iyengar (u7670692), Saksham Gupta (u7726995) and Omair Soomro (u7517790)
 * Created: 09-May-2024
 * Comments: This is the HomePage class that displays the data in a table and a chart.
 */

public class HomePage extends AppCompatActivity {
    String TAG = "HomePage";
    private RealTimeFirebaseOperations firebaseOperations;
    private DataStream dataStream;
    ConstraintLayout container;
    private int currentPage = 0;
    private int itemsPerPage = 20;
    private List<SuicidePreventionData> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ImageButton homeButton = findViewById(R.id.button_home);
        ImageButton dashboardButton = findViewById(R.id.button_dashboard);
        ImageButton searchButton = findViewById(R.id.button_search);

//        TextView textView = findViewById(R.id.textView);
        // Initialize FirebaseOperations
        firebaseOperations = new RealTimeFirebaseOperations();
        // Read the data from the CSV file
        dataList = new ArrayList<>();

        AssetManager manager = this.getApplicationContext().getAssets();
        InputStream is = null;
        try {
            is = manager.open(firebase_filenames.datastream_data_csv.getComponent_name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int srNo = 1;
            // Read the header to determine the column indices
            line = br.readLine();
            String[] headers = line.split(",");
            int ageIndex = -1, sexIndex = -1, remotenessIndex = -1, totalIndex = -1, austIndex = -1, description1Index = -1;

            // Find the indices of required columns
            for (int i = 0; i < headers.length; i++) {
                switch (headers[i].trim()) {
                    case "Age":
                        ageIndex = i;
                        break;
                    case "Sex":
                        sexIndex = i;
                        break;
                    case "Remoteness":
                        remotenessIndex = i;
                        break;
                    case "Total":
                        totalIndex = i;
                        break;
                    case "Aust":
                        austIndex = i;
                        break;
                    case "Description1":
                        description1Index = i;
                        break;
                }
            }
            // Process each line of the file
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String age = values[ageIndex].trim();
                String sex = values[sexIndex].trim();
                String remoteness = values[remotenessIndex].trim();
                String totalValue = "";
                try {
                    totalValue = values[totalIndex].trim().isEmpty() ? values[austIndex].trim() : values[totalIndex].trim();
                }catch (IndexOutOfBoundsException e) {
                    totalValue = "0";
                }

                String description1 = values[description1Index].trim();
                // Add data to the list
                dataList.add(new SuicidePreventionData(String.valueOf(srNo), Integer.parseInt(age), sex, remoteness, totalValue, description1));
                srNo++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        firebaseOperations.uploadDataList(dataList);

        dashboardButton.setOnClickListener(v -> {
            Intent getintent = getIntent();
            String userID = getintent.getStringExtra("username");
            Intent intent = new Intent(this, AppMainPage.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        });
        searchButton.setOnClickListener(v -> {
            Intent getintent = getIntent();
            String userID = getintent.getStringExtra("username");
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("username", userID);
            startActivity(intent);
        });



        // Initialize and start DataRefresher
        dataStream = new DataStream(firebaseOperations, 1, TimeUnit.SECONDS);
        dataStream.start(new RealTimeFirebaseOperations.UserDataListCallback() {
            @Override
            public void onUserDataListReceived(List<SuicidePreventionData> userDataList) {
                dataList.clear();
                dataList.addAll(userDataList);

                // Update the table with the new data
                populateTable(dataList);
            }

            @Override
            public void onError(Exception e) {
                handleError(e);
            }
        });
        TableLayout tableLayoutHeader = findViewById(R.id.tableLayoutHeader);
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("ID"));
        headerRow.addView(createTextView("Average Age"));
        headerRow.addView(createTextView("Gender"));
        headerRow.addView(createTextView("Region"));
        headerRow.addView(createTextView("Average rate of Events"));
        headerRow.addView(createTextView("Event Description"));
        tableLayoutHeader.addView(headerRow);
    }

    private void populateChart(List<SuicidePreventionData> dataList) {
        LineChartView chart = findViewById(R.id.chart);

        List<PointValue> values = new ArrayList<>();
        for (SuicidePreventionData data : dataList) {
            // Convert the individual ID to a float
            float idAsFloat = Float.parseFloat(data.getIndividualId());
            values.add(new PointValue(idAsFloat, data.getAge()));
        }

        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        // Create and set axes
        Axis xAxis = new Axis();
        xAxis.setMaxLabelChars(5);
        xAxis.setTextColor(Color.BLACK); // Set the color of the axis labels
        xAxis.setTextSize(12); // Set the text size of the axis labels

        Axis yAxis = new Axis().setHasLines(true);
        yAxis.setTextColor(Color.BLACK); // Set the color of the axis labels
        yAxis.setTextSize(12); // Set the text size of the axis labels

        // Set the y-axis to have a range from 0 to 100 with a skip of 5
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i <= 100; i += 5) {
            axisValues.add(new AxisValue(i));
        }
        yAxis.setValues(axisValues);

        xAxis.setName("Average Value");
        yAxis.setName("Average Age");
        data.setAxisXBottom(xAxis);
        data.setAxisYLeft(yAxis);

        chart.setLineChartData(data);
    }

    private void populateTable(List<SuicidePreventionData> dataList) {
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // Clear the table before populating it
        tableLayout.removeAllViews();


        // Calculate the start and end item indexes for the current page
        int startItem = currentPage * itemsPerPage;
        int endItem = Math.min(startItem + itemsPerPage, dataList.size());

        // Create a sublist for the current page
        List<SuicidePreventionData> currentPageDataList = dataList.subList(startItem, endItem);

        // Add a row for each SuicidePreventionData object in the current page
        for (SuicidePreventionData data : currentPageDataList) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(String.valueOf(data.getIndividualId())));
            row.addView(createTextView(String.valueOf(data.getAge())));
            row.addView(createTextView(data.getGender()));
            row.addView(createTextView(data.getRegion()));
            row.addView(createTextView(String.valueOf(data.gettotalValue())));
            row.addView(createTextView(data.getLastCrisisEvent()));
            tableLayout.addView(row);
        }

        // Populate the chart with the data for the current page
        populateChart(currentPageDataList);
    }

    // Call this method when the "Next Page" button is clicked
// Call this method when the "Next Page" button is clicked
    public void nextPage(View view) {
        int totalPages = (int) Math.ceil((double) dataList.size() / itemsPerPage);
        if (currentPage < totalPages - 1) {
            currentPage++;
            populateTable(dataList);
        }
    }

    // Call this method when the "Previous Page" button is clicked
    public void previousPage(View view) {
        if (currentPage > 0) {
            currentPage--;
        }
        populateTable(dataList);
    }
    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Set the layout_weight to 1
        textView.setTypeface(Typeface.DEFAULT_BOLD); // Make the text bold
        return textView;
    }
    private View createTableRow(String label, String value, ConstraintLayout container, View topView) {
        View row = LayoutInflater.from(this).inflate(R.layout.table_create, container, false);  // corrected layout file name
        TextView tvLabel = row.findViewById(R.id.tvLabel);
        TextView tvValue = row.findViewById(R.id.tvValue);
        tvLabel.setText("");
        tvValue.setText("");
        tvLabel.setText(label);
        tvValue.setText(value);

        // Ensure that each row has a unique ID to manage constraints
        row.setId(View.generateViewId());

        container.addView(row);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(container);
        if (topView == null) {
            constraintSet.connect(row.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 8);
        } else {
            constraintSet.connect(row.getId(), ConstraintSet.TOP, topView.getId(), ConstraintSet.BOTTOM, 8);
        }
        constraintSet.applyTo(container);

        return row;
    }
    @SuppressLint("SetTextI18n")
    private void handleError(Throwable throwable) {
//        textView.setText("Error: " + throwable.getMessage());
        Log.e(TAG, "Error: " + throwable.getMessage());
    }
    private void updateFirebaseFromCsv() {
        // Read the data from the CSV file
        List<SuicidePreventionData> dataList = new ArrayList<>();
        AssetManager manager = this.getApplicationContext().getAssets();
        InputStream is = null;
        try {
            is = manager.open(firebase_filenames.datastream_data_csv.getComponent_name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ... existing code for reading the CSV file ...

        // Update the Firebase Realtime Database
        firebaseOperations.uploadDataList(dataList);
    }
    // Step 1: Move the CSV file to the app's internal storage directory

    private void startFileObserver() {
        File csvFile = new File(getFilesDir(), "wellbeing_dataset.csv");

        FileObserver fileObserver = new FileObserver(csvFile.getAbsolutePath()) {
            @Override
            public void onEvent(int event, String path) {
                if ((FileObserver.MODIFY & event) != 0) {
                    // The CSV file was modified, update the Firebase data
                    updateFirebaseFromCsv();
                }
            }
        };

        // Start watching for changes
        fileObserver.startWatching();
    }



        @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataStream != null) {
            dataStream.stop();
        }
    }

}