#include <algorithm>
#include <cctype>
#include <cerrno>
#include <chrono>
#include <climits>
#include <cmath>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <deque>
#include <fstream>
#include <iostream>
#include <map>
#include <queue>
#include <set>
#include <sstream>
#include <string>
#include <unordered_map>
#include <unordered_set>
#include <vector>
#include <err.h>
#include <fcntl.h>
#include <sysexits.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#include "stdio.h"
#include "omp_hack.h"

#if __ANDROID_API__ >= 23
extern FILE* stdin __INTRODUCED_IN(23);
extern FILE* stdout __INTRODUCED_IN(23);
extern FILE* stderr __INTRODUCED_IN(23);

/* C99 and earlier plus current C++ standards say these must be macros. */
#define stdin stdin
#define stdout stdout
#define stderr stderr
#else
/* Before M the actual symbols for stdin and friends had different names. */
extern FILE __sF[] __REMOVED_IN(23);

#define stdin (&__sF[0])
#define stdout (&__sF[1])
#define stderr (&__sF[2])
#endif

#ifndef SIZE_MAX
#define SIZE_MAX ((size_t) -1)
#endif

#ifndef UINT64_MAX
#define UINT64_MAX ((uint64_t) -1)
#endif
