#include <iostream>
#include <string>
#include <vector>
#include <limits>

#include "Production.h"
#include "Scanner.h"

using namespace std;



int main(int argc, char *argv[]) {
    string currentLine;
	int state = 0;
    Scanner scanner;
	Production currentProduction;
    vector<Production> productionList;
	while(true){
        currentLine = scanner.getNextLine();
        if (currentLine.empty()) {
            break;
        }
		switch (state) {
			case 0:	//Start state
				if (input >= 'A' && input <= 'Z') {
					state = 1;
					currentProduction.left = input;
					continue;
				} else {
					state = -2;
				}
				break;
			case 1:
				if (input == '-') {
					state = 2;
					continue;
				} else {
					state = -2;
				}
				break;
			case 2:
				if (input == '>') {
					state = 3;
					continue;
				} else {
					state = -2;
				}
				break;
			case 3:
				if ((input >= 'a' && input <= 'z') || (input >= 'A' && input <= 'Z')
					|| (input == '(') || (input == ')') || (input == '+') || (input == '-')
					|| (input == '*') || (input == '/')) {	//a-zA-Z()+-*/
					state = 4;
					//TODO
					continue;
				} else if (input == '\\') {
					state = 5;
					//TODO
					continue;
				} else {
					state = -2;
				}
				break;
			case 4:
				if ((input >= 'a' && input <= 'z') || (input >= 'A' && input <= 'Z')
					|| (input == '(') || (input == ')') || (input == '+') || (input == '-')
					|| (input == '*') || (input == '/')) {	//a-zA-Z()+-*/
					state = 4;
					//TODO
					continue;
				} else if (input == '\\') {
					state = 5;
					cout<<"Epsilon"<<endl;
					continue;
				} else if (input == '\n') {
					state = -1;
					//TODO
					continue;
				} else {
					state = -2;
				}
				break;
			case 5:
				if (input == 'e') {
					state = 4;
					currentProduction.right.push_back(130);
					continue;
				} else {
					state = -2;
				}
				break;
			case -1:	//End state
				cout<<"Grammar is legal"<<endl;
				break;
			default:	//Error state for -2
				cerr<<"Grammar is illegal!"<<endl;
				break;
		}
	}
	cout<<"over"<<endl;
}