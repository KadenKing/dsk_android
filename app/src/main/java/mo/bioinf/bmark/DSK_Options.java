package mo.bioinf.bmark;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class DSK_Options {

    static private int kmer = 31;
    static private int memory = 5000;
    static private int disk = 50000;
    static private String devicePath = "";
    static private String fullPath = "";
    static private int repartition_type = 0;
    static private int minimizer_type = 0;
    static private String filename = "";

    public DSK_Options(int kmer, int memory, int disk, int repartition_type, int minimizer_type){
        this.kmer = kmer;
        this.memory = memory;
        this.disk = disk;
        this.devicePath = devicePath;
        this.fullPath = fullPath;
        this.repartition_type = repartition_type;
        this.minimizer_type = minimizer_type;
        this.filename = filename;
    }

    public DSK_Options(){

    }

    public static String minimizer2string()
    {
        if(minimizer_type == 0)
            return "Lexicographic";
        if(minimizer_type == 1)
            return "Frequency";


        return "e";
    }

    public static String repartition2string()
    {
        if(repartition_type == 0)
            return "Unordered";
        if(repartition_type == 1)
            return "Ordered";

        return "e";
    }

//    public static final Creator<DSK_Options> CREATOR = new Creator<DSK_Options>() {
//        @Override
//        public DSK_Options createFromParcel(Parcel in) {
//            return new DSK_Options(in);
//        }
//
//        @Override
//        public DSK_Options[] newArray(int size) {
//            return new DSK_Options[size];
//        }
//    };
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeInt(kmer);
//        parcel.writeInt(memory);
//        parcel.writeInt(disk);
//        parcel.writeString(devicePath);
//        parcel.writeString(fullPath);
//        parcel.writeInt(repartition_type);
//        parcel.writeInt(minimizer_type);
//        parcel.writeString(filename);
//    }

//    public static DSK_Options(Parcel parcel){
//        kmer = parcel.readInt();
//        memory = parcel.readInt();
//        disk = parcel.readInt();
//        devicePath = parcel.readString();
//        fullPath = parcel.readString();
//        repartition_type = parcel.readInt();
//        minimizer_type = parcel.readInt();
//        filename = parcel.readString();
//    }

    public static int getKmer() {
        return kmer;
    }

    public static void setKmer(int kmer) {
        DSK_Options.kmer = kmer;
    }

    public static int getMemory() {
        return memory;
    }

    public static void setMemory(int memory) {
        DSK_Options.memory = memory;
    }

    public static int getDisk() {
        return disk;
    }

    public static void setDisk(int disk) {
        DSK_Options.disk = disk;
    }

    public static String getDevicePath() {
        return devicePath;
    }

    public static void setDevicePath(String devicePath) {
        DSK_Options.devicePath = devicePath;
    }

    public static String getFullPath() {
        return fullPath;
    }

    public static void setFullPath(String fullPath) {
        DSK_Options.fullPath = fullPath;
    }

    public static int getRepartition_type() {
        return repartition_type;
    }

    public static void setRepartition_type(int repartition_type) {
        DSK_Options.repartition_type = repartition_type;
    }

    public static int getMinimizer_type() {
        return minimizer_type;
    }

    public static void setMinimizer_type(int minimizer_type) {
        DSK_Options.minimizer_type = minimizer_type;
    }

    public static String getFilename() {
        return filename;
    }

    public static void setFilename(String filename) {
        DSK_Options.filename = filename;
    }

//    protected DSK_Options(Parcel in) {
//    }


//
//    @Override
//    public int describeContents() {
//        return 0;
//    }


}
