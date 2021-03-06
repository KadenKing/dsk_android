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
// We include required definitions
/********************************************************************************/

#include <DSK.hpp>
#include <android/log.h>

using namespace std;

/********************************************************************************/

struct Parameter
{
    Parameter (DSK& dsk, IProperties* props) : dsk(dsk), props(props) {}
    DSK&         dsk;
    IProperties* props;
};

/*********************************************************************
** METHOD  :
** PURPOSE :
** INPUT   :
** OUTPUT  :
** RETURN  :
** REMARKS :
*********************************************************************/
template<size_t span> struct Functor  {  void operator ()  (Parameter parameter)
{
    DSK&         dsk   = parameter.dsk;
    IProperties* props = parameter.props;

    /** We get a handle on tha bank. */
    IBank* bank = Bank::open(props->getStr("-file"));

    LOCAL (bank);

    /** We create a SortingCountAlgorithm instance. */
    SortingCountAlgorithm<span> sortingCount (bank, props);

    sortingCount.getInput()->add (0, STR_VERBOSE, props->getStr(STR_VERBOSE));
    __android_log_print(ANDROID_LOG_INFO,"bank","got here");
    /** We execute the algorithm. */
    try{
        sortingCount.execute();
    }catch(gatb::core::system::Exception e)
    {
        __android_log_print(ANDROID_LOG_INFO,"bank error",e.getMessage());
    }

    /** We collect statistics. */
    dsk.getInfo()->add (1, sortingCount.getConfig().getProperties());
    dsk.getInfo()->add (1, sortingCount.getInfo());


    /* save execution info into storage, same thing as Graph.cpp::executealgorithm would do */
    sortingCount.getStorage()->getGroup(sortingCount.getName()).setProperty("xml", string("\n") + sortingCount.getInfo()->getXML());


} };

/*********************************************************************
** METHOD  :
** PURPOSE :
** INPUT   :
** OUTPUT  :
** RETURN  :
** REMARKS :
*********************************************************************/
DSK::DSK () : Tool ("dsk")
{
    /** We add options specific to DSK (most important at the end). */
    getParser()->push_back (SortingCountAlgorithm<>::getOptionsParser(), 1);
    /** We rename the input option. */
    if (IOptionsParser* input = getParser()->getParser (STR_URI_INPUT))  {  input->setName (STR_URI_FILE);  }
    __android_log_print(ANDROID_LOG_INFO,"tool","got here");
}

/*********************************************************************
** METHOD  :
** PURPOSE :
** INPUT   :
** OUTPUT  :
** RETURN  :
** REMARKS :
*********************************************************************/
void DSK::execute ()
{
    __android_log_print(ANDROID_LOG_INFO,"execute","got here");
    /** we get the kmer size chosen by the end user. */
    size_t kmerSize = getInput()->getInt (STR_KMER_SIZE);

    /** We launch dsk with the correct Integer implementation according to the choosen kmer size. */
    Integer::apply<Functor,Parameter> (kmerSize, Parameter (*this, getInput()));
}
