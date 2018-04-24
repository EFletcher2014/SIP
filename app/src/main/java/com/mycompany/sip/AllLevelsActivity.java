//All from androidhive 7/30/17
package com.mycompany.sip;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import static com.mycompany.sip.Global.*;

public class AllLevelsActivity extends ListActivity {
    // Progress Dialog
    private ProgressDialog pDialog;

    LocalDatabaseHandler ldb = new LocalDatabaseHandler(this);
    RemoteDatabaseHandler rdb = new RemoteDatabaseHandler(this);

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> levelsList;

    private static Site site;
    private static Unit unit;
    private static Level level;
    private static int levelNumber;
    private static int foreignKey;
    private static int primaryKey;

    private AlertDialog.Builder alert;
    ArrayList<Level> allLevels = new ArrayList<>();



    boolean test=false;
    Level[] testLevels = {new Level(1, 10.0, 15.0, site, unit, "11/03/1996", "shovel skimmed", "we did things", 1, null, null),
            new Level(2, 15.0, 20.0, site, unit, "07/22/17", "troweling", "more things", 2, null, null),
            new Level(3, 20.0, 25.0, site, unit, "08/2/17", "backhoe", "more things", 3, null, null)};

    // levels JSONArray
    JSONArray levels = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_levels);

        if(savedInstanceState!=null)
        {
            if(savedInstanceState.getBoolean("alert"))
            {
                showDialog((Level) savedInstanceState.getParcelable("level"));
            }
        }

        // Hashmap for ListView
        levelsList = new ArrayList<HashMap<String, String>>();

        //added by Emily Fletcher 8/27/17
        Intent openIntent = getIntent();
        foreignKey = openIntent.getIntExtra("PrimaryKey", -1);
        site = openIntent.getParcelableExtra("siteName");
        unit = openIntent.getParcelableExtra("datum");
        TextView titleText = (TextView) findViewById(R.id.siteNameUnitNumber);
        String title = site.getName() + " " + unit.getDatum() + " Levels";
        titleText.setText(title);

        if(!test) {
            // Loading sites in Background Thread
            new LoadAllLevels().execute();
        }
        else
        {
            // looping through All levels
            for (int i = 0; i < 3; i++) {

                String depth = testLevels[i].getDepth();

                // creating new HashMap
                HashMap<String, String> testMap = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                testMap.put(TAG_PID, i + "");
                testMap.put("name", depth);

                // adding HashList to ArrayList
                levelsList.add(testMap);
                System.out.println(levelsList);
            }
            ListAdapter adapter = new SimpleAdapter(
                    AllLevelsActivity.this, levelsList,
                    R.layout.list_item, new String[] { TAG_PID,
                    "name"},
                    new int[] { R.id.pid, R.id.name });
            // updating listview
            setListAdapter(adapter);
        }

        // Get listview
        ListView lv = getListView();

        //TODO: make this do something else (but what??). Fix in other activities
        // on seleting single level
        // launching Edit level Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String su = ((TextView) view.findViewById(R.id.su)).getText()
                        .toString();
                levelNumber=Integer.parseInt(su);
                if(test) {
                    level = testLevels[levelNumber];
                }
                else
                {
                    level = allLevels.get(levelNumber);
                }
                primaryKey = Integer.parseInt(((TextView) view.findViewById(R.id.pid)).getText().toString());
                System.out.println(level.toString());

                showDialog(level);
            }
        });

        //on clicking new Level button
        //launching new level activity
        Button newLevel = (Button) findViewById(R.id.newLevelBtn);
        newLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Add New product Activity
                Intent i = new Intent(view.getContext(),
                        MapHome.class);
                i.putExtra("depth", new Level(allLevels.size()+1, -1, -1, site, unit, "", "", "", -1, null, null));
                //i.putExtra("ForeignKey", foreignKey);
                //i.putExtra("lvlNum", allLevels.size()+1);
                //i.putExtra("siteName", site);
                //i.putExtra("unitNumber", unit);
                // Closing all previous activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i, 333);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode == 333)
        {
            if(resultCode == RESULT_OK)
            {
                new LoadAllLevels().execute();
                //finish();
                //startActivity(getIntent());
            }
        }*/
        // if result code 100
        if (resultCode == RESULT_OK) {
            // if result code 100 is received
            // means user edited/deleted level
            // reload this screen again
            //Intent intent = getIntent();
            //finish();
            //startActivity(intent);
            new LoadAllLevels().execute();
        }

        if(resultCode == RESULT_CANCELED)//if user canceled without saving a new level
        {
            //do nothing, just stay on current empty list of levels
        }

    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllLevels extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllLevelsActivity.this);
            pDialog.setMessage("Loading levels. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            //new UpdateDBs(getApplicationContext()).execute();
        }

        /**
         * getting All levels from url
         * */
        protected String doInBackground(String... args) {
            allLevels = rdb.loadAllLevels(unit);
            levelsList = new ArrayList<HashMap<String, String>>();

            for(int i=0; i<allLevels.size(); i++)
            {
                Level temp = allLevels.get(i);

                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                map.put(TAG_PID, temp.getPk() + "");
                map.put("name", temp.toString());
                map.put("Unit Level", i + "");

                // adding HashList to ArrayList
                levelsList.add(map);
            }
            // Building Parameters
            /*HashMap params = new HashMap();

            params.put("foreignKey", foreignKey);

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_levels, "GET", params);

            try {
                // Check your log cat for JSON reponse
                Log.d("All levels: ", json.toString());

                try {
                    // Checking for SUCCESS TAG
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // levels found
                        // Getting Array of levels
                        levels = json.getJSONArray(TAG_LEVELS);

                        // looping through All sites
                        for (int i = 0; i < levels.length(); i++) {
                            JSONObject c = levels.getJSONObject(i);

                            // Storing each json item in variable
                            String id = c.getString(TAG_PID);
                            int num = c.getInt(TAG_LVLNUM);
                            Double bd = c.getDouble(TAG_BD);
                            Double ed = c.getDouble(TAG_ED);
                            String date = c.getString(TAG_DATE);
                            String excm = c.getString(TAG_EXCM);

                            Level temp = new Level(num, bd, ed, site, unit, date, excm, "", Integer.parseInt(id));
                            allLevels.add(temp);
                            String name = temp.toString();


                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map.put(TAG_PID, id);
                            map.put("name", name);
                            map.put("Unit Level", i + "");

                            // adding HashList to ArrayList
                            levelsList.add(map);

                            //save to local database
                            System.out.println(temp.getUnit().getPk());
                            if (ldb.updateLevel(temp, temp.getUnit().getPk()) == 0) {
                                System.out.println("Adding new level to SQLite DB");
                                ldb.addLevel(temp);
                            } else {
                                System.out.println();
                            }
                            System.out.println(ldb.getLevelsCount());
                            System.out.println(ldb.getLevel(i));
                            System.out.println(ldb.getAllLevels().toString());
                        }
                    } else {
                        // no levels found
                        // Launch Add New level Activity
                        //TODO: Change it so it isn't getApplicationContext
                        *//*Intent i = new Intent(getApplicationContext(),
                                MapHome.class);
                        // Closing all previous activities
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);*//*
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }catch(NullPointerException e)
            {
                System.out.println("Coudln't connect to remote server, loading levels from local sever instead");
                allLevels = (ArrayList) ldb.getAllLevelsFromUnit(unit.getPk());
                for(int i=0; i<allLevels.size(); i++)
                {
                    Level temp = allLevels.get(i);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_PID, temp.getPk() + "");
                    map.put("name", temp.toString());
                    map.put("Unit Level", i + "");

                    // adding HashList to ArrayList
                    levelsList.add(map);
                }
            }*/
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all sites
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    if(levelsList.size()!=0) {
                        ListAdapter adapter = new SimpleAdapter(
                                AllLevelsActivity.this, levelsList,
                                R.layout.list_item, new String[]{TAG_PID,
                                "name", "Unit Level"},
                                new int[]{R.id.pid, R.id.name, R.id.su});
                        // updating listview
                        setListAdapter(adapter);
                    }
                    else
                    {
                            Intent i = new Intent(findViewById(R.id.newLevelBtn).getContext(),
                                    MapHome.class);
                            i.putExtra("depth", new Level(allLevels.size() + 1, -1, -1, site, unit, "", "", "", -1, null, null));
                            //i.putExtra("ForeignKey", foreignKey);
                            //i.putExtra("lvlNum", 1);
                            //i.putExtra("siteName", site);
                            //i.putExtra("unitNumber", unit);
                            // Closing all previous activities
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(i, 333);
                            Toast.makeText(getApplicationContext(), "No levels have been created for this unit yet. You can create one here.", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        if(alert!=null)
        {
            //TODO: Figure out how to save alert and all of its corresponding strings--UGH
            outState.putBoolean("alert", true);
            outState.putParcelable("level", level);

        }
        else
        {
            outState.putBoolean("alert", false);
        }
    }

    public void showDialog(Level lvl)
    {
        LayoutInflater inflater = getLayoutInflater();
        final View editLevelLayout = inflater.inflate(R.layout.edit_level_dialog, null);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme));
        }
        else
        {
            alert = new AlertDialog.Builder(AllLevelsActivity.this);
        }
        alert.setTitle("Level " + lvl.toString());
        System.out.println("You should have a dialog");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Pass intent
                // getting values from selected ListItem

                // Starting new intent
                //TODO: Will have to add call to server to pick the correct level sheet to edit
                //TODO: is this the right context?
                Intent in = new Intent(editLevelLayout.getContext(),
                        MapHome.class);
                System.out.println(in);
                System.out.println(level.getPk());
                // sending pid to next activity
                //in.putExtra("ForeignKey", foreignKey);
                //in.putExtra("PrimaryKey", primaryKey);
                //in.putExtra("lvlNum", level.getNumber());
                //in.putExtra("siteName", site);
                //in.putExtra("unitNumber", unit);
                in.putExtra("depth", level);
                //System.out.println(site + " " + unit + " " + level);
                System.out.println(in.getExtras());

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
                alert=null;
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alert=null;
                //Go back
            }
        });
        alert.setView(editLevelLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}