package com.mycompany.sip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewSiteActivity extends Activity {

    //test site
    boolean test=true;
    SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
    String siteName;
    String description;
    String dateFound;
    String siteNumber;


    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText inputName;
    EditText inputDesc;
    EditText inputDate;
    EditText inputNumb;

    //TODO: get actual URL
    // url to create new site
    private static String url_create_site = "https://api.androidhive.info/android_connect/create_product.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_site);

        // Edit Text
        //TODO:change these
        inputName = (EditText) findViewById(R.id.inputName);
        inputDesc = (EditText) findViewById(R.id.inputDesc);
        inputDate = (EditText) findViewById(R.id.inputDate);
        inputNumb = (EditText) findViewById(R.id.inputNumb);

        // Create button
        Button btnCreateSite = (Button) findViewById(R.id.btnCreateSite);

        // button click event
        btnCreateSite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                siteName = inputName.getText().toString();
                description = inputDesc.getText().toString();
                dateFound = format.format(inputDate.getText().toString());
                siteNumber = inputNumb.getText().toString();

                //if not testing, save to server
                if(!test) {

                    // creating new site in background thread
                    new CreateNewSite().execute();
                }
                else
                {
                    System.out.println("siteName: " + siteName + "/ndescription: " + description + "/ndateFound: " + dateFound + "/nsiteNumber: " + siteNumber);
                    // just go to next activity
                    //TODO: Should this go to NewSiteActivity or AllSitesActivity?
                    Intent i = new Intent(getApplicationContext(), AllSitesActivity.class);
                    startActivity(i);

                }
            }
        });
    }

    /**
     * Background Async Task to Create new product
     * */
    class CreateNewSite extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewSiteActivity.this);
            pDialog.setMessage("Creating Site..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating site
         * */
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap params = new HashMap();
            params.put("siteName", siteName);
            params.put("description", description);
            params.put("dateFound", dateFound);
            params.put("siteNumber", siteNumber);

            // getting JSON Object
            // Note that create site url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_site,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created product
                    //TODO: Should this go to NewSiteActivity or AllSitesActivity?
                    Intent i = new Intent(getApplicationContext(), NewSiteActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }
}