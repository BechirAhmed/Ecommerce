package gr.uoa.di.airbnbproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fromRESTful.Residences;
import fromRESTful.Users;
import util.RetrofitCalls;
import util.Session;
import util.Utils;

public class AddResidenceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private static final int RESULT_LOAD_IMAGE =1;

    String token;
    Boolean user;

    ImageButton bcontinue, btnStartDate, btnEndDate;
    ImageView imageToUpload;
    Spinner etType;
    String resType;
    EditText etUpload, etAbout, etAddress, etCity, etCountry, etAmenities, etFloor, etRooms, etBaths, etView, etTitle, etSpaceArea, etGuests, etMinPrice, etAdditionalCost, etCancellationPolicy, etRules;
    TextView tvStartDate, tvEndDate;
    CheckBox cbKitchen, cbLivingRoom;
    boolean bkitchen, blivingRoom;

    private int mStartYear, mStartMonth, mStartDay, mEndYear, mEndMonth, mEndDay;

    Context c;
    Users host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Session sessionData = Utils.getSessionData(AddResidenceActivity.this);
        token = sessionData.getToken();
        if (!sessionData.getUserLoggedInState()) {
            Intent intent = new Intent(this, GreetingActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.layout_residence_editfields);
        c = this;

        user = false;
        userInputLayout();

        RetrofitCalls retrofitCalls = new RetrofitCalls();
        if(Utils.isTokenExpired(token))
        {
            Utils.logout(this);
            finish();
        }
        List<Users> userData = retrofitCalls.getUserbyUsername(token, sessionData.getUsername());

        host = userData.get(0);
        TextView residencetxt = (TextView) findViewById(R.id.residencetxt);
        residencetxt.setText("Add New Residence");
        saveResidence();

        /** BACK BUTTON **/
        Utils.manageBackButton(this, HostActivity.class, user);
    }

    public void userInputLayout () {
        etUpload             = (EditText)findViewById(R.id.etUpload);
        etTitle              = (EditText)findViewById(R.id.etTitle);
        etAbout              = (EditText)findViewById(R.id.etAbout);
        etAddress            = (EditText)findViewById(R.id.etAddress);
        etCity               = (EditText)findViewById(R.id.etCity);
        etCountry            = (EditText)findViewById(R.id.etCountry);
        etAmenities          = (EditText)findViewById(R.id.etAmenities);
        etFloor              = (EditText)findViewById(R.id.etFloor);
        etRooms              = (EditText)findViewById(R.id.etRooms);
        etBaths              = (EditText)findViewById(R.id.etBaths);
        etView               = (EditText)findViewById(R.id.etView);
        etSpaceArea          = (EditText)findViewById(R.id.etSpaceArea);
        etGuests             = (EditText)findViewById(R.id.etGuests);
        etMinPrice           = (EditText)findViewById(R.id.etMinPrice);
        etAdditionalCost     = (EditText)findViewById(R.id.etAdditionalCost);
        tvStartDate          = (TextView) findViewById(R.id.tvStartDate);
        tvEndDate            = (TextView)findViewById(R.id.tvEndDate);
        etCancellationPolicy = (EditText)findViewById(R.id.etCancellationPolicy);
        etRules              = (EditText)findViewById(R.id.etRules);

        btnStartDate        = (ImageButton)findViewById(R.id.btnStartDate);
        btnEndDate          = (ImageButton)findViewById(R.id.btnEndDate);

        cbKitchen           = (CheckBox)findViewById(R.id.cbKitchen);
        cbLivingRoom        = (CheckBox)findViewById(R.id.cbLivingRoom);

        bkitchen = (cbKitchen).isChecked();
        blivingRoom = (cbLivingRoom).isChecked();

        bcontinue = (ImageButton)findViewById(R.id.ibContinue);

//        imageToUpload = (ImageView)findViewById(R.id.imageToUpload);
//        imageToUpload.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
//            }
//        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnStartDate) {
                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    mStartYear = c.get(Calendar.YEAR);
                    mStartMonth = c.get(Calendar.MONTH);
                    mStartDay = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddResidenceActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            tvStartDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mStartYear, mStartMonth, mStartDay);
                    datePickerDialog.show();
                }
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnEndDate) {
                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    mEndYear = c.get(Calendar.YEAR);
                    mEndMonth = c.get(Calendar.MONTH);
                    mEndDay = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddResidenceActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            tvEndDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mEndYear, mEndMonth, mEndDay);
                    datePickerDialog.show();
                }
            }
        });

        etType = (Spinner) findViewById(R.id.etType);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinneradapter = ArrayAdapter.createFromResource(this, R.array.residence_types_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        etType.setAdapter(spinneradapter);
        etType.setOnItemSelectedListener(this);
    }

    /**  An item was selected. You can retrieve the selected item using parent.getItemAtPosition(pos) **/
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { resType = parent.getItemAtPosition(pos).toString(); }
    /** Another interface callback **/
    public void onNothingSelected(AdapterView<?> parent) { resType = "Residence Title"; }

    public void saveResidence () {
        bcontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.w("","SETTING HARDCODED RESIDENCE VALUES FOR DEBUGGING!");
                final String photo                      = etUpload.getText().toString();
                final String title                      = etTitle.getText().toString();
                final String type                       = resType;
                final String about                      = etAbout.getText().toString();
                final String address                    = etAddress.getText().toString();
                final String city                       = etCity.getText().toString();
                final String country                    = etCountry.getText().toString();
                final String amenities                  = etAmenities.getText().toString();
                final String floor                      = etFloor.getText().toString();
                final String rooms                      = etRooms.getText().toString();
                final String baths                      = etBaths.getText().toString();
                final String view                       = etView.getText().toString();
                final String spaceArea                  = etSpaceArea.getText().toString();
                final String guests                     = etGuests.getText().toString();
                final String minPrice                   = etMinPrice.getText().toString();
                final String additionalCostPerPerson    = etAdditionalCost.getText().toString();
                final String availableStartDate         = tvStartDate.getText().toString();
                final String availableEndDate           = tvEndDate.getText().toString();
                final String cancellationPolicy         = etCancellationPolicy.getText().toString();
                final String rules                      = etRules.getText().toString();
                final String kitchen                    = Boolean.toString(bkitchen);
                final String livingRoom                 = Boolean.toString(blivingRoom);

                Date startDate = Utils.ConvertStringToDate(availableStartDate, Utils.APP_DATE_FORMAT);
                String convertedStartDate = Utils.ConvertDateToString(startDate, Utils.DATABASE_DATE_FORMAT);

                Date endDate = Utils.ConvertStringToDate(availableEndDate, Utils.APP_DATE_FORMAT);
                String convertedEndDate = Utils.ConvertDateToString(endDate, Utils.DATABASE_DATE_FORMAT);

                if (title.length() == 0 || type.length() == 0 || about.length() == 0 || address.length() == 0 || city.length() == 0 || country.length() == 0 || amenities.length() == 0 || floor.length() == 0
                        || rooms.length() == 0 || baths.length() == 0 || view.length() == 0 || spaceArea.length() == 0 || guests.length() == 0 || minPrice.length() == 0
                        || additionalCostPerPerson.length() == 0 || cancellationPolicy.length() == 0 || rules.length() == 0 || kitchen.length() == 0 || livingRoom.length() == 0
                        || convertedStartDate.length() == 0 || convertedEndDate.length() == 0 || photo.length() == 0) {
                    Toast.makeText(c, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    token = PostResult(host, title, type, about, cancellationPolicy, country, city, address, rules, amenities, Integer.parseInt(floor),
                            Integer.parseInt(rooms), Integer.parseInt(baths), Double.parseDouble(spaceArea), photo, Integer.parseInt(guests), startDate, endDate,
                            Double.parseDouble(minPrice), Double.parseDouble(additionalCostPerPerson), Boolean.parseBoolean(kitchen), Boolean.parseBoolean(livingRoom),
                            view);

                    if (!token.isEmpty() && token!=null && token != "not") {
                        Intent hostIntent = new Intent(AddResidenceActivity.this, HostActivity.class);
                        Bundle bhost = new Bundle();
                        bhost.putBoolean("type", user);
                        hostIntent.putExtras(bhost);
                        try {
                            startActivity(hostIntent);
                            finish();
                        } catch (Exception e) {
                            Log.e("",e.getMessage());
                        }
                    } else {
                        Toast.makeText(c, "Your session is finished, please login again!", Toast.LENGTH_SHORT).show();
                        Utils.logout(AddResidenceActivity.this);
                        finish();
                    }
                }
            }
        });
    }

    public String PostResult(Users hostId, String title, String type, String about, String cancellationPolicy, String country, String city, String address, String rules,
                             String amenities, int floor, int rooms, int baths, double spaceArea, String photos, int guests, Date availableDateStart,
                             Date availableDateEnd, double minPrice, double additionalCostPerPerson, boolean kitchen, boolean livingRoom, String view)
    {
        Residences ResidenceParameters = new Residences(hostId, title, type, about, cancellationPolicy, country, city, address, rules, amenities, floor, rooms,
                baths, spaceArea, photos, guests, availableDateStart, availableDateEnd, minPrice, additionalCostPerPerson, kitchen, livingRoom, view);

        RetrofitCalls retrofitCalls = new RetrofitCalls();
        token = retrofitCalls.postResidence(token, ResidenceParameters);
        return token;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && requestCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }
}