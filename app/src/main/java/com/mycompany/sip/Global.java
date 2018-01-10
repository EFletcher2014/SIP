package com.mycompany.sip;

/**
 * Created by Emily on 12/7/2017.
 */

public class Global {

    // url to get all sites list
    public static String url_all_sites = "http://75.134.106.101:80/mapp/get_all_sites.php";

    // url to create new site
    public static String url_create_site = "http://75.134.106.101:80/mapp/create_new_site.php";

    // url to get all units list
    public static String url_all_units = "http://75.134.106.101:80/mapp/get_all_units.php";

    // url to create a new unit
    public static String url_create_unit = "http://75.134.106.101:80/mapp/create_new_unit.php";

    // url to get all levels list
    public static String url_all_levels = "http://75.134.106.101:80/mapp/get_all_levels.php";
    
    // url to create new level
    public static String url_create_level = "http://75.134.106.101/mapp/create_new_level.php";

    // url to upload image
    public static String url_upload_image = "http://75.134.106.101/mapp/upload_image.php";

    // url to get all artifacts list
    public static String url_all_artifacts = "http://75.134.106.101:80/mapp/get_all_artifacts.php";
    
    // url to create new artifact
    public static String url_create_artifact = "http://75.134.106.101:80/mapp/create_new_artifact.php";
    
    // Site JSON Node names--column headings in database, which allow JSON to parse data
    public static final String TAG_PID = "PrimaryKey";
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_SITES = "sites";
    public static final String TAG_SITENUM = "siteNumber";
    public static final String TAG_SITENAME = "siteName";
    public static final String TAG_LOC = "location";
    public static final String TAG_DESC = "description";
    public static final String TAG_DATEDISC = "dateDiscovered";

    // Unit JSON Node names
    public static final String TAG_UNITS = "units";
    public static final String TAG_UNITNAME = "datum";
    public static final String TAG_NS = "nsDim";
    public static final String TAG_EW = "ewDim";
    public static final String TAG_DATEOPEN = "dateOpened";
    public static final String TAG_EXCS = "excavators";
    public static final String TAG_REAS = "reasonForOpening";
    
    // Level JSON Node names
    public static final String TAG_LEVELS = "levels";
    public static final String TAG_LVLNUM = "lvlNum";
    public static final String TAG_BD = "begDepth";
    public static final String TAG_ED = "endDepth";
    public static final String TAG_DATE = "dateStarted";
    public static final String TAG_EXCM = "excavationMethod";
    public static final String TAG_IMPATH = "imagePath";

    // Artifact JSON Node names
    public static final String TAG_ARTIFACTS = "artifacts";
    public static final String TAG_ANUM = "accNum";
    public static final String TAG_CNUM = "catNum";
    public static final String TAG_CONT = "contents";
}