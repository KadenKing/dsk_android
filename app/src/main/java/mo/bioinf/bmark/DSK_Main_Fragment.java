package mo.bioinf.bmark;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DSK_Main_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DSK_Main_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DSK_Main_Fragment extends Fragment {

    /**** Code that runs what the view of the fragment does ***/

    public void checkForSolids(String filename, String base_path){
        final Button delete_button = (Button) getView().findViewById(R.id.delete_button);

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
                getActivity(), android.R.layout.simple_spinner_item, fastq_file_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
    }


    /**************/



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DSK_Main_Fragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DSK_Main_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DSK_Main_Fragment newInstance(String param1, String param2) {
        DSK_Main_Fragment fragment = new DSK_Main_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.activity_main, container, false);


        final Context context = getActivity();


        /*** instances of ui widgets ***/
        final TextView tv = (TextView) rootView.findViewById(R.id.sample_text);
        final Button run = (Button) rootView.findViewById(R.id.run_button);
        final Spinner dropdown = (Spinner) rootView.findViewById(R.id.fastq_files);
        final ImageButton settings_button = (ImageButton) rootView.findViewById(R.id.settings_button);
        final Button delete_button = (Button) rootView.findViewById(R.id.delete_button);
        /*******************************/

        run.setEnabled(false); // enabled later when the file path is verified

        final String base_path = getActivity().getFilesDir().getAbsolutePath().toString() + "/fastq/"; // this phone's working directory
        DSK_Options.setDevicePath(getActivity().getFilesDir().getAbsolutePath().toString());
        DSK_Options.setFullPath(base_path);

        populateDropdown(getActivity(), dropdown, base_path); // fills the dropdown with the files in the fastq folder



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

        /*** open settings fragment ***/
        settings_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                //Intent myIntent = new Intent(getBaseContext(), SettingsActivity.class);
                //myIntent.putExtra("parcel",DSK_Options);

                //MainActivity.this.startActivityForResult(myIntent,1);

                Fragment fragment = new SettingsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                final TextView tv = (TextView) rootView.findViewById(R.id.sample_text);
                updateTV(tv, DSK_Options.getKmer(),DSK_Options.getMemory(), DSK_Options.getDisk(),DSK_Options.getFullPath(),
                        DSK_Options.getRepartition_type(), DSK_Options.getMinimizer_type());

            }
        });
        /***********************************/


        /*** run DSK ***/
        run.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                File solids_folder = new File(DSK_Options.getDevicePath() + "/" + DSK_Options.getFilename() + "_gatb");
                if(solids_folder.exists())
                {
//                    Intent results_intent = new Intent(getBaseContext(),ResultsActivity.class);
//                    results_intent.putExtra("runtime", "already run");
//                    results_intent.putExtra("filename", DSK_Options.getFilename());
//                    MainActivity.this.startActivity(results_intent);
                }else{
//                    Intent dskIntent = new Intent(getBaseContext(),DSKRunning.class);
//                    //dskIntent.putExtra("parcel",DSK_Options);
//
//                    MainActivity.this.startActivity(dskIntent);

                    Fragment fragment = new DSKRunningFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container,fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }





            }
        });
        /****************/


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void checkPermission() {


        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
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
