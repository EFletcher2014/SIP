package com.mycompany.sip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import org.json.*;

import static android.content.ContentValues.TAG;
import static com.mycompany.sip.Global.*;

//TODO: allow this to save a new level and also edit an old one

public class MapHome extends AppCompatActivity {

    public static boolean isActive;

    // Progress Dialog
    private ProgressDialog pDialog;

    //Firebase
    FirebaseHandler fbh = FirebaseHandler.getInstance();

    private static int SELECT_PICTURE = 1;
    JSONParser jsonParser = new JSONParser();
  
    private Uri selectedImageUri = null;
    private String userName = "root";
    private String password = "";
    private String dbms = "mysql";
    private String serverName = "192.168.2.7";//"192.168.1.187"; //"184.53.49.56";
    private String portNumber = "3306";
    private String dbName = "mapp";
    private String siteName;
    private String siteNumber;
    private String ID;
    private int pk = -1, fk = -1, lvlNum = -1;
    private String unitNumber = "";
    private String levelNumber;
    private String levelDepth;
    private String imageReference = "IMAGE";
    private String description = "DESCRIPTION HERE2";
    private String dateTime;
    private Site site;
    private Unit unit;
    private Level level;
    private EditText begDepth;
    private EditText endDepth;
    private EditText excMeth, notes;
    private ImageView unitImage;
    private AlertDialog.Builder alert, alert1;
    private ViewSwitcher switcher;
    private static final int CAMERA_REQUEST = 1888;
    private int rotation = 0;
    private boolean madeLevel = false;

    @Override
    public void onStart()
    {
        super.onStart();
        isActive = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        isActive = false;
    }

    public boolean isActive()
    {
        return isActive;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        //tried to make a new titlebar, didn't work, said I couldn't have two
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.mytitle);
        super.onCreate(savedInstanceState);
        fbh.updateLevelDocActivity(this);

        setContentView(R.layout.activity_map_home);
        switcher= (ViewSwitcher) findViewById(R.id.imageSwitch);
        begDepth = (EditText) findViewById(R.id.enterBegDepth);
        endDepth = (EditText) findViewById(R.id.enterEndDepth);
        excMeth = (EditText) findViewById(R.id.techniques);
        notes = (EditText) findViewById(R.id.level_notes);
        unitImage = (ImageView) findViewById(R.id.unitImgView);


        if(savedInstanceState!=null)
        {
            selectedImageUri=savedInstanceState.getParcelable("URI");
            if(selectedImageUri!=null)
            {
                switcher.showNext();
                unitImage.setImageURI(selectedImageUri);
                Bitmap bm=((BitmapDrawable)unitImage.getDrawable()).getBitmap();
                rotation=savedInstanceState.getInt("rotation");
                Bitmap bitmap = rotateBitmap(bm, rotation);
                unitImage.setImageBitmap(bitmap);
            }

            if(savedInstanceState.getBoolean("alert"))
            {
                showCancelDialog();
            }
            if(savedInstanceState.getBoolean("alert1"))
            {
                showChooserDialog();
            }
        }

        //added by Emily Fletcher 8/27/17
        Intent openIntent = getIntent();
        level = openIntent.getParcelableExtra("depth");
        if(level!=null) {
            lvlNum = level.getNumber();
            site = level.getSite();
            siteName = site.getName();
            siteNumber = site.getNumber();
            unit = level.getUnit();
            unitNumber = unit.getDatum();
            levelNumber = level.getNumber() + "";
            levelDepth = level.getDepth();
            if(level.getBegDepth()!=-1)
            {
                begDepth.setText(level.getBegDepth() + "");
            }
            if(level.getEndDepth()!=-1)
            {
                endDepth.setText(level.getEndDepth() + "");
            }
            excMeth.setText(level.getExcavationMethod() + "");
            notes.setText(level.getNotes() + "");
            Uri tempUri = fbh.getImage(level,site.getNumber() + "/" + unit.getDatum() + "/", "level" + level.getNumber() + "Map");
            if(tempUri != null) {
                selectedImageUri = tempUri;
                //unitImage.setImageURI(selectedImageUri);
                if (switcher.getNextView().equals(findViewById(R.id.pictures))) {
                    switcher.showNext();
                }
            }
        }
        else
        {
            //TODO: these vvvvvvv
            //Query server for new level number
        }
        TextView siteNameText = (TextView) findViewById(R.id.SiteNameLevel);
        siteNameText.setText("Site: " + siteName);
        TextView siteNumberText = (TextView) findViewById(R.id.siteNumLevel);
        siteNumberText.setText("Site Number: " + siteNumber);
        TextView unitNumberText = (TextView) findViewById(R.id.UnitNumberLevel);
        unitNumberText.setText("Unit: " + unitNumber);
        TextView levelNumberText = (TextView) findViewById(R.id.levelNumber);
        levelNumberText.setText("Level " + levelNumber);

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooserDialog();
            }
        });

        final FloatingActionButton rotate = (FloatingActionButton) findViewById(R.id.rotateFab);
        rotate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                Bitmap bm=((BitmapDrawable)unitImage.getDrawable()).getBitmap();
                Bitmap bmRotated = rotateBitmap(bm, 90);
                unitImage.setImageBitmap(bmRotated);
                rotation+=90;
                rotation%=360;
            }
        });

        final Button toAddArtifactActivity = (Button) findViewById(R.id.toAddArtifactsActivity);
        toAddArtifactActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(level!=null) {
                    //Move to select on image activity
                    Intent artifactActivityIntent = new Intent(view.getContext(), AllArtifactsActivity.class);
                    artifactActivityIntent.putExtra("name", site);
                    artifactActivityIntent.putExtra("datum", unit);
                    artifactActivityIntent.putExtra("depth", level);
                    artifactActivityIntent.putExtra("PrimaryKey", pk);
                    startActivity(artifactActivityIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You must save your level before creating artifacts", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button toSelectOnImageActivity = (Button) findViewById(R.id.select_on_image_button);
        toSelectOnImageActivity.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                    if(level!=null) {
                        if(selectedImageUri!=null) {
                            //Move to select on image activity
                            Intent selectActivityIntent = new Intent(Intent.ACTION_ATTACH_DATA, selectedImageUri, view.getContext(), selectActivity.class);
                            selectActivityIntent.putExtra("unit", unit);
                            selectActivityIntent.putExtra("rotation", rotation);
                            startActivityForResult(selectActivityIntent, 33);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "You must add a picture of your unit before using this feature", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "You must save your level before mapping", Toast.LENGTH_SHORT).show();
                    }
              }
        });

        //Button to save the level
        final Button saveButton = (Button) findViewById(R.id.mainsave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                Double bd = Double.parseDouble(begDepth.getText().toString());
                Double ed = Double.parseDouble(endDepth.getText().toString());

                //TODO: add date started input or remove
                //String date = dateTime.getText().toString();
                String em = excMeth.getText().toString();
                String n = notes.getText().toString();

                level.setBegDepth(bd);
                level.setEndDepth(ed);
                level.setExcavationMethod(em);
                level.setNotes(n);

                fbh.createLevel(level);
            }
        });

        //Button to cancel all work
        final Button cancelButton = (Button) findViewById(R.id.maincancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Throws dialog asking if user wants to cancel without saving
                showCancelDialog();
            }
        });

    }

    /**Displays cancel dialog ("Are you sure you want to go back? You will lose your progress")
     *when the user clicks the back button*/
    @Override
    public void onBackPressed()
    {
        showCancelDialog();
    }

    /**
     * FROM STACKOVERFLOW https://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically*****
     */
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(requestCode == 33)
         {
             if(resultCode == RESULT_OK) {
                 //TODO: will this work? is it necessary
                 selectedImageUri = data.getParcelableExtra("newURI");

                 final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                 unitImage.setImageURI(selectedImageUri);
             }
         }
         else {
             if (resultCode == RESULT_OK) {

                 if (switcher.getNextView().equals(findViewById(R.id.pictures))) {
                     switcher.showNext();
                 }
                 selectedImageUri = data.getData();
                 imageReference = selectedImageUri.toString();
                 unitImage.setImageURI(selectedImageUri);
                 rotation = 0;
             }
         }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);

        // Save our own state now
        outState.putParcelable("URI", selectedImageUri);
        outState.putBoolean("alert", (alert!=null));
        outState.putBoolean("alert1", (alert1!=null));
        outState.putInt("rotation", rotation);
    }

    private void showCancelDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        final View cancelLayout = inflater.inflate(R.layout.cancel_level_dialog, null);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme));
        }
        else
        {
            alert = new AlertDialog.Builder(MapHome.this);
        }
        alert.setTitle("Cancel?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alert=null;
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }

        });
        alert.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Go back
                alert=null;
            }
        });
        alert.setView(cancelLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void showChooserDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        final View chooserLayout = inflater.inflate(R.layout.chooser_dialog, null);
        alert1 = new AlertDialog.Builder(MapHome.this);
        alert1.setView(chooserLayout);
        alert1.setTitle("Add A Picture:");
        alert1.setIcon(R.drawable.ic_add_a_photo_white_24dp);
        final AlertDialog dialog = alert1.create();
        dialog.show();
        ImageButton findPic = (ImageButton) chooserLayout.findViewById(R.id.goToGallery);
        findPic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Get Picture
                Intent pictureIntent = new Intent();
                pictureIntent.setType("image/*");
                pictureIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
                pictureIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(pictureIntent, "Select an aerial view of your unit"), SELECT_PICTURE);
                alert1=null;
                dialog.cancel();
            }

        });

        ImageButton takePic = (ImageButton) chooserLayout.findViewById(R.id.takeNewPicture);
        takePic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Take picture from camera
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                alert1=null;
                dialog.cancel();
            }
        });
    }

    //From https://stackoverflow.com/questions/31781150/auto-image-rotated-from-portrait-to-landscape
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        matrix.setRotate(orientation);

        try {
            System.out.println("rotating!");
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor=null;
        String[] proj = {MediaStore.Images.Media.DATA};
        cursor = this.getBaseContext().getContentResolver().query(contentURI, proj, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            System.out.println("Cursor: " + cursor);
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}