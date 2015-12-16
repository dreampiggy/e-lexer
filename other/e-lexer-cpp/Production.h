//
// Created by lizhuoli on 15/12/5.
//
#ifndef E_LEXER_PRODUCTION_H
#define E_LEXER_PRODUCTION_H

#include <string>
#include <vector>
using namespace std;

class Production {
public:
    Production();

    char left;
    vector<char> right;
};


#endif //E_LEXER_PRODUCTION_H
