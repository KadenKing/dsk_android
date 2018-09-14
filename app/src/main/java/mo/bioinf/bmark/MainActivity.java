package mo.bioinf.bmark;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;





public class MainActivity extends AppCompatActivity {

    private DSK_Parcel parcel = new DSK_Parcel(31,2000, 20000, 0, 0);



    public void checkForSolids(String filename, String base_path){
        final Button delete_button = (Button) findViewById(R.id.delete_button);

        String pathStr = parcel.getDevicePath() + "/" + parcel.getFilename() + "_gatb";

        File file = new File(pathStr);

        if(file.exists())
        {
            delete_button.setEnabled(true);
        }else{
            delete_button.setEnabled(false);
            Log.println(Log.INFO,"solid deleter", pathStr + " not found");
        }

        //System.out.println(path);

    }


    void updateTV(TextView tv, int kmer, int memory, int disk, String path, int repartition_type, int minimizer_type){
        String text = "kmer: " + kmer + "\n" +
                        "memory: " + memory + "\n" +
                        " disk: " + disk + "\n" +
                        "path: " + path + "\n" +
                        "repartition type: " + parcel.repartition2string() + "\n" +
                        "minimizer type: " + parcel.minimizer2string() + "\n";
        tv.setText(text);
    }

    void populateDropdown(Context context, Spinner dropdown, String base_path){
        /*gets this phone's directory within the application*/

        File fastq_folder = new File(base_path);
        File[] fastq_files = fastq_folder.listFiles();
        List<String> fastq_file_names = new ArrayList<String>();
        for(File fastq_file : fastq_files)
        {
            fastq_file_names.add(fastq_file.getName().toString());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, fastq_file_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data != null){
            DSK_Parcel returnParcel = data.getParcelableExtra("returnParcel");

            returnParcel.setFullPath(parcel.getFullPath());
            returnParcel.setFilename(parcel.getFilename());
            returnParcel.setDevicePath(parcel.getDevicePath());
            parcel = returnParcel;


            final TextView tv = (TextView) findViewById(R.id.sample_text);
            updateTV(tv, parcel.getKmer(),parcel.getMemory(), parcel.getDisk(),parcel.getFullPath(),
                    parcel.getRepartition_type(), parcel.getMinimizer_type());
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;

        /*** instances of ui widgets ***/
        final TextView tv = (TextView) findViewById(R.id.sample_text);
        final Button run = (Button) findViewById(R.id.run_button);
        final Spinner dropdown = (Spinner) findViewById(R.id.fastq_files);
        final ImageButton settings_button = (ImageButton) findViewById(R.id.settings_button);
        final Button delete_button = (Button) findViewById(R.id.delete_button);
        /*******************************/


        run.setEnabled(false); // enabled later when the file path is verified

        final String base_path = context.getFilesDir().getAbsolutePath().toString() + "/fastq/"; // this phone's working directory
        parcel.setDevicePath(context.getFilesDir().getAbsolutePath().toString());
        parcel.setFullPath(base_path);

        populateDropdown(context, dropdown, base_path); // fills the dropdown with the files in the fastq folder



        /** path[] is an array because a work-around is needed to edit a variable from within the anonymous method
          */
        final String path[] = {base_path};

        /*current file path is updated and shown on the text view */
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                parcel.setFilename(selectedItem);
                parcel.setFullPath(base_path  + selectedItem);
                updateTV(tv, parcel.getKmer(),parcel.getMemory(), parcel.getDisk(),parcel.getFullPath(),
                        parcel.getRepartition_type(), parcel.getMinimizer_type());


                checkForSolids(selectedItem,base_path);
                /*makes sure that if we press the run button the app won't crash due to a file permission error*/
                checkPermission();
                if (tryOpenFile(context,parcel.getFullPath()) && isExternalStorageReadable() && isExternalStorageWritable()) {
                    run.setEnabled(true);
                }

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        /*** open settings activity ***/
        settings_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                Intent myIntent = new Intent(getBaseContext(), SettingsActivity.class);
                myIntent.putExtra("parcel",parcel);

                MainActivity.this.startActivityForResult(myIntent,1);



            }
        });
        /***********************************/

        /*** run DSK ***/
        run.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                File solids_folder = new File(parcel.getDevicePath() + "/" + parcel.getFilename() + "_gatb");
                if(solids_folder.exists())
                {
                    Intent results_intent = new Intent(getBaseContext(),ResultsActivity.class);
                    results_intent.putExtra("runtime", "already run");
                    results_intent.putExtra("filename", parcel.getFilename());
                    MainActivity.this.startActivity(results_intent);
                }else{
                    Intent dskIntent = new Intent(getBaseContext(),DSKRunning.class);
                    dskIntent.putExtra("parcel",parcel);

                    MainActivity.this.startActivity(dskIntent);
                }





            }
        });
        /****************/

        delete_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                String filename = parcel.getFilename();
                String device_path = parcel.getDevicePath();
                String path = device_path + "/" + filename + "_gatb";

                File folder = new File(path);

                String[] files = folder.list();

                for(String file : files)
                {
                    Log.println(Log.INFO,"deleting", "trying to delete " + path + "/" + file);
                    File about_to_delete = new File(path + "/" + file);
                    about_to_delete.delete();
                }

                folder.delete();

                delete_button.setEnabled(false);

            }


        });




    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */





    public void checkPermission() {


        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //stringFromJNI();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    public boolean tryOpenFile(Context context, String path) {
        Log.println(Log.ASSERT,"hey", path);
        File file = new File(path);
        boolean write = file.canWrite();
        boolean isFile = file.isFile();
        boolean exists = file.exists();
        return  file.exists();

    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
