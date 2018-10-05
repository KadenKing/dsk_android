package mo.bioinf.bmark;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {


    /*** ui instances ***/
    private View returnView = null;

    private EditText num_disk = null;
    private EditText num_kmer = null;
    private EditText num_mem = null;
    private Button done_button = null;
    private Spinner minimizer_spinner = null;
    private Spinner repartition_spinner = null;
    /*********************/

    private void initialize_ui_instances(LayoutInflater inflater, ViewGroup container){
        /*** input widget instances ****/
        returnView = inflater.inflate(R.layout.activity_settings, container, false);

         num_disk = (EditText) returnView.findViewById(R.id.num_disk);
         num_kmer = (EditText) returnView.findViewById(R.id.num_kmer);
         num_mem = (EditText) returnView.findViewById(R.id.num_memory);
         done_button = (Button) returnView.findViewById(R.id.done_button);
         minimizer_spinner = (Spinner) returnView.findViewById(R.id.minimizer_spinner);
         repartition_spinner = (Spinner) returnView.findViewById(R.id.repartition_spinner);
        /*******************************/
    }

    private int minimizer2int(String input)
    {
        if(input.equals("Lexicographic"))
            return 0;
        if(input.equals("Frequency"))
            return 1;

        return -1;
    }

    private int repartition2int(String input)
    {
        if(input.equals("Unordered"))
            return 0;
        if(input.equals("Ordered"))
            return 1;

        return -1;
    }

    private void set_ui_to_current_values(){
        /*** set the input fields to their current values ***/
        num_kmer.setText(String.valueOf(DSK_Options.getKmer()));
        num_disk.setText(String.valueOf(DSK_Options.getDisk()));
        num_mem.setText(String.valueOf(DSK_Options.getMemory()));
        minimizer_spinner.setSelection(DSK_Options.getMinimizer_type());
        repartition_spinner.setSelection(DSK_Options.getRepartition_type());
        /****************************************************/
    }

    private void initialize_done_button(){
        /*** send settings information back to MainActivity_deprecated ***/
        done_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int kmer = Integer.parseInt(num_kmer.getText().toString());
                int memory = Integer.parseInt(num_mem.getText().toString());
                int disk = Integer.parseInt(num_disk.getText().toString());
                String minimizerStr = minimizer_spinner.getSelectedItem().toString();
                String repartitionStr = repartition_spinner.getSelectedItem().toString();

                //DSK_Options returnParcel = new DSK_Options(kmer,memory,disk,repartition2int(repartitionStr),minimizer2int(minimizerStr));
                DSK_Options.setKmer(kmer);
                DSK_Options.setMemory(memory);
                DSK_Options.setDisk(disk);
                DSK_Options.setMinimizer_type(minimizer2int(minimizerStr));
                DSK_Options.setRepartition_type(repartition2int(repartitionStr));

//                Intent result = new Intent();
//
//                //result.putExtra("returnParcel",returnParcel);
//                setResult(Activity.RESULT_OK, result);
//                finish();
                getFragmentManager().popBackStackImmediate();
            }
        });
        /************************************************************/
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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



        initialize_ui_instances(inflater,container);

        set_ui_to_current_values();

        initialize_done_button();


        return returnView;
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
}
