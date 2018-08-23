//
// Created by kaden on 8/22/18.
//

#ifndef DSK_ANDROID_CUSTOMSORTINGCOUNTALGORITHM_H
#define DSK_ANDROID_CUSTOMSORTINGCOUNTALGORITHM_H

#include <gatb/tools/misc/impl/Algorithm.hpp>
#include <gatb/bank/api/IBank.hpp>
#include <gatb/kmer/api/ICountProcessor.hpp>
#include <gatb/kmer/impl/Model.hpp>
#include <gatb/kmer/impl/BankKmers.hpp>
#include <gatb/kmer/impl/Configuration.hpp>
#include <gatb/kmer/impl/PartiInfo.hpp>
#include <gatb/tools/storage/impl/Storage.hpp>
#include <gatb/gatb_core.hpp>
#include <string>

/********************************************************************************/
namespace gatb      {
    namespace core      {
/** \brief Package for genomic databases management. */
        namespace kmer      {
/** \brief Implementation for genomic databases management. */
            namespace impl      {
/********************************************************************************/

/** \brief Class performing the kmer counting (also known as 'DSK')
 *
 * This class does the real job of counting the kmers from a reads database.
 *
 * This is a template class whose template argument is the kind of integer used for
 * kmers (integers on 64 bits, 128 bits, etc...)
 *
 * We define some template instantiations of this SortingCountAlgorithm; such an instantiation
 * does the real job of kmers counting. By defining several instantiations, we allow
 * to choose dynamically the correct class according to the user choice for kmer size
 * (remember that initial Minia version had to be re-compiled for different kmer size).
 *
 * Actually, this class is mainly used in the debruijn::impl::Graph class as a first step for
 * the de Bruijn graph creation.
 */
template<size_t span=KMER_DEFAULT_SPAN>
class CustomSortingCountAlgorithm : public gatb::core::kmer::impl::SortingCountAlgorithm<>
{
    static tools::misc::IOptionsParser* getOptionsParser (bool mandatory=true);

};


} } } } /* end of namespaces. */


#endif //DSK_ANDROID_CUSTOMSORTINGCOUNTALGORITHM_H
