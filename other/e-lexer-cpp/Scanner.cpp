//
// Created by lizhuoli on 15/12/5.
//

#include "Scanner.h"
#include <iostream>
#include <fstream>
#include <string>

using namespace std;

Scanner::Scanner() {
    file = ifstream("./input.l");
    if (file.is_open()) {
        cout<<"Success to open .l file"<<endl;
    }
}

string Scanner::getNextLine() {
    if (getline(file, line)) {
        return line;
    } else {
        return nullptr;
    }
}

char Scanner::getNextChar() {
    
}

Scanner::~Scanner() {
    file.close();
}
