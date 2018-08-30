package mo.bioinf.bmark;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final EditText num_disk = (EditText) findViewById(R.id.num_disk);
        final EditText num_kmer = (EditText) findViewById(R.id.num_kmer);
        final EditText num_mem = (EditText) findViewById(R.id.num_memory);
        final Button done_button = (Button) findViewById(R.id.done_button);
        final Spinner minimizer_spinner = (Spinner) findViewById(R.id.minimizer_spinner);
        final Spinner repartition_spinner = (Spinner) findViewById(R.id.repartition_spinner);

        done_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int kmer = Integer.parseInt(num_kmer.getText().toString());
                int memory = Integer.parseInt(num_mem.getText().toString());
                int disk = Integer.parseInt(num_disk.getText().toString());
                String minimizerStr = minimizer_spinner.getSelectedItem().toString();
                String repartitionStr = repartition_spinner.getSelectedItem().toString();

                Intent result = new Intent();
                result.putExtra("kmer", kmer);
                result.putExtra("disk", disk);
                result.putExtra("memory", memory);
                result.putExtra("minimizer_type", minimizer2int(minimizerStr));
                result.putExtra("repartition_type",repartition2int(repartitionStr));
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });

    }

}
