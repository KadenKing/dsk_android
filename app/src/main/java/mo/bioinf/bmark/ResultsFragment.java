package mo.bioinf.bmark;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultsFragment newInstance(String param1, String param2) {
        ResultsFragment fragment = new ResultsFragment();
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

        final View returnView = inflater.inflate(R.layout.activity_results, container, false);;

        /**** ui element instances ***/
        final Button done_button = (Button) returnView.findViewById(R.id.finished_button);
        final Button read_dna_button = (Button) returnView.findViewById(R.id.dna_button);
        final TextView tv = (TextView) returnView.findViewById(R.id.time_text);
        final TextView results_view = (TextView) returnView.findViewById(R.id.histogram);

        //set results text
        final String results = getArguments().getString("runtime");
        final String filename = DSK_Options.getFilename();
        /***********************************************/
        tv.setText(results);


        done_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                getFragmentManager().popBackStack();
            }
        });

        final Runnable read_dna_runner = new Runnable() {
            @Override
            public void run() {
                final String base_path = getActivity().getFilesDir().getAbsolutePath().toString() + "/";
                /*** making dna output ***/
                DnaOutput dna_output = null;
                long runtime = 0;
                try{
                    long startTime = System.nanoTime();
                    Log.println(Log.INFO,"filename", filename);
                    Log.println(Log.INFO,"base_path", base_path);
                    dna_output = new DnaOutput(filename, base_path,true);
                    long endTime = System.nanoTime();
                    runtime = endTime - startTime;
                    runtime /= 1000000;


                }catch(java.io.FileNotFoundException e){
                    System.out.println(e.getMessage());
                }

                /************************/


                /*** puts some debug information about the dna decompression into the view ***/

                results_view.append(dna_output.line_count + " lines written to file in " + runtime + " milliseconds");

                /****************************************************************************************************/
            }
        };

        final Handler handler = new Handler();

        read_dna_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                handler.post(read_dna_runner);

            }
        });

        return returnView;
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
