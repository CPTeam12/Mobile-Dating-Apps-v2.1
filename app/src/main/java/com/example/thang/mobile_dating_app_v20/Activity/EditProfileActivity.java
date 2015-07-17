package com.example.thang.mobile_dating_app_v20.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends ActionBarActivity {
    private static final String URL_UPDATE = MainActivity.URL_CLOUD + "/Service/updateprofile";
    MaterialEditText fullname, age, phone, address;
    RadioButton male, female;
    FloatingActionButton update;
    //MaterialMultiAutoCompleteTextView hobby;
    Person currentPerson;
    ImageView avatar;
    private MaterialDialog materialDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.edit_profile));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //get current use
        DBHelper helper = new DBHelper(getApplicationContext());
        currentPerson = helper.getCurrentUser();

        fullname = (MaterialEditText) findViewById(R.id.edit_fullname);
        age = (MaterialEditText) findViewById(R.id.edit_age);
        phone = (MaterialEditText) findViewById(R.id.edit_phone);
        address = (MaterialEditText) findViewById(R.id.edit_address);
        male = (RadioButton) findViewById(R.id.edit_male_rb);
        female = (RadioButton) findViewById(R.id.edit_female_rb);
        update = (FloatingActionButton) findViewById(R.id.edit_accept);

        String[] hobbies = getResources().getStringArray(R.array.register_hobbies);
        avatar = (ImageView) findViewById(R.id.edit_profile_avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 200);
            }
        });

        setupProfile();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    currentPerson.setFullName(fullname.getText().toString());
                    currentPerson.setAge(Integer.parseInt(age.getText().toString()));
                    //currentPerson.setHobbies(hobby.getText().toString());
                    if (!phone.getText().toString().isEmpty())
                        currentPerson.setPhone(phone.getText().toString());
                    if (!address.getText().toString().isEmpty())
                        currentPerson.setAddress(address.getText().toString());
                    if (male.isChecked()) currentPerson.setGender("Male");
                    if (female.isChecked()) currentPerson.setGender("Female");
                    List<Person> persons = new ArrayList<Person>();
                    persons.add(currentPerson);
                    new updateProfileTask().execute(persons);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.commit_update) {
            if (validate()) {
                currentPerson.setFullName(fullname.getText().toString());
                currentPerson.setAge(Integer.parseInt(age.getText().toString()));
                //currentPerson.setHobbies(hobby.getText().toString());
                if (!phone.getText().toString().isEmpty())
                    currentPerson.setPhone(phone.getText().toString());
                if (!address.getText().toString().isEmpty())
                    currentPerson.setAddress(address.getText().toString());
                if (male.isChecked()) currentPerson.setGender("Male");
                if (female.isChecked()) currentPerson.setGender("Female");
                List<Person> persons = new ArrayList<Person>();
                persons.add(currentPerson);
                new updateProfileTask().execute(persons);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == -1 && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap avatar = (Utils.resizeBitmap(Utils.scaleBitmapFromFile(picturePath, 200, 200)));
            //Bitmap avatar = Utils.scaleBitmapFromFile(picturePath, 400, 400);
            this.avatar.setImageBitmap(avatar);
            //Convert avatar to Base64 for transfer to service
            currentPerson.setAvatar(Utils.encodeBitmapToBase64String(avatar));

        }
    }

    private void setupProfile() {
        if (currentPerson != null) {
            fullname.setText(currentPerson.getFullName());
            age.setText(currentPerson.getAge() + "");
            //hobby.setText(currentPerson.getHobbies());
            if (currentPerson.getGender().equalsIgnoreCase("male")) {
                male.setChecked(true);
                female.setChecked(false);
            } else {
                male.setChecked(false);
                female.setChecked(true);
            }
            address.setText(currentPerson.getAddress());
            if (currentPerson.getPhone() == null) currentPerson.setPhone("");
            phone.setText(currentPerson.getPhone());
            avatar.setImageBitmap(currentPerson.getAvatar().isEmpty() ? BitmapFactory.decodeResource(getResources(),
                    R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(currentPerson.getAvatar()));
        }
    }

    public boolean validate() {
        String error = getResources().getString(R.string.error_field_required);
        boolean a = fullname.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean b = age.validateWith(new RegexpValidator(getResources().getString(R.string.error_invalid_age), "^(\\d{2})([\\.|,]\\d{1,2})?$"));
        return (a && b);
    }

    private class updateProfileTask extends AsyncTask<List<Person>, Integer, String> {

        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(EditProfileActivity.this);
            if (!connectionTool.isNetworkAvailable()) {
                new MaterialDialog.Builder(EditProfileActivity.this)
                        .title(R.string.error_connection_title)
                        .content(R.string.error_connection)
                        .titleColorRes(R.color.md_red_400)
                        .show();
            }
        }

        @Override
        protected String doInBackground(List<Person>... persons) {
            List<Person> p = persons[0];
            return ConnectionTool.makePostRequest(URL_UPDATE, p);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //close loading dialog
                if (materialDialog != null) {
                    materialDialog.dismiss();
                }

                //start parsing jsonResponse
                JSONObject jsonObject = new JSONObject(result);
                List<Person> personList = ConnectionTool.fromJSON(jsonObject);
                if (personList != null) {
                    //update current user into database
                    DBHelper helper = new DBHelper(getApplicationContext());
                    helper.updatePerson(currentPerson);

                    //move to avatar activity
                    Bundle bundle = new Bundle();
                    bundle.putString("ProfileOf", DBHelper.USER_FLAG_CURRENT);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext().getApplicationContext(),
                            getResources().getText(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
