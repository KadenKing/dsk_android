/*****************************************************************************
 *   GATB : Genome Assembly Tool Box
 *   Copyright (C) 2014  INRIA
 *   Authors: R.Chikhi, G.Rizk, E.Drezen
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*****************************************************************************/

/********************************************************************************/

#include <DSK.hpp>
#include <jni.h>
#include <string>
#include <android/log.h>
#include <chrono>


using namespace std;

/********************************************************************************/

//int main (int argc, char* argv[])
//{
//    // We define a try/catch block in case some method fails (bad filename for instance)
//    try
//    {
//        /** We execute dsk. */
//        DSK().run (argc, argv);
//    }
//
//    catch (OptionFailure& e)
//    {
//        return e.displayErrors (std::cout);
//    }
//
//    catch (Exception& e)
//    {
//        cerr << "EXCEPTION: " << e.getMessage() << endl;
//        return EXIT_FAILURE;
//    }
//
//    return EXIT_SUCCESS;
//}



extern "C"
JNIEXPORT jstring JNICALL
Java_mo_bioinf_bmark_DSKRunningFragment_stringFromJNI(JNIEnv *env, jobject instance, jstring path, jstring base_path,jstring filename, jint kmer, jint memory, jint disk, jint repartition_type, jint minimizer_type) {

    /***converting jstrings to c++ strings*****/
    const char *str = (*env).GetStringUTFChars(path,0);
    std::string javaPath = str;
    const char *bstr = (*env).GetStringUTFChars(base_path,0);
    std::string javaBasePath = bstr;
    /*******************************************/


    /****convert all inputs to strings so they can be put into a char pointer array *****/
    std::string outpath = javaBasePath.append("/").append((*env).GetStringUTFChars(filename,0));
    string strK, strM, strD, strRepartition, strMinimizer;
    strK = to_string(kmer);
    strM = to_string(memory);
    strD = to_string(disk);
    strRepartition = to_string(repartition_type);
    strMinimizer = to_string(minimizer_type);
    /**************************************************************************************/


    /***** debug logs *******/
    __android_log_print(ANDROID_LOG_INFO,"file",javaPath.c_str());
    __android_log_print(ANDROID_LOG_INFO,"base_path",&javaBasePath[0u]);
    __android_log_print(ANDROID_LOG_INFO,"kmer",&strK[0u]);
    __android_log_print(ANDROID_LOG_INFO,"mem",&strM[0u]);
    __android_log_print(ANDROID_LOG_INFO,"disk",&strD[0u]);
    __android_log_print(ANDROID_LOG_INFO,"repartition",&strRepartition[0u]);
    __android_log_print(ANDROID_LOG_INFO,"minimizer",&strMinimizer[0u]);
    /**********************/

    /**** create a arguments array that will be parsed by gatb's argument parser.i'll probably change this later  ****/
    char *argv[] = {"./dsk", "-file", &javaPath[0u], "-kmer-size",  &strK[0u],"-out", &outpath[0u],
                    "-out-tmp", &javaBasePath[0u], "-out-dir", &javaBasePath[0u], "-max-memory", &strM[0u],
                    "-max-disk", &strD[0u], "-minimizer-type", &strRepartition[0u], "-minimizer-type", &strMinimizer[0u]};
    /******************************************************************************************************************/



    string strDuration;
    try{
        /**** runs dsk with the arguments, and keeps track of how long it takes ****/
        chrono::high_resolution_clock::time_point t1 = chrono::high_resolution_clock::now();
        DSK().run(19,argv);
        chrono::high_resolution_clock::time_point t2 = chrono::high_resolution_clock::now();
        auto duration = chrono::duration_cast<chrono::seconds>( t2 - t1 ).count();
        strDuration = to_string(duration);
        /**************************************************************************/

    }catch(OptionFailure& e)
    {

        __android_log_print(ANDROID_LOG_INFO,"Exception", "caught");
    }





    const char *charDuration = strDuration.c_str();

    __android_log_print(ANDROID_LOG_INFO,"finish", "finished");

    string answer = "This fastq took ";
    answer.append(strDuration);
    answer.append(" seconds.");

    return env->NewStringUTF(answer.c_str());
}

