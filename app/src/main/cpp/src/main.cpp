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
Java_mo_bioinf_bmark_MainActivity_stringFromJNI(JNIEnv *env, jobject instance) {

    char **argv;
//    argv[0] = "-file";
//    argv[1] = "/sdcard/Download/test.fastq";

    //char* args[] = {"./dsk","-file", "/sdcard/Download/test.fastq"};

    try{
        DSK().run(1,argv);
    }catch(OptionFailure& e)
    {

        __android_log_print(ANDROID_LOG_INFO,"Exception", "caught");
    }

    std::string hello = "Howdy from DSK";
    __android_log_print(ANDROID_LOG_INFO,"test","main");






    //std::string hello = "Howdy from DSK";
    return env->NewStringUTF("howdy from dsk");
}

