cmake_minimum_required(VERSION 2.8.9)
project(Kraken)

add_subdirectory(${PROJECT_SOURCE_DIR}/classify)
add_subdirectory(${PROJECT_SOURCE_DIR}/dump_table)
add_subdirectory(${PROJECT_SOURCE_DIR}/estimate_capacity)
add_subdirectory(${PROJECT_SOURCE_DIR}/lookup_accession_numbers)