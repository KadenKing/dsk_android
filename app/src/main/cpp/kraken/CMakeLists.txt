cmake_minimum_required(VERSION 2.8.9)
project(Kraken)
SET(CMAKE_CXX_FLAGS "-fopenmp -std=c++0x")

file(GLOB CLASSIFY_SOURCES "src/*.cc")

add_library(Classify SHARED ${CLASSIFY_SOURCES})