package mo.bioinf.bmark;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DSKRunning extends AppCompatActivity {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("dsk");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dskrunning);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        final DSK_Parcel parcel = getIntent().getParcelableExtra("parcel");


//        final String path = getIntent().getStringExtra("path");
//        final String base_path = getIntent().getStringExtra("base_path");
//        final String filename = getIntent().getStringExtra("filename");
//        final int kmer = getIntent().getIntExtra("kmer",-1);
//        final int memory = getIntent().getIntExtra("memory",-1);
//        final int disk = getIntent().getIntExtra("disk",-1);
//        final int repartition_type = getIntent().getIntExtra("repartition_type",-1);
//        final int minimizer_type = getIntent().getIntExtra("minimizer_type",-1);

        final Runnable DSK = new Runnable(){
            @Override
            public void run(){
                String runtime = stringFromJNI(parcel.getFullPath(),parcel.getDevicePath(), parcel.getFilename(), parcel.getKmer(),
                                                parcel.getMemory(),parcel.getDisk(),parcel.getRepartition_type(),parcel.getMinimizer_type());

                Intent results_intent = new Intent(getBaseContext(),ResultsActivity.class);
                results_intent.putExtra("runtime", runtime);
                results_intent.putExtra("filename", parcel.getFilename());
                DSKRunning.this.startActivity(results_intent);


                finish();
            }
        };

        final Handler dskHandler = new Handler();
        dskHandler.postDelayed(DSK,2000);



    }

    public native String stringFromJNI(String path, String base_path, String filename, int kmer, int memory, int disk, int repartition_type, int minimizer_type);


}
