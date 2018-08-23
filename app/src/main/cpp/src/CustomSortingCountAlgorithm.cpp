//
// Created by kaden on 8/22/18.
//

#include "CustomSortingCountAlgorithm.hpp"




template<size_t span>
IOptionsParser* CustomSortingCountAlgorithm<span>::getOptionsParser (bool mandatory)
{
    IOptionsParser* parser = new OptionsParser ("kmer count");
    //__android_log_print(ANDROID_LOG_INFO,"sorting algorithm","got here");
    string abundanceMax = Stringify::format("%ld", std::numeric_limits<CountNumber>::max());

    //parser->push_back (new OptionOneParam (STR_URI_INPUT,         "reads file", mandatory ));

    parser->push_back (new OptionOneParam (STR_URI_INPUT,         "reads file", false, "/data/user/0/mo.bioinf.bmark/files/fastq/test1.fastq"));
    parser->push_back (new OptionOneParam (STR_KMER_SIZE,         "size of a kmer",                                 false, "31"    ));
    parser->push_back (new OptionOneParam (STR_KMER_ABUNDANCE_MIN,"min abundance threshold for solid kmers",        false, "2"     ));
    parser->push_back (new OptionOneParam (STR_KMER_ABUNDANCE_MAX,"max abundance threshold for solid kmers",        false, abundanceMax));
    parser->push_back (new OptionOneParam (STR_KMER_ABUNDANCE_MIN_THRESHOLD,"min abundance hard threshold (only used when min abundance is \"auto\")",false, "2"));
    parser->push_back (new OptionOneParam (STR_HISTOGRAM_MAX,     "max number of values in kmers histogram",        false, "10000"));
    parser->push_back (new OptionOneParam (STR_SOLIDITY_KIND,     "way to compute counts of several files (sum, min, max, one, all, custom)",false, "sum"));
    parser->push_back (new OptionOneParam (STR_SOLIDITY_CUSTOM,   "when solidity-kind is cutom, specifies list of files where kmer must be present",false, ""));
    parser->push_back (new OptionOneParam (STR_MAX_MEMORY,        "max memory (in MBytes)",                         false, "2000"));
    parser->push_back (new OptionOneParam (STR_MAX_DISK,          "max disk   (in MBytes)",                         false, "20000"));
    parser->push_back (new OptionOneParam (STR_URI_SOLID_KMERS,   "output file for solid kmers (only when constructing a graph)", false, "/data/user/0/mo.bioinf.bmark/files"));
    parser->push_back (new OptionOneParam (STR_URI_OUTPUT,        "output file",                                    false));
    parser->push_back (new OptionOneParam (STR_URI_OUTPUT_DIR,    "output directory",                               false, "/data/user/0/mo.bioinf.bmark/files"));
    parser->push_back (new OptionOneParam (STR_URI_OUTPUT_TMP,    "output directory for temporary files",           false, "/data/user/0/mo.bioinf.bmark/files"));
    parser->push_back (new OptionOneParam (STR_COMPRESS_LEVEL,    "h5 compression level (0:none, 9:best)",          false, "0"));
    parser->push_back (new OptionOneParam (STR_STORAGE_TYPE,      "storage type of kmer counts ('hdf5' or 'file')", false, "file"  ));


    IOptionsParser* devParser = new OptionsParser ("kmer count, algorithmic options");

    devParser->push_back (new OptionOneParam (STR_MINIMIZER_TYPE,    "minimizer type (0=lexi, 1=freq)",                false, "1"));
    devParser->push_back (new OptionOneParam (STR_MINIMIZER_SIZE,    "size of a minimizer",                            false, "10"));
    devParser->push_back (new OptionOneParam (STR_REPARTITION_TYPE,  "minimizer repartition (0=unordered, 1=ordered)", false, "1"));
    parser->push_back (devParser);

    return parser;
}
