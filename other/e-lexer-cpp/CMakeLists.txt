cmake_minimum_required(VERSION 3.3)
project(e_lexer)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.cpp Production.cpp Production.h Scanner.cpp Scanner.h)
add_executable(e_lexer ${SOURCE_FILES})