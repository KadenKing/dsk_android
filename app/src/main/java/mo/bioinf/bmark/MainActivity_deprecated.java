package mo.bioinf.bmark;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity_deprecated extends AppCompatActivity {

    //private DSK_Options parcel = new DSK_Options();



    public void checkForSolids(String filename, String base_path){
        final Button delete_button = (Button) findViewById(R.id.delete_button);

        String pathStr = DSK_Options.getDevicePath() + "/" + DSK_Options.getFilename() + "_gatb";

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
                        "repartition type: " + DSK_Options.repartition2string() + "\n" +
                        "minimizer type: " + DSK_Options.minimizer2string() + "\n";
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



            final TextView tv = (TextView) findViewById(R.id.sample_text);
            updateTV(tv, DSK_Options.getKmer(),DSK_Options.getMemory(), DSK_Options.getDisk(),DSK_Options.getFullPath(),
                    DSK_Options.getRepartition_type(), DSK_Options.getMinimizer_type());
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
        DSK_Options.setDevicePath(context.getFilesDir().getAbsolutePath().toString());
        DSK_Options.setFullPath(base_path);

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
                DSK_Options.setFilename(selectedItem);
                DSK_Options.setFullPath(base_path  + selectedItem);
                updateTV(tv, DSK_Options.getKmer(),DSK_Options.getMemory(), DSK_Options.getDisk(),DSK_Options.getFullPath(),
                        DSK_Options.getRepartition_type(), DSK_Options.getMinimizer_type());


                checkForSolids(selectedItem,base_path);
                /*makes sure that if we press the run button the app won't crash due to a file permission error*/
                checkPermission();
                if (tryOpenFile(context,DSK_Options.getFullPath()) && isExternalStorageReadable() && isExternalStorageWritable()) {
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

                Intent myIntent = new Intent(getBaseContext(), SettingsActivity_deprecated.class);
                //myIntent.putExtra("parcel",DSK_Options);

                MainActivity_deprecated.this.startActivityForResult(myIntent,1);



            }
        });
        /***********************************/

        /*** run DSK ***/
        run.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                File solids_folder = new File(DSK_Options.getDevicePath() + "/" + DSK_Options.getFilename() + "_gatb");
                if(solids_folder.exists())
                {
                    Intent results_intent = new Intent(getBaseContext(),ResultsActivity_deprecated.class);
                    results_intent.putExtra("runtime", "already run");
                    results_intent.putExtra("filename", DSK_Options.getFilename());
                    MainActivity_deprecated.this.startActivity(results_intent);
                }else{
                    Intent dskIntent = new Intent(getBaseContext(),DSKRunning.class);
                    //dskIntent.putExtra("parcel",DSK_Options);

                    MainActivity_deprecated.this.startActivity(dskIntent);
                }





            }
        });
        /****************/

        delete_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                String filename = DSK_Options.getFilename();
                String device_path = DSK_Options.getDevicePath();
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


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


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
