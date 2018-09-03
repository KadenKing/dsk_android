package mo.bioinf.bmark;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.Scanner;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Context context = this;

        final Button done_button = (Button) findViewById(R.id.finished_button);
        final TextView tv = (TextView) findViewById(R.id.time_text);
        final TextView results_view = (TextView) findViewById(R.id.histogram);

        //set results text
        String results = getIntent().getStringExtra("runtime");
        String filename = getIntent().getStringExtra("filename");
        tv.setText(results);

        final String base_path = context.getFilesDir().getAbsolutePath().toString();
        File histo = new File(base_path + "/" + filename + ".histo");
//        if(histo.exists() && histo.canRead())
//        {
            try{
                Scanner scanner = new Scanner(histo);

                while(scanner.hasNextLine())
                {
                    results_view.append(scanner.nextLine() + "\n");
                }


            }catch(java.io.FileNotFoundException e)
            {
                results_view.setText("exception");
            }
//
//        }else{
//            results_view.setText("could not open");
//        }


        done_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
    }

}
