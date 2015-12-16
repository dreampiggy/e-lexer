//
//  main.swift
//  e-lexer
//
//  Created by lizhuoli on 15/12/5.
//  Copyright © 2015年 lizhuoli. All rights reserved.
//

import Foundation


func main(){
    let argv = Process.arguments
    var path = ""
    if argv.count < 2 {
        path = "./input.l"
    } else {
        path = argv[1]
    }
    
    var state = 0
    let scanner = Scanner(path: path)
    var currentProduction = Production()
    var productionList:[Production] = []
    
    while(true){
        guard let input = scanner.getNextChar() else {
            if let _ = scanner.getNextLine() {
                continue
            } else {    //End state
                print("Grammar is legal")
                break
            }
        }
        print("Current \(input)")
        switch (state) {
        case 0:	//Start state
            if (input >= "A" && input <= "Z") {
                state = 1
                currentProduction.left = Symbol(value: input, type: .NonTerminal)
                continue
            } else {
                Error.lexError(scanner.getLineNum())
            }
            
        case 1:
            if (input == "-") {
                state = 2
                continue
            } else {
                Error.lexError(scanner.getLineNum())
            }
            
        case 2:
            if (input == ">") {
                state = 3
                continue
            } else {
                Error.lexError(scanner.getLineNum())
            }
            
        case 3:
            if ((input >= "a" && input <= "z") || (input == "(") || (input == ")")
                || (input == "+") || (input == "-")
                || (input == "*") || (input == "/")) {	//a-zA-Z()+-*/
                    state = 4
                    currentProduction.right.append(Symbol(value: input, type: .Terminal))
                    continue
            } else if (input >= "A" && input <= "Z") {
                state = 4
                currentProduction.right.append(Symbol(value: input, type: .NonTerminal))
                continue
            } else if (input == "\\") {
                state = 5
                //TODO
                continue
            } else {
                Error.lexError(scanner.getLineNum())
            }
        case 4:
            if ((input >= "a" && input <= "z") || (input >= "A" && input <= "Z")
                || (input == "(") || (input == ")") || (input == "+") || (input == "-")
                || (input == "*") || (input == "/")) {	//a-zA-Z()+-*/
                    state = 4
                    currentProduction.right.append(Symbol(value: input, type: .Terminal))
                    continue
            } else if (input == "\\") {
                state = 5
                //TODO
                continue
            } else if (input == "|") {
                state = 4
                let newProduction = Production()
                productionList.append(currentProduction)
                newProduction.left = currentProduction.left
                currentProduction = newProduction
                continue
            } else if (input == "\n") {
                state = 0
                productionList.append(currentProduction)
                currentProduction = Production()
                print(productionList[0].left)
                continue
            } else {
                Error.lexError(scanner.getLineNum())
            }
            
        case 5:
            if (input == "e") {
                state = 4
                currentProduction.right.append(Symbol(value: "ε", type: .Terminal))
                continue
            } else {
                Error.lexError(scanner.getLineNum())
            }
            
        default:	//Error state for -2
            print("Grammar is illegal!")
            exit(0)
        }
    }
    for p in productionList {
        print("Production: left: \(p.left), right: \(p.right)")
    }
}


main()