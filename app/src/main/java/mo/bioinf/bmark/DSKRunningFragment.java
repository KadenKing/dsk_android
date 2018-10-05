package mo.bioinf.bmark;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DSKRunningFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DSKRunningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DSKRunningFragment extends Fragment {

    static {
        System.loadLibrary("dsk");
    }

    public native String stringFromJNI(String path, String base_path, String filename, int kmer, int memory, int disk, int repartition_type, int minimizer_type);


    public boolean solids_exist(){


        String pathStr = DSK_Options.getDevicePath() + "/" + DSK_Options.getFilename() + "_gatb";

        File file = new File(pathStr);

        if(file.exists())
        {
            return true;
        }else{
            return false;
        }

        //System.out.println(path);

    }

    private void next_fragment(Fragment input, Bundle bundle){
        Fragment fragment = input;
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DSKRunningFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DSKRunningFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DSKRunningFragment newInstance(String param1, String param2) {
        DSKRunningFragment fragment = new DSKRunningFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(solids_exist())
        {
            getFragmentManager().popBackStackImmediate();
            Log.println(Log.INFO, "running fragment", "found solids");
        }


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final Runnable DSK = new Runnable(){
            @Override
            public void run(){
                /*** sending the actual parsel to the jni is a nightmare so for now we're sending it piece by piece ***/
                String runtime = stringFromJNI(DSK_Options.getFullPath(),DSK_Options.getDevicePath(), DSK_Options.getFilename(), DSK_Options.getKmer(),
                        DSK_Options.getMemory(),DSK_Options.getDisk(),DSK_Options.getRepartition_type(),DSK_Options.getMinimizer_type());
                /******************************************************************************************************/

                /*** send the runtime and the filename to the results activity to show the results ***/
//                Intent results_intent = new Intent(getBaseContext(),ResultsActivity_deprecated.class);
//                results_intent.putExtra("runtime", runtime);
//                results_intent.putExtra("filename", DSK_Options.getFilename());
//                DSKRunning_deprecated.this.startActivity(results_intent);
                /*************************************************************************************/

                getFragmentManager().popBackStack();
                Bundle bundle = new Bundle();
                bundle.putString("runtime", runtime);

                next_fragment(new ResultsFragment(), bundle);






            }
        };

        /*** the java ui thread needs a chance to start before we run DSK ***/
        final Handler dskHandler = new Handler();
        dskHandler.postDelayed(DSK,2000);
        /*********************************************************************/



        return inflater.inflate(R.layout.activity_dskrunning, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


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



}
