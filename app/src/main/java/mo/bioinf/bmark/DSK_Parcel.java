package mo.bioinf.bmark;

import android.os.Parcel;
import android.os.Parcelable;

public class DSK_Parcel implements Parcelable{

    private int kmer = 31;
    private int memory = 5000;
    private int disk = 50000;
    private String devicePath = "";
    private String fullPath = "";
    private int repartition_type = 0;
    private int minimizer_type = 0;
    private String filename = "";

    public DSK_Parcel(int kmer, int memory, int disk, int repartition_type, int minimizer_type){
        this.kmer = kmer;
        this.memory = memory;
        this.disk = disk;
        this.devicePath = devicePath;
        this.fullPath = fullPath;
        this.repartition_type = repartition_type;
        this.minimizer_type = minimizer_type;
        this.filename = filename;
    }

    public DSK_Parcel(){

    }

    public String minimizer2string()
    {
        if(minimizer_type == 0)
            return "Lexicographic";
        if(minimizer_type == 1)
            return "Frequency";


        return "e";
    }

    public String repartition2string()
    {
        if(repartition_type == 0)
            return "Unordered";
        if(repartition_type == 1)
            return "Ordered";

        return "e";
    }

    public static final Creator<DSK_Parcel> CREATOR = new Creator<DSK_Parcel>() {
        @Override
        public DSK_Parcel createFromParcel(Parcel in) {
            return new DSK_Parcel(in);
        }

        @Override
        public DSK_Parcel[] newArray(int size) {
            return new DSK_Parcel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(kmer);
        parcel.writeInt(memory);
        parcel.writeInt(disk);
        parcel.writeString(devicePath);
        parcel.writeString(fullPath);
        parcel.writeInt(repartition_type);
        parcel.writeInt(minimizer_type);
        parcel.writeString(filename);
    }

    public DSK_Parcel(Parcel parcel){
        kmer = parcel.readInt();
        memory = parcel.readInt();
        disk = parcel.readInt();
        devicePath = parcel.readString();
        fullPath = parcel.readString();
        repartition_type = parcel.readInt();
        minimizer_type = parcel.readInt();
        filename = parcel.readString();
    }

    public int getKmer() {
        return kmer;
    }

    public void setKmer(int kmer) {
        this.kmer = kmer;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getDisk() {
        return disk;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    public String getDevicePath() {
        return devicePath;
    }

    public void setDevicePath(String devicePath) {
        this.devicePath = devicePath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public int getRepartition_type() {
        return repartition_type;
    }

    public void setRepartition_type(int repartition_type) {
        this.repartition_type = repartition_type;
    }

    public int getMinimizer_type() {
        return minimizer_type;
    }

    public void setMinimizer_type(int minimizer_type) {
        this.minimizer_type = minimizer_type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

//    protected DSK_Parcel(Parcel in) {
//    }



    @Override
    public int describeContents() {
        return 0;
    }


}
