package mo.bioinf.bmark;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.sax.StartElementListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

public class ResultsActivity_deprecated extends AppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Context context = this;

        final Button done_button = (Button) findViewById(R.id.finished_button);
        final Button read_dna_button = (Button) findViewById(R.id.dna_button);
        final TextView tv = (TextView) findViewById(R.id.time_text);
        final TextView results_view = (TextView) findViewById(R.id.histogram);

        //set results text
        final String results = getIntent().getStringExtra("runtime");
        final String filename = getIntent().getStringExtra("filename");
        tv.setText(results);


        final Runnable read_dna_runner = new Runnable() {
            @Override
            public void run() {
                final String base_path = context.getFilesDir().getAbsolutePath().toString() + "/";
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


                /*** determines what the name of the histogram should be, opens it, and reads it into the view ***/

                //Map<String,String> dna_map = dna_output.getDna_map();

                results_view.append(dna_output.line_count + " lines written to file in " + runtime + " milliseconds");
//
//                for(Map.Entry<String,String> entry : dna_map.entrySet())
//                {
//                    results_view.append(entry.getKey() + " " + entry.getValue() + "\n");
//                }
                /****************************************************************************************************/
            }
        };

        final Handler handler = new Handler();


        read_dna_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                handler.post(read_dna_runner);

            }
        });

        done_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }




}
