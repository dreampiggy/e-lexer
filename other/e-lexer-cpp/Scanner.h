//
// Created by lizhuoli on 15/12/5.
//
#ifndef E_LEXER_SCANNER_H
#define E_LEXER_SCANNER_H


#include <fstream>
using namespace std;

class Scanner {
public:
    Scanner();
    string getNextLine();
    char getNextChar();
    ~Scanner();

private:
    ifstream file;
    string line;
    char*
};


#endif //E_LEXER_SCANNER_H
